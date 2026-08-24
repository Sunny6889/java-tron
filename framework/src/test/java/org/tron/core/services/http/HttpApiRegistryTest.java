package org.tron.core.services.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Inherited;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletMapping;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.tron.core.services.http.HttpApi.Access;
import org.tron.core.services.http.HttpApi.Surface;
import org.tron.core.services.http.solidity.SolidityNodeHttpApiService;
import org.tron.core.services.interfaceOnPBFT.HttpApiOnPBFTService;
import org.tron.core.services.interfaceOnSolidity.HttpApiOnSolidityService;

public class HttpApiRegistryTest {

  private static final String BASELINE = "/http/pre-refactor-routes.txt";

  private static final String REGTEST = "org.tron.core.services.http.regtest.";

  /**
   * Endpoints intentionally added to a surface since the pre-refactor baseline, keyed by the
   * commit that added them. Everything else must match the baseline exactly; this map is the
   * only place a deliberate exposure change is recorded.
   */
  private static final Map<Surface, Set<String>> INTENTIONAL_ADDED = new EnumMap<>(Surface.class);

  static {
    // 849c29e73a feat(http): expose two solidity read endpoints on pbft surface
    INTENTIONAL_ADDED.put(Surface.PBFT, new HashSet<>(
        Arrays.asList("getpaginatednowwitnesslist", "gettransactioninfobyblocknum")));
  }

  @Test
  public void testRegistryBuildsAndValidates() {
    // touching the registry builds and validates the whole table; an invalid table throws here
    Assert.assertFalse("registry must not be empty", HttpApiRegistry.all().isEmpty());
  }

  @Test
  public void testAnnotationsAreNotInheritable() {
    // a subclass must never inherit its parent's exposure — see the removed cursor wrappers
    Assert.assertFalse("@HttpApi must not be @Inherited",
        HttpApi.class.isAnnotationPresent(Inherited.class));
    Assert.assertFalse("@HttpApiExcluded must not be @Inherited",
        HttpApiExcluded.class.isAnnotationPresent(Inherited.class));
  }

  @Test
  public void testNonReadIsFullOnly() {
    for (HttpApiRegistry.Entry entry : HttpApiRegistry.all()) {
      if (entry.getAccess() != Access.READ) {
        Assert.assertEquals(entry.getSuffix() + " must be FULL-only",
            new HashSet<>(Arrays.asList(Surface.FULL)), entry.getSurfaces());
      }
      Assert.assertFalse(entry.getSuffix() + " must not contain '/'",
          entry.getSuffix().contains("/"));
    }
  }

  @Test
  public void testEveryHttpApiServletIsASpringComponent() {
    for (HttpApiRegistry.Entry entry : HttpApiRegistry.all()) {
      Assert.assertNotNull(entry.getServlet().getName() + " must be a @Component",
          entry.getServlet().getDeclaredAnnotation(Component.class));
    }
  }

  /**
   * The independent check: the routes the annotation registry derives must equal the routes the
   * four hand-written registration lists mounted before this refactor, read from a checked-in
   * fixture, plus only the deltas recorded in {@link #INTENTIONAL_ADDED}. The registry does not
   * validate itself.
   */
  @Test
  public void testDerivedRoutesMatchPreRefactorBaseline() throws Exception {
    Map<Surface, Set<String>> baseline = loadBaseline();
    for (Surface surface : Surface.values()) {
      Set<String> expected =
          new TreeSet<>(baseline.getOrDefault(surface, Collections.emptySet()));
      expected.addAll(INTENTIONAL_ADDED.getOrDefault(surface, Collections.emptySet()));
      Set<String> actual = new TreeSet<>();
      for (HttpApiRegistry.Entry entry : HttpApiRegistry.forSurface(surface)) {
        actual.add(entry.getSuffix());
      }
      Assert.assertEquals(surface + " routes drifted from the pre-refactor baseline",
          expected, actual);
    }
  }

  /**
   * The checked-in audit snapshot must equal the matrix derived from the annotations, so any
   * change to an endpoint's access or exposed surfaces surfaces as a one-line diff in review.
   */
  @Test
  public void testAuditMatrixMatchesSnapshot() throws Exception {
    List<String> expected = readLines("/http/api-audit-matrix.txt");
    Assert.assertEquals(
        "http api audit matrix changed; if intentional, regenerate the snapshot resource",
        expected, HttpApiRegistry.auditMatrix());
  }

  @Test
  public void testEveryConcreteServletIsAnnotatedOrExcluded() {
    // the completeness net: a servlet cannot be added to the package and silently left unmounted
    for (Class<?> servlet : HttpApiRegistry.scanConcreteServlets(HttpApiRegistry.PACKAGE)) {
      boolean api = servlet.getDeclaredAnnotation(HttpApi.class) != null;
      boolean excluded = servlet.getDeclaredAnnotation(HttpApiExcluded.class) != null;
      Assert.assertTrue(
          servlet.getName() + " must declare exactly one of @HttpApi / @HttpApiExcluded",
          api ^ excluded);
    }
  }

