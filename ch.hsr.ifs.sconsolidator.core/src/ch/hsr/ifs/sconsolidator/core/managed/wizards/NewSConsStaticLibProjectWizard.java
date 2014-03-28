package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;

public class NewSConsStaticLibProjectWizard extends NewSConsProjectWizard {

  public NewSConsStaticLibProjectWizard() {
    super("ch.hsr.ifs.sconsolidator.a.projectType",
        ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_STATICLIB, SConsImages.getImageDescriptor(
            SConsImages.SCONS_TARGET).createImage(),
        SConsI18N.NewSConsStaticLibraryProjectWizard_StaticLibraryProjectName);
  }
}
