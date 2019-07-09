package ch.hsr.ifs.sconsolidator.core.targets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetEvent;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetListener;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetManager;


class SConsTargetContentProvider implements ITreeContentProvider, SConsBuildTargetListener {

    private StructuredViewer viewer;

    @Override
    public Object[] getChildren(Object obj) {
        if (obj instanceof IWorkspaceRoot)
            return getAllProjects();
        else if (obj instanceof IProject) return getProjectTargets((IProject) obj);

        return new Object[0];
    }

    private Object[] getProjectTargets(IProject project) {
        List<SConsBuildTarget> children = new ArrayList<SConsBuildTarget>();
        try {
            children.addAll(getTargetManager().getTargets(project));
        } catch (CoreException e) {
            SConsPlugin.log(e);
        }
        return children.toArray();
    }

    private IProject[] getAllProjects() {
        return getTargetManager().getTargetBuilderProjects().toArray(new IProject[0]);
    }

    private SConsBuildTargetManager getTargetManager() {
        return SConsPlugin.getDefault().getSConsTargetManager();
    }

    @Override
    public Object getParent(Object obj) {
        if (obj instanceof SConsBuildTarget)
            return ((SConsBuildTarget) obj).getContainer();
        else if (obj instanceof IProject) return ((IProject) obj).getParent();
        return null;
    }

    @Override
    public boolean hasChildren(Object obj) {
        return getChildren(obj).length > 0;
    }

    @Override
    public Object[] getElements(Object obj) {
        return getChildren(obj);
    }

    @Override
    public void inputChanged(Viewer newViewer, Object oldInput, Object newInput) {
        if (viewer == null) {
            getTargetManager().addListener(this);
        }
        viewer = (StructuredViewer) newViewer;
    }

    @Override
    public void dispose() {
        if (viewer != null) {
            getTargetManager().removeListener(this);
        }
    }

    @Override
    public void targetChanged(SConsBuildTargetEvent event) {
        Control ctrl = viewer.getControl();
        if (ctrl != null && !ctrl.isDisposed()) {
            switch (event.getType()) {
            case PROJECT_ADDED:
            case PROJECT_REMOVED:
                refreshProjects(ctrl);
                break;
            case TARGET_ADDED:
            case TARGET_CHANGED:
            case TARGET_REMOVED:
                refreshTargets(event, ctrl);
                break;
            default:
                throw new IllegalArgumentException("Unknown event type");
            }
        }
    }

    private void refreshTargets(final SConsBuildTargetEvent event, final Control ctrl) {
        ctrl.getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (!ctrl.isDisposed()) {
                    IContainer container = event.getTarget().getContainer();

                    while (container.getParent() != null) {
                        container = container.getParent();
                    }
                    viewer.refresh(container);
                }
            }
        });
    }

    private void refreshProjects(final Control ctrl) {
        ctrl.getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (!ctrl.isDisposed()) {
                    viewer.refresh();
                }
            }
        });
    }
}
