package ch.hsr.ifs.sconsolidator.core.managed;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.NatureUtil;
import ch.hsr.ifs.sconsolidator.core.managed.actions.ConfigureSConsFilesAction;
import ch.hsr.ifs.sconsolidator.core.managed.actions.DeconfigureSConsFilesAction;

public class SConsManagedProjectHandler {
  public static final String MANAGED_MAKE_BUILDER =
      "org.eclipse.cdt.managedbuilder.core.genmakebuilder";
  private final IProject project;
  private final IProgressMonitor pm;

  public SConsManagedProjectHandler(IProject project, IProgressMonitor pm) {
    if (!hasDefaultConfig(project))
      throw new IllegalArgumentException("Project must have a valid default configuration");

    this.project = project;
    this.pm = pm;
  }

  private static boolean hasDefaultConfig(IProject project) {
    IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
    IManagedProject managedProject = buildInfo.getManagedProject();
    IProjectType projectType = managedProject.getProjectType();
    return projectType != null;
  }

  public void configureProject() throws CoreException {
    try {
      writeSConsFiles();
      removeManagedMakeBuilder();
      addSConsNature();
    } catch (Exception e) {
      SConsPlugin.log(e);
      IStatus status =
          new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0,
              SConsI18N.SConsProjectConfigureHandler_ConfiguringFailedErrorMessage, e);
      throw new CoreException(status);
    }
  }

  private void writeSConsFiles() throws InvocationTargetException, InterruptedException {
    new ConfigureSConsFilesAction(project).run(pm);
  }

  public void deconfigureProject() throws CoreException {
    try {
      removeSConsFiles();
      removeSConsNature();
      restoreManagedCDTBuilder();
    } catch (Exception e) {
      SConsPlugin.log(e);
      IStatus status =
          new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0,
              SConsI18N.SConsProjectConfigureHandler_DeconfiguringFailedErrorMessage, e);
      throw new CoreException(status);
    }
  }

  private void removeSConsNature() throws CoreException {
    new SConsManagedNature().removeSConsNature(project, pm);
  }

  private void removeSConsFiles() throws InvocationTargetException, InterruptedException {
    new DeconfigureSConsFilesAction(project).run(pm);
  }

  private void addSConsNature() throws CoreException {
    new SConsManagedNature().addSConsNature(project, pm);
  }

  private void removeManagedMakeBuilder() throws CoreException {
    new NatureUtil(project).deconfigureBuilder(MANAGED_MAKE_BUILDER);
  }

  private void restoreManagedCDTBuilder() throws CoreException {
    new NatureUtil(project).configureBuilder(MANAGED_MAKE_BUILDER);
  }
}
