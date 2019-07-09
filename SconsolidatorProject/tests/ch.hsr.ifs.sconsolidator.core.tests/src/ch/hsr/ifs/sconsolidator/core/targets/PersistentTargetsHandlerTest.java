package ch.hsr.ifs.sconsolidator.core.targets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;
import ch.hsr.ifs.sconsolidator.core.targets.model.PersistentTargetsHandler;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;

public class PersistentTargetsHandlerTest {
  private IProject project;
  private PersistentTargetsHandler handler;
  private SConsBuildTarget target;
  private CppManagedTestProject testProject;

  @Before
  public void before() throws Exception {
    testProject = new CppManagedTestProject(false);
    project = testProject.getProject();
    handler = new PersistentTargetsHandler(project);
    target =
        new SConsBuildTarget("CoastFoundationTest", project, "", "foundation tests", "--run-force");
  }

  @After
  public void after() throws Exception {
	assertNotNull("TestProject is null", testProject);
    testProject.dispose();
  }

  @Test
  public void testNoTargets() {
    assertEquals(0, handler.getTargets().size());
  }

  @Test
  public void testTargetAdd() {
    handler.add(target);
    assertTrue(handler.contains(target));
    handler.add(target);
    assertEquals(1, handler.getTargets().size());
  }

  @Test
  public void testTargetRemove() {
    handler.add(target);
    assertTrue(handler.contains(target));
    handler.remove(target);
    assertFalse(handler.contains(target));
    assertEquals(0, handler.getTargets().size());
  }

  @Test
  public void testSaveTargets() throws CoreException {
    handler.add(target);
    handler.saveTargets();
    PersistentTargetsHandler newHandler = new PersistentTargetsHandler(testProject.getProject());
    assertEquals(1, handler.getTargets().size());
    assertTrue(newHandler.getTargets().iterator().next().getTargetName()
        .equals("CoastFoundationTest"));
  }
}
