package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;

public class NewSConsExecutableProjectWizard extends NewSConsProjectWizard {

  public NewSConsExecutableProjectWizard() {
    super("ch.hsr.ifs.sconsolidator.exe.projectType",
        ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_EXE, SConsImages.getImageDescriptor(
            SConsImages.SCONS_TARGET).createImage(),
        SConsI18N.NewSConsExecutableProjectWizard_ExecutableProjectName);
  }
}
