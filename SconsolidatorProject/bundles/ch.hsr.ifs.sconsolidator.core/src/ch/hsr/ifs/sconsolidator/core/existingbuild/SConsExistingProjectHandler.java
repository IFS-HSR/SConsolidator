package ch.hsr.ifs.sconsolidator.core.existingbuild;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPersistentPreferenceStore;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.NatureUtil;
import ch.hsr.ifs.sconsolidator.core.managed.ProjectSettingsWriter;
import ch.hsr.ifs.sconsolidator.core.managed.SConsManagedProjectHandler;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;

public class SConsExistingProjectHandler {
  private static final String MANAGED_BUILD_NATURE =
      "org.eclipse.cdt.managedbuilder.core.managedBuildNature";
  private static final String SCANNER_CONFIG_NATURE =
      "org.eclipse.cdt.managedbuilder.core.ScannerConfigNature";
  private final IProject project;
  private final IProgressMonitor pm;

  public SConsExistingProjectHandler(IProject project, IProgressMonitor pm) {
    this.project = project;
    this.pm = pm;
  }

  public void configureProject(String wizardCmdArgs) throws CoreException {
    pm.beginTask(SConsI18N.SConsProjectConfigureHandler_ConvertingInProgress, 10);

    try {
      configureCDTProject();
      pm.worked(1);

      saveProjectSettings(wizardCmdArgs);
      pm.worked(1);

      extractProjectInformation();
      pm.worked(7);

      removeMakeBuilder();
      addSConsNature();
    } catch (Exception e) {
      SConsPlugin.log(e);
      IStatus status =
          new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0,
              SConsI18N.SConsProjectConfigureHandler_ConfiguringFailedErrorMessage, e);
      throw new CoreException(status);
    } finally {
      pm.done();
    }
  }

  private void extractProjectInformation() throws InvocationTargetException, InterruptedException {
    new BuildInfoCollector(project).run(pm);
  }

  private void saveProjectSettings(String wizardCmdOptions) {
    String workspaceOptions =
        SConsPlugin.getWorkspacePreferenceStore().getString(
            PreferenceConstants.ADDITIONAL_COMMANDLINE_OPTIONS);

    if (wizardCmdOptions == null || workspaceOptions.equals(wizardCmdOptions))
      return;

    // user has chosen his own SCons options and decided to take these
    // instead of workspace settings; therefore we store them in the
    // newly created project
    IPersistentPreferenceStore projectOptions = SConsPlugin.getProjectPreferenceStore(project);
    projectOptions.setValue(PreferenceConstants.ADDITIONAL_COMMANDLINE_OPTIONS, wizardCmdOptions);
    projectOptions.setValue(PreferenceConstants.BUILD_SETTINGS_PAGE_ID
        + PreferenceConstants.USE_PARENT_SUFFIX, false);
    try {
      projectOptions.save();
    } catch (Exception e) {
      // ignore because user then already has SCons settings
    }
  }

  private void configureCDTProject() throws CoreException {
    new ProjectSettingsWriter(project).configureCDTProject();
  }

  private void addSConsNature() throws CoreException {
    new SConsExistingCodeNature().addSConsNature(project, pm);
  }

  private void removeMakeBuilder() throws CoreException {
    new NatureUtil(project).deconfigureBuilder(SConsManagedProjectHandler.MANAGED_MAKE_BUILDER);
  }

  public void deconfigureProject() throws CoreException {
    try {
      removeSConsNature();
      removeManagedBuildNatures();
    } catch (Exception e) {
      SConsPlugin.log(e);
      IStatus status =
          new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0,
              SConsI18N.SConsProjectConfigureHandler_DeconfiguringFailedErrorMessage, e);
      throw new CoreException(status);
    }
  }

  private void removeManagedBuildNatures() throws CoreException {
    NatureUtil util = new NatureUtil(project);
    util.removeNature(SCANNER_CONFIG_NATURE, pm);
    util.removeNature(MANAGED_BUILD_NATURE, pm);
  }

  private void removeSConsNature() throws CoreException {
    new SConsExistingCodeNature().removeSConsNature(project, pm);
  }
}
