package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.AbstractCWizard;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.graphics.Image;

abstract class NewSConsProjectWizard extends AbstractCWizard {
  private final String sconsProjectType;
  private final String buildArtefactType;
  private final Image wizardImg;
  private final String projectName;

  public NewSConsProjectWizard(String sconsProjectType, String buildArtefactType, Image wizardImg,
      String projectName) {
    this.sconsProjectType = sconsProjectType;
    this.buildArtefactType = buildArtefactType;
    this.wizardImg = wizardImg;
    this.projectName = projectName;
  }

  @Override
  public EntryDescriptor[] createItems(boolean supportedOnly, IWizard wizard) {
    SConsWizardHandler handler = getHandler(wizard);
    IToolChain[] toolChains = getExtensionToolchains();

    for (IToolChain tc : toolChains)
      if (!supportedOnly || isValid(tc, true, wizard)) {
        handler.addTc(tc);
      }

    return new EntryDescriptor[] {getEntryDescriptor(handler)};
  }

  private IToolChain[] getExtensionToolchains() {
    return ManagedBuildManager.getExtensionsToolChains(
        ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_ID, buildArtefactType);
  }

  private EntryDescriptor getEntryDescriptor(SConsWizardHandler handler) {
    return new EntryDescriptor(sconsProjectType, null, projectName, false, handler, wizardImg);
  }

  private SConsWizardHandler getHandler(IWizard wizard) {
    return new SConsWizardHandler(new SConsBuildPropertyValue(buildArtefactType, projectName),
        parent, wizard);
  }
}