  @Test
  public void testValidFixturePackageBuilds() {
    List<HttpApiRegistry.Entry> entries =
        HttpApiRegistry.buildFromPackage(REGTEST + "valid");
    Set<String> suffixes = new TreeSet<>();
    for (HttpApiRegistry.Entry entry : entries) {
      suffixes.add(entry.getSuffix());
    }
    Assert.assertEquals(new TreeSet<>(Arrays.asList("validread", "validwrite")), suffixes);
  }

  @Test
  public void testUnannotatedServletRejected() {
    assertBuildFails(REGTEST + "noanno", "must declare exactly one");
  }

  @Test
  public void testBothAnnotationsRejected() {
    assertBuildFails(REGTEST + "both", "must declare exactly one");
  }

  @Test
  public void testWriteOnCursorSurfaceRejected() {
    assertBuildFails(REGTEST + "writecursor", "may only be exposed on the FULL surface");
  }

  @Test
  public void testBuildOnCursorSurfaceRejected() {
    assertBuildFails(REGTEST + "buildcursor", "may only be exposed on the FULL surface");
  }

  @Test
  public void testDuplicateSuffixRejected() {
    assertBuildFails(REGTEST + "dupsuffix", "duplicate endpoint");
  }

  @Test
  public void testSlashInSuffixRejected() {
    assertBuildFails(REGTEST + "slash", "must not contain '/'");
  }

  @Test
  public void testBlankSuffixRejected() {
    assertBuildFails(REGTEST + "blank", "blank");
  }

  @Test
  public void testMissingComponentRejected() {
    assertBuildFails(REGTEST + "notcomponent", "must be a @Component");
  }

  @Test
  public void testEmptySurfacesRejected() {
    assertBuildFails(REGTEST + "emptysurface", "at least one surface");
  }

  private void assertBuildFails(String pkg, String fragment) {
    try {
      HttpApiRegistry.buildFromPackage(pkg);
      Assert.fail("expected build to fail for " + pkg);
    } catch (IllegalStateException e) {
      Assert.assertTrue("message '" + e.getMessage() + "' should contain '" + fragment + "'",
          e.getMessage() != null && e.getMessage().contains(fragment));
    }
  }

  @Test
  public void testFullNodeServiceMountsExactlyTheRegistry() throws Exception {
    Set<String> expected = pathsOf(Surface.FULL, "/wallet/");
    expected.add("/net/listnodes");
    expected.add("/monitor/getstatsinfo");
    expected.add("/monitor/getnodeinfo");
    Assert.assertEquals(expected, mountedPaths(FullNodeHttpApiService.class));
  }

  @Test
  public void testSolidityServiceMountsExactlyTheRegistry() throws Exception {
    Set<String> expected = pathsOf(Surface.SOLIDITY, "/walletsolidity/");
    expected.add("/wallet/getnodeinfo");
    Assert.assertEquals(expected, mountedPaths(HttpApiOnSolidityService.class));
  }

  @Test
  public void testPbftServiceMountsExactlyTheRegistry() throws Exception {
    Set<String> expected = pathsOf(Surface.PBFT, "/");
    Assert.assertEquals(expected, mountedPaths(HttpApiOnPBFTService.class));
  }

  @Test
  public void testSolidityNodeServiceMountsExactlyTheRegistry() throws Exception {
    Set<String> expected = pathsOf(Surface.SOLIDITY_NODE, "/walletsolidity/");
    expected.add("/wallet/getnodeinfo");
    Assert.assertEquals(expected, mountedPaths(SolidityNodeHttpApiService.class));
  }

  private static Set<String> pathsOf(Surface surface, String prefix) {
    Set<String> paths = new HashSet<>();
    for (HttpApiRegistry.Entry entry : HttpApiRegistry.forSurface(surface)) {
      paths.add(prefix + entry.getSuffix());
    }
    return paths;
  }

  private List<String> readLines(String resource) throws Exception {
    List<String> lines = new ArrayList<>();
    try (InputStream in = getClass().getResourceAsStream(resource)) {
      Assert.assertNotNull("missing resource " + resource, in);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.trim().isEmpty() && !line.startsWith("#")) {
          lines.add(line);
        }
      }
    }
    return lines;
  }

  private Map<Surface, Set<String>> loadBaseline() throws Exception {
    Map<Surface, Set<String>> baseline = new EnumMap<>(Surface.class);
    try (InputStream in = getClass().getResourceAsStream(BASELINE)) {
      Assert.assertNotNull("missing baseline fixture " + BASELINE, in);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }
        String[] parts = line.split(" ");
        baseline.computeIfAbsent(Surface.valueOf(parts[0]), s -> new TreeSet<>()).add(parts[1]);
      }
    }
    return baseline;
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
