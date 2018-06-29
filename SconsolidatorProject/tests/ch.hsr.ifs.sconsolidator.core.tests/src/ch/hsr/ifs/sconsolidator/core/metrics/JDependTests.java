package ch.hsr.ifs.sconsolidator.core.metrics;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;

import org.junit.Before;
import org.junit.Test;

// Taken and adapted from http://clarkware.com/software/JDepend.html#junit
public class JDependTests {
  private JDepend jdepend;
  private Collection<JavaPackage> packages;

  @Before
  public void setUp() throws IOException {
    initJDepend();
    startAnalysis();
  }

  private void initJDepend() throws IOException {
    PackageFilter filter = createPackageFilter();
    jdepend = new JDepend(filter);
    jdepend.addDirectory(getBinaryDirectory());
    handleNotVolatilePackages();
  }

  private static PackageFilter createPackageFilter() {
    PackageFilter filter = new PackageFilter();
    filter.addPackage("java.*");
    filter.addPackage("javax.*");
    filter.addPackage("org.*");
    filter.addPackage("ch.hsr.ifs.sconsolidator.core");
    return filter;
  }

  private void handleNotVolatilePackages() {
    // Packages that are not expected to change can be specifically
    // configured with a volatility (V) value.
    // V can either be 0 or 1. If V=0, meaning the package is not at all
    // subject to change, then the package will automatically fall
    // directly on the main sequence (D=0). The following
    // packages are not volatile and maximally stable. Creating
    // dependencies on them is therefore no concern.

    // addNotVolatilePackage("ch.hsr.ifs.mockator.plugin.base");
  }

  // private void addNotVolatilePackage(String packageName) {
  // JavaPackage javaPackage = new JavaPackage(packageName);
  // javaPackage.setVolatility(0);
  // jdepend.addPackage(javaPackage);
  // }

  @SuppressWarnings("unchecked")
  private void startAnalysis() {
    packages = jdepend.analyze();
  }

  @Test
  public void noCyclicPackageDependencies() throws Exception {
    StringBuilder cycles = new StringBuilder();

    for (JavaPackage p : packages) {
      List<JavaPackage> packages = new ArrayList<JavaPackage>();

      if (p.collectCycle(packages)) {
        cycles.append(String.format("%n$ %s $ [", p.getName()));

        for (int i = 0; i < packages.size(); i++) {
          if (i > 0) {
            cycles.append(" -> ");
          }

          cycles.append(packages.get(i).getName());
        }

        cycles.append("]");
      }
    }

    assertEquals("Cycles exist in packages: " + cycles.toString(), false, jdepend.containsCycles());
  }

  @Test
  public void conformanceOfDistanceFromMainSequence() {
    double ideal = 0.0;
    double tolerance = 0.52;

    for (JavaPackage p : packages) {
      assertEquals("Distance exceeded of package: " + p.getName(), ideal, p.distance(), tolerance);
    }
  }

  private static String getBinaryDirectory() {
    try {
      // Locally we should take bin directory because Eclipse keeps this
      // always up-to-date; on the build server only the target/classes
      // directory
      // exists because Maven stores the class files there
      File eclipseBinDir = getEclipseBinaryDir();

      if (eclipseBinDir.exists())
        return eclipseBinDir.getPath();
    } catch (Exception e) {
      return getMavenTargetDir();
    }

    throw new IllegalStateException("Problems determining binary directory for metric tests");
  }

  private static String getMavenTargetDir() {
    return "../ch.hsr.ifs.sconsolidator.core/target/classes";
  }

  private static File getEclipseBinaryDir() throws URISyntaxException, MalformedURLException {
    String relPathToMockatorPlugin = "../../../../../../../../ch.hsr.ifs.sconsolidator.core/";
    URL currentDir = JDependTests.class.getResource("./");
    return new File(new URL(currentDir, relPathToMockatorPlugin + "bin").toURI());
  }
}
