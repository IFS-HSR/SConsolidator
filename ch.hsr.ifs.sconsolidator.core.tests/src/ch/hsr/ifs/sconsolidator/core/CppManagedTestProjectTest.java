package ch.hsr.ifs.sconsolidator.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.base.functional.FunctionalHelper;
import ch.hsr.ifs.sconsolidator.core.base.functional.UnaryFunction;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;

public class CppManagedTestProjectTest {
  private CppManagedTestProject testProject;

  @Before
  public void setUp() throws Exception {
    testProject = new CppManagedTestProject(false);
  }

  @After
  public void tearDown() throws Exception {
    testProject.dispose();
  }

  @Test
  public void testNatureManaged() {
    assertTrue(testProject.isManagedBuildActivated());
  }

  @Test
  public void testArtefactInfos() {
    IConfiguration config = testProject.getConfig();
    assertEquals("org.eclipse.cdt.build.core.buildArtefactType.exe", config.getBuildArtefactType()
        .getId());
    assertEquals(CppManagedTestProject.TEST_PROJECT_NAME, config.getArtifactName());
  }

  @Test
  public void testAddIncludePath() throws Exception {
    testProject.addIncludePath("/usr/include/boost");
    assertTrue(contains(testProject.getIncludePaths(), "/usr/include/boost"));
  }

  @Test
  public void testAddLibrary() throws Exception {
    testProject.addLibrary("boost-thread");
    assertTrue(contains(testProject.getLibraries(), "boost-thread"));
  }

  @Ignore
  public void testAddLibraryPath() throws Exception {
    testProject.addLibraryPath("/usr/lib/boost");
    assertTrue(contains(testProject.getLibraryPaths(), "/usr/lib/boost"));
  }

  private boolean contains(final String[] arr, final String elm) {
    Collection<String> matched =
        FunctionalHelper.filter(Arrays.asList(arr), new UnaryFunction<String, Boolean>() {
          @Override
          public Boolean apply(String param) {
            return param.equals(elm);
          }
        });
    return !matched.isEmpty();
  }
}
