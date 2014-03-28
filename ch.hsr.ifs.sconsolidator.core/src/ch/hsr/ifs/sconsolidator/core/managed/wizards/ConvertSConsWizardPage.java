package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.ui.wizards.conversion.ConvertProjectWizardPage;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;

class ConvertSConsWizardPage extends ConvertProjectWizardPage {
  private static String WIZARD_CONVERT_SCONS_DESC = SConsI18N.ConvertSConsWizardPage_Desc;
  private static String WIZARD_CONVERT_SCONS_TITLE = SConsI18N.ConvertSConsWizardPage_Title;

  public ConvertSConsWizardPage() {
    super(WIZARD_CONVERT_SCONS_TITLE);
  }

  @Override
  protected String getWzDescriptionResource() {
    return WIZARD_CONVERT_SCONS_DESC;
  }

  @Override
  protected String getWzTitleResource() {
    return WIZARD_CONVERT_SCONS_TITLE;
  }

  @Override
  public Object[] getCheckedElements() {
    return super.getCheckedElements();
  }

  @Override
  public boolean isCandidate(IProject project) {
    IManagedBuildInfo buildInfo = getBuildInfo(project);
    if (buildInfo == null)
      return false;
    return !SConsNatureTypes.isOpenSConsProject(project) && hasCorCppNature(project)
        && isMangedBuildOn(buildInfo, project);
  }

  private IManagedBuildInfo getBuildInfo(IProject project) {
    return ManagedBuildManager.getBuildInfo(project);
  }

  private boolean isMangedBuildOn(IManagedBuildInfo buildInfo, IProject project) {
    return buildInfo.getDefaultConfiguration().isManagedBuildOn();
  }

  private boolean hasCorCppNature(IProject project) {
    try {
      return project.hasNature(CProjectNature.C_NATURE_ID)
          || project.hasNature(CCProjectNature.CC_NATURE_ID);
    } catch (CoreException e) {
      SConsPlugin.log(e);
      return false;
    }
  }
}
