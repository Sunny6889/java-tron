package org.tron.core.services.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletMapping;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.tron.core.services.interfaceOnPBFT.HttpApiOnPBFTService;
import org.tron.core.services.interfaceOnSolidity.HttpApiOnSolidityService;

public class HttpApiDefTest {

  @Test
  public void testTableInvariantsHold() {
    // must not throw: unique suffixes, non-READ rows are FULL-only
    HttpApiDef.validate();
    for (HttpApiDef def : HttpApiDef.values()) {
      if (def.getAccess() != HttpApiDef.Access.READ) {
        Assert.assertEquals(def.name() + " must be FULL-only",
            new HashSet<>(Arrays.asList(HttpApiDef.Surface.FULL)),
            surfacesOf(def));
      }
      Assert.assertFalse(def.name() + " suffix must not contain '/'",
          def.getSuffix().contains("/"));
    }
  }

  @Test
  public void testEveryServletIsASpringComponent() {
    // registry mounting resolves servlets from the context; a non-bean class fails the boot
    for (HttpApiDef def : HttpApiDef.values()) {
      Assert.assertTrue(def.getServlet().getName() + " must be annotated with @Component",
          def.getServlet().isAnnotationPresent(Component.class));
    }
  }

  @Test
  public void testSurfaceRowCounts() {
    Assert.assertEquals(120, HttpApiDef.values().length);
    Assert.assertEquals(120, HttpApiDef.forSurface(HttpApiDef.Surface.FULL).size());
    Assert.assertEquals(44, HttpApiDef.forSurface(HttpApiDef.Surface.SOLIDITY).size());
    Assert.assertEquals(42, HttpApiDef.forSurface(HttpApiDef.Surface.PBFT).size());
    Assert.assertEquals(44, HttpApiDef.forSurface(HttpApiDef.Surface.SOLIDITY_NODE).size());
  }

  @Test
  public void testFullNodeServiceMountsExactlyTheRegistry() throws Exception {
    Set<String> expected = pathsOf(HttpApiDef.Surface.FULL, "/wallet/");
    expected.add("/net/listnodes");
    expected.add("/monitor/getstatsinfo");
    expected.add("/monitor/getnodeinfo");
    Assert.assertEquals(expected, mountedPaths(FullNodeHttpApiService.class));
  }

  @Test
  public void testSolidityServiceMountsExactlyTheRegistry() throws Exception {
    Set<String> expected = pathsOf(HttpApiDef.Surface.SOLIDITY, "/walletsolidity/");
    expected.add("/wallet/getnodeinfo");
    Assert.assertEquals(expected, mountedPaths(HttpApiOnSolidityService.class));
  }

  @Test
  public void testPbftServiceMountsExactlyTheRegistry() throws Exception {
    Set<String> expected = pathsOf(HttpApiDef.Surface.PBFT, "/");
    Assert.assertEquals(expected, mountedPaths(HttpApiOnPBFTService.class));
  }

  @Test
  public void testSolidityNodeServiceMountsExactlyTheRegistry() throws Exception {
    Set<String> expected = pathsOf(HttpApiDef.Surface.SOLIDITY_NODE, "/walletsolidity/");
    expected.add("/wallet/getnodeinfo");
    Assert.assertEquals(expected, mountedPaths(SolidityNodeHttpApiService.class));
  }

  private static Set<String> pathsOf(HttpApiDef.Surface surface, String prefix) {
    Set<String> paths = new HashSet<>();
    for (HttpApiDef def : HttpApiDef.forSurface(surface)) {
      paths.add(prefix + def.getSuffix());
    }
    return paths;
  }

  private static Set<HttpApiDef.Surface> surfacesOf(HttpApiDef def) {
    Set<HttpApiDef.Surface> surfaces = new HashSet<>();
    for (HttpApiDef.Surface surface : HttpApiDef.Surface.values()) {
      if (HttpApiDef.forSurface(surface).contains(def)) {
        surfaces.add(surface);
      }
    }
    return surfaces;
  }

  /**
   * Instantiates the service without running its constructor, injects a mock application
   * context whose beans are mocks, runs the registry-driven registration against a real
   * jetty context and returns every mounted path spec.
   */
  private static Set<String> mountedPaths(Class<?> serviceClass) throws Exception {
    ApplicationContext ctx = mock(ApplicationContext.class);
    given(ctx.getBean(any(Class.class))).willAnswer(inv -> mock((Class<?>) inv.getArgument(0)));

    Object service = mock(serviceClass,
        withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
    Field appContext = serviceClass.getDeclaredField("appContext");
    appContext.setAccessible(true);
    appContext.set(service, ctx);

    ServletContextHandler context = new ServletContextHandler();
    Method register = serviceClass
        .getDeclaredMethod("addServletsFromRegistry", ServletContextHandler.class);
    register.setAccessible(true);
    register.invoke(service, context);

    Set<String> mounted = new HashSet<>();
    for (ServletMapping mapping : context.getServletHandler().getServletMappings()) {
      mounted.addAll(Arrays.asList(mapping.getPathSpecs()));
    }
    return mounted;
  }
}
