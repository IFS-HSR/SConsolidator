package ch.hsr.ifs.sconsolidator.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


public class SConsBuildFilesFilter extends ViewerFilter {

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (!(element instanceof IAdaptable)) return false;

        IAdaptable adaptable = (IAdaptable) element;
        IFile resource = (IFile) adaptable.getAdapter(IFile.class);
        return shouldIgnore(resource);
    }

    private boolean shouldIgnore(IFile file) {
        return file == null || !hasManagedNature(file.getProject()) || !SConsHelper.isSConsFile(file);
    }

    private boolean hasManagedNature(IProject project) {
        try {
            return project.hasNature(SConsNatureTypes.MANAGED_PROJECT_NATURE.getId());
        } catch (CoreException e) {
            SConsPlugin.log(e);
            return false;
        }
    }
}
