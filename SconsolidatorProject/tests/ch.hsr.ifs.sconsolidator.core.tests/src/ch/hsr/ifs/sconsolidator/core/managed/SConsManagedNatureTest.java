package ch.hsr.ifs.sconsolidator.core.managed;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.SConsBuilder;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.base.utils.NatureUtil;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;

public class SConsManagedNatureTest {
  CppManagedTestProject testProject;

  @Before
  public void setUp() throws Exception {
    testProject = new CppManagedTestProject(false, false);
  }

  @After
  public void tearDown() throws Exception {
	assertNotNull("TestProject is null", testProject);
    testProject.dispose();
  }

  @Test
  public void testNatureAddAndRemove() throws Exception {
    IProject project = testProject.getProject();
    new NatureUtil(project).addNature(SConsNatureTypes.MANAGED_PROJECT_NATURE.getId(),
        new NullProgressMonitor());
    assertTrue(project.hasNature(SConsNatureTypes.MANAGED_PROJECT_NATURE.getId()));
    assertTrue(new NatureUtil(project).hasBuilder(SConsBuilder.BUILDER_ID));

    new NatureUtil(project).removeNature(SConsNatureTypes.MANAGED_PROJECT_NATURE.getId(),
        new NullProgressMonitor());
    assertFalse(project.hasNature(SConsNatureTypes.MANAGED_PROJECT_NATURE.getId()));
    Thread.sleep(100); // FIXME wait a little for deconfigure() being called
    assertFalse(new NatureUtil(project).hasBuilder(SConsBuilder.BUILDER_ID));
    // assertTrue(NatureUtil.hasBuilder(project,
    // SConsProjectConfigureHandler.MANAGED_MAKE_BUILDER));
    // assertFalse(SConsUtil.hasSConsFiles(project));
  }
}
