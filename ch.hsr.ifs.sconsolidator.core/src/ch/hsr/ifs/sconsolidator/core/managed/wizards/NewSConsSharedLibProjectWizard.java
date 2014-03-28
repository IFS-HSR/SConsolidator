package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;

public class NewSConsSharedLibProjectWizard extends NewSConsProjectWizard {

  public NewSConsSharedLibProjectWizard() {
    super("ch.hsr.ifs.sconsolidator.so.projectType",
        ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_SHAREDLIB, SConsImages.getImageDescriptor(
            SConsImages.SCONS_TARGET).createImage(),
        SConsI18N.NewSConsSharedLibraryProjectWizard_SharedLibraryProjectName);
  }
}
