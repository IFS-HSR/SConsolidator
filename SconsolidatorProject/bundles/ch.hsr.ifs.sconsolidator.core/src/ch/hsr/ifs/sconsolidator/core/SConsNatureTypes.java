package ch.hsr.ifs.sconsolidator.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;


public enum SConsNatureTypes {
    MANAGED_PROJECT_NATURE("ch.hsr.ifs.sconsolidator.ManagedNature"), //
    EXISTING_CODE_PROJECT_NATURE("ch.hsr.ifs.sconsolidator.ExistingCodeNature"), //
    NON_CPP_PROJECT_NATURE("ch.hsr.ifs.sconsolidator.NonCppNature");

    private SConsNatureTypes(String natureId) {
        this.natureId = natureId;
    }

    public static boolean isOpenSConsProject(IProject project) {
        try {
            for (SConsNatureTypes nature : values())
                if (project.isOpen() && project.hasNature(nature.getId())) return true;
        } catch (CoreException e) {
            SConsPlugin.log(e);
        }

        return false;
    }

    public String getId() {
        return natureId;
    }

    @Override
    public String toString() {
        return natureId;
    }

    private final String natureId;
}
