package ch.hsr.ifs.sconsolidator.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;


public abstract class WithSelectedProjectsAction implements IWorkbenchWindowActionDelegate {

    private final List<IProject> selectedProjects;

    protected WithSelectedProjectsAction() {
        selectedProjects = new ArrayList<IProject>();
    }

    public void withSingleProject(VoidFunction<IProject> f) {
        if (selectedProjects.size() > 1) throw new IllegalStateException("This action should only be called with single element selections");
        f.apply(selectedProjects.get(0));
    }

    public void withProjects(VoidFunction<Collection<IProject>> f) {
        f.apply(selectedProjects);
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (!(selection instanceof IStructuredSelection)) return;

        selectedProjects.clear();

        for (Object selected : ((IStructuredSelection) selection).toList()) {
            if (selected instanceof IProject) {
                selectedProjects.add((IProject) selected);
            } else if (selected instanceof IAdaptable) {
                IProject proj = (IProject) ((IAdaptable) selected).getAdapter(IProject.class);
                if (proj != null) {
                    selectedProjects.add(proj);
                }
            } else if (selected instanceof ICElement) {
                selectedProjects.add(((ICElement) selected).getCProject().getProject());
            }
        }
    }

    @Override
    public void dispose() {}

    @Override
    public void init(final IWorkbenchWindow window) {}
}
