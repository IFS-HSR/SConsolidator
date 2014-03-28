package ch.hsr.ifs.sconsolidator.core.targets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetListener;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetEvent;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetEvent.SConsBuildTargetEventType;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetManager;

public class SConsBuildTargetManagerTest {
  private CppManagedTestProject testProject;
  private SConsBuildTargetManager targetManager;
  private SConsBuildTarget target;

  @Before
  public void setUp() throws Exception {
    SConsPlugin.getConfigPreferenceStore().setValue(PreferenceConstants.EXECUTABLE_PATH,
        getSConsPath());
    testProject = new CppManagedTestProject(true);
    targetManager = new SConsBuildTargetManager();
    target =
        new SConsBuildTarget("CoastFoundationTest", testProject.getProject(), "",
            "foundation tests", "--run-force");
  }

  @After
  public void tearDown() throws Exception {
    testProject.dispose();
  }

  private String getSConsPath() {
    return PlatformSpecifics.findSConsExecOnSystemPath().getAbsolutePath();
  }

  @Test
  public void testCreateTarget() {
    SConsBuildTarget target =
        targetManager.createTarget(testProject.getProject(), "test", "", "test desc", "-m");
    assertNotNull(target);
  }

  @Test
  public void testAddTarget() throws CoreException {
    TargetListener listener = new TargetListener(SConsBuildTargetEventType.TARGET_ADDED);
    targetManager.addListener(listener);
    targetManager.addTarget(testProject.getProject(), target);
    targetManager.removeListener(listener);
    assertEquals(1, targetManager.getTargets(testProject.getProject()).size());
    assertTrue(targetManager.targetExists(target));
  }

  @Test
  public void testGetTargetBuilderProjects() {
    Collection<IProject> targetBuilderProjects = targetManager.getTargetBuilderProjects();
    assertEquals(0, targetBuilderProjects.size());
  }

  @Test
  public void renameTargetTest() throws CoreException {
    Collection<SConsBuildTarget> targets = targetManager.getTargets(testProject.getProject());
    targetManager.addTarget(testProject.getProject(), target);
    targetManager.renameTarget(target, "test2");
    targets = targetManager.getTargets(testProject.getProject());
    assertEquals("test2", targets.iterator().next().getTargetName());
  }

  @Test
  public void removeTargetTest() throws CoreException {
    targetManager.addTarget(testProject.getProject(), target);
    TargetListener listener = new TargetListener(SConsBuildTargetEventType.TARGET_REMOVED);
    targetManager.addListener(listener);
    targetManager.removeTarget(target);
    targetManager.removeListener(listener);
    Collection<SConsBuildTarget> targets = targetManager.getTargets(testProject.getProject());
    assertEquals(0, targets.size());
  }

  @Test
  public void updateTargetTest() throws CoreException {
    targetManager.addTarget(testProject.getProject(), target);
    TargetListener listener = new TargetListener(SConsBuildTargetEventType.TARGET_CHANGED);
    targetManager.addListener(listener);
    target.setTargetName("test2");
    targetManager.updateTarget(target);
    targetManager.removeListener(listener);
    Collection<SConsBuildTarget> targets = targetManager.getTargets(testProject.getProject());
    assertEquals("test2", targets.iterator().next().getTargetName());
  }

  private static class TargetListener implements SConsBuildTargetListener {
    private final SConsBuildTargetEvent.SConsBuildTargetEventType type;

    TargetListener(final SConsBuildTargetEvent.SConsBuildTargetEventType type) {
      this.type = type;
    }

    @Override
    public void targetChanged(final SConsBuildTargetEvent event) {
      if (event.getType().equals(type))
        return;
      fail();
    }
  }
}
