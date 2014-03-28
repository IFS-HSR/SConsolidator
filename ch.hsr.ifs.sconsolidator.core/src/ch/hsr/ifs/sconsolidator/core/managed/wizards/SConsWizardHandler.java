package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSWizardHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Composite;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.managed.SConsManagedProjectHandler;

class SConsWizardHandler extends MBSWizardHandler {
  private final String buildArtefactType;

  public SConsWizardHandler(SConsBuildPropertyValue prop, Composite composite, IWizard wizard) {
    super(prop, composite, wizard);
    this.buildArtefactType = prop.getId();
  }

  @Override
  protected void doCustom(IProject newProject) {
    super.doCustom(newProject);

    try {
      getWizard().getContainer().run(false, true, getProgressRunnable(newProject));
    } catch (InvocationTargetException e) {
      SConsPlugin.log(e);
    } catch (InterruptedException e) {
      SConsPlugin.log(e);
    }
  }

  private IRunnableWithProgress getProgressRunnable(final IProject newProject) {
    return new IRunnableWithProgress() {
      @Override
      public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
        createSConsProjectSettings(newProject, pm);
      }
    };
  }

  private void createSConsProjectSettings(IProject newProject, IProgressMonitor pm) {
    try {
      setBuildArtefactType(newProject);
      configureProject(newProject, pm);
    } catch (CoreException e) {
      SConsPlugin.log(e);
    }
  }

  private void configureProject(IProject project, IProgressMonitor pm) throws CoreException {
    new SConsManagedProjectHandler(project, pm).configureProject();
  }

  private void setBuildArtefactType(IProject newProject) throws CoreException {
    try {
      IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(newProject);
      info.getDefaultConfiguration().setBuildArtefactType(buildArtefactType);
    } catch (BuildException e) {
      IStatus status =
          new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0,
              SConsI18N.SConsWizardHandler_SetOfBuildArtefactTypeFailed, e);
      throw new CoreException(status);
    }
  }
}
