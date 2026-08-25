package org.tron.core.services.http;

import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpServlet;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.tron.core.services.http.HttpApi.Access;
import org.tron.core.services.http.HttpApi.Surface;

/**
 * Read-only view of the node's http endpoints, derived at class-load from the {@link HttpApi}
 * annotation declared on each servlet. No hand-maintained table repeats the metadata: the
 * annotation is the single declaration and this registry is computed from it, so an endpoint
 * cannot drift between its implementation and its registration.
 *
 * <p>The whole table is built and validated when this class is first touched — which happens
 * while a service mounts its servlets, before any Jetty bind, and covers every surface whether
 * or not that surface is enabled on the node. A table that breaks an invariant throws here and
 * the node does not boot. Invariants:
 *
 * <ul>
 *   <li>every concrete servlet under {@value #PACKAGE} declares exactly one of {@link HttpApi} or
 *       {@link HttpApiExcluded} — a servlet cannot be added and silently left unmounted;</li>
 *   <li>each {@code (surface, suffix)} pair is unique;</li>
 *   <li>suffixes are non-blank and contain no {@code '/'};</li>
 *   <li>every {@link HttpApi} servlet is a Spring {@link Component} bean;</li>
 *   <li>every endpoint whose access is not {@link Access#READ} is exposed on the FULL surface
 *       only — a cursor surface (SOLIDITY / PBFT) must never run a write path on a
 *       cursor-switched thread, and the standalone SolidityNode surface cannot propagate
 *       transactions.</li>
 * </ul>
 *
 * <p>Annotations are read with {@link Class#getDeclaredAnnotation} and {@link HttpApi} is not
 * {@link Inherited}, asserted below, so a servlet subclass can never inherit its parent's
 * exposure — the failure mode the removed cursor-wrapper subclasses would otherwise reintroduce.
 */
public final class HttpApiRegistry {

  static final String PACKAGE = "org.tron.core.services.http.servlets";

  private static final List<Entry> ENTRIES = init();

  private HttpApiRegistry() {
  }

  /** One declared endpoint: its suffix, servlet type, access nature and exposed surfaces. */
  public static final class Entry {

    private final String suffix;
    private final Class<? extends HttpServlet> servlet;
    private final Access access;
    private final EnumSet<Surface> surfaces;

    private Entry(String suffix, Class<? extends HttpServlet> servlet, Access access,
        EnumSet<Surface> surfaces) {
      this.suffix = suffix;
      this.servlet = servlet;
      this.access = access;
      this.surfaces = surfaces;
    }

    public String getSuffix() {
      return suffix;
    }

    public Class<? extends HttpServlet> getServlet() {
      return servlet;
    }

    public Access getAccess() {
      return access;
    }

    public Set<Surface> getSurfaces() {
      return Collections.unmodifiableSet(surfaces);
    }
  }

  /** Endpoints exposed on {@code surface}, ordered by suffix. */
  public static List<Entry> forSurface(Surface surface) {
    List<Entry> result = new ArrayList<>();
    for (Entry entry : ENTRIES) {
      if (entry.surfaces.contains(surface)) {
        result.add(entry);
      }
    }
    return Collections.unmodifiableList(result);
  }

  /** All endpoints, ordered by suffix. */
  public static List<Entry> all() {
    return ENTRIES;
  }

  private static List<Entry> init() {
    List<Entry> entries = buildFromPackage(PACKAGE);
    if (entries.isEmpty()) {
      // a broken classpath scan (packaging / classloader) would otherwise leave the node with no
      // http api and no error; fail the boot loudly instead
      throw new IllegalStateException("no http endpoints discovered in " + PACKAGE);
    }
    return entries;
  }

  /** Concrete, top-level {@link HttpServlet} classes declared in {@code pkg}. */
  static List<Class<?>> scanConcreteServlets(String pkg) {
    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AssignableTypeFilter(HttpServlet.class));
    List<Class<?>> classes = new ArrayList<>();
    for (BeanDefinition bean : scanner.findCandidateComponents(pkg)) {
      Class<?> clazz = load(bean.getBeanClassName());
      // endpoint servlets are top-level classes; a nested servlet in this package is a helper
      // (e.g. a test's inner servlet on the test classpath), never a mounted endpoint
      if (clazz.getEnclosingClass() == null) {
        classes.add(clazz);
      }
    }
    return classes;
  }

  /** Builds and validates the registry from the servlets in {@code pkg}; visible for testing. */
  static List<Entry> buildFromPackage(String pkg) {
    if (HttpApi.class.isAnnotationPresent(Inherited.class)) {
      throw new IllegalStateException(
          "@HttpApi must not be @Inherited: a subclass would inherit its parent's exposure");
    }
    List<Entry> entries = new ArrayList<>();
    Set<String> mounts = new TreeSet<>();
    for (Class<?> clazz : scanConcreteServlets(pkg)) {
      HttpApi api = clazz.getDeclaredAnnotation(HttpApi.class);
      HttpApiExcluded excluded = clazz.getDeclaredAnnotation(HttpApiExcluded.class);
      if ((api == null) == (excluded == null)) {
        throw new IllegalStateException(clazz.getName()
            + " must declare exactly one of @HttpApi or @HttpApiExcluded");
      }
      if (api == null) {
        continue;
      }
      Entry entry = validate(clazz, api);
      for (Surface surface : entry.surfaces) {
        if (!mounts.add(surface + " " + entry.suffix)) {
          throw new IllegalStateException(
              "duplicate endpoint (" + surface + ", " + entry.suffix + ")");
        }
      }
      entries.add(entry);
    }
    entries.sort(Comparator.comparing(Entry::getSuffix));
    return Collections.unmodifiableList(entries);
  }

  private static Entry validate(Class<?> clazz, HttpApi api) {
    if (clazz.getDeclaredAnnotation(Component.class) == null) {
      throw new IllegalStateException(clazz.getName() + " with @HttpApi must be a @Component bean");
    }
    String suffix = api.value();
    if (suffix == null || suffix.trim().isEmpty()) {
      throw new IllegalStateException(clazz.getName() + " has a blank @HttpApi suffix");
    }
    if (suffix.contains("/")) {
      throw new IllegalStateException(clazz.getName() + " suffix must not contain '/': " + suffix);
    }
    if (api.surfaces().length == 0) {
      throw new IllegalStateException(clazz.getName() + " must declare at least one surface");
    }
    EnumSet<Surface> surfaces = EnumSet.copyOf(Arrays.asList(api.surfaces()));
    if (api.access() != Access.READ && !surfaces.equals(EnumSet.of(Surface.FULL))) {
      throw new IllegalStateException(String.format(
          "%s is %s and may only be exposed on the FULL surface, found %s",
          clazz.getName(), api.access(), surfaces));
    }
    return new Entry(suffix, clazz.asSubclass(HttpServlet.class), api.access(), surfaces);
  }

  private static Class<?> load(String name) {
    try {
      return Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("cannot load servlet " + name, e);
    }
  }
}
