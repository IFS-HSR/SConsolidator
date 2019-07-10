package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.AbstractCWizard;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSWizardHandler;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.jface.wizard.IWizard;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;


public class NewSConsProjectWizard extends AbstractCWizard {

    protected static final String CATEGORY_ID   = "ch.hsr.ifs.sconsolidator.wizardcategory";
    private static final String   CATEGORY_NAME = SConsI18N.NewSConsProjectWizard_CategoryName;

    @Override
    public EntryDescriptor[] createItems(boolean supportedOnly, IWizard wizard) {
        SConsWizardHandler handler = getHandler(wizard);
        IToolChain[] toolChains = getExtensionToolchains();

        for (IToolChain tc : toolChains)
            if (!supportedOnly || isValid(tc, true, wizard)) {
                handler.addTc(tc);
            }

        EntryDescriptor entryDescriptor = getEntryDescriptor(handler);
        entryDescriptor.setDefaultForCategory(true);
        return new EntryDescriptor[] { entryDescriptor };
    }

    protected IToolChain[] getExtensionToolchains() {
        return ManagedBuildManager.getExtensionsToolChains(MBSWizardHandler.ARTIFACT, ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_EXE, false);
    }

    protected EntryDescriptor getEntryDescriptor(SConsWizardHandler handler) {
        return new EntryDescriptor(CATEGORY_ID, null, CATEGORY_NAME, true, handler, null);
    }

    private SConsWizardHandler getHandler(IWizard wizard) {
        return new SConsWizardHandler(new SConsBuildPropertyValue(ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_EXE, CATEGORY_NAME), parent,
                wizard);
    }
}
