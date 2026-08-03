package org.tron.core.services.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.tron.core.db.Manager;
import org.tron.core.db2.core.Chainbase;

public class WalletCursorFilterTest {

  private final Manager dbManager = mock(Manager.class);
  private final ServletRequest request = mock(ServletRequest.class);
  private final ServletResponse response = mock(ServletResponse.class);

  @Test
  public void testSolidityFilterSetsAndResetsCursorAroundChain() throws Exception {
    FilterChain chain = mock(FilterChain.class);
    SolidityCursorFilter filter = new SolidityCursorFilter(dbManager);

    filter.doFilter(request, response, chain);

    InOrder order = inOrder(dbManager, chain);
    order.verify(dbManager).setCursor(Chainbase.Cursor.SOLIDITY);
    order.verify(chain).doFilter(request, response);
    order.verify(dbManager).resetCursor();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testPbftFilterSetsAndResetsCursorAroundChain() throws Exception {
    FilterChain chain = mock(FilterChain.class);
    PbftCursorFilter filter = new PbftCursorFilter(dbManager);

    filter.doFilter(request, response, chain);

    InOrder order = inOrder(dbManager, chain);
    order.verify(dbManager).setCursor(Chainbase.Cursor.PBFT);
    order.verify(chain).doFilter(request, response);
    order.verify(dbManager).resetCursor();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testCursorResetEvenIfChainThrows() throws Exception {
    FilterChain chain = mock(FilterChain.class);
    doThrow(new ServletException("boom")).when(chain).doFilter(any(), any());
    SolidityCursorFilter filter = new SolidityCursorFilter(dbManager);

    try {
      filter.doFilter(request, response, chain);
      Assert.fail("expected ServletException");
    } catch (ServletException expected) {
      Assert.assertEquals("boom", expected.getMessage());
    }

    verify(dbManager).setCursor(Chainbase.Cursor.SOLIDITY);
    verify(dbManager).resetCursor();
  }
}
