package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.swt.graphics.Image;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;


public class NewSConsStaticLibProjectWizard extends NewSConsProjectWizard {

    private static final String ENTRY_ID = "ch.hsr.ifs.sconsolidator.a.projectType";

    @Override
    protected EntryDescriptor getEntryDescriptor(SConsWizardHandler handler) {
        Image proImg = SConsImages.getImageDescriptor(SConsImages.SCONS_TARGET).createImage();
        String name = SConsI18N.NewSConsStaticLibraryProjectWizard_StaticLibraryProjectName;
        return new EntryDescriptor(ENTRY_ID, CATEGORY_ID, name, false, handler, proImg);
    }

    protected IToolChain[] getExtensionToolchains() {
        return ManagedBuildManager.getExtensionsToolChains(ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_ID,
                ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_STATICLIB);
    }
}
