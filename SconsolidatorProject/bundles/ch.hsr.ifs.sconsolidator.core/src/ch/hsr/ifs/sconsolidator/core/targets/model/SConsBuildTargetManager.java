package ch.hsr.ifs.sconsolidator.core.targets.model;

import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.list;
import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.map;
import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.set;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.FileUtil;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetEvent.SConsBuildTargetEventType;


public class SConsBuildTargetManager implements IResourceChangeListener {

    private final List<IProject>                          projects;
    private final Map<IProject, PersistentTargetsHandler> projectMap;
    private final Set<SConsBuildTargetListener>           listeners;

    public SConsBuildTargetManager() {
        projects = list();
        projectMap = map();
        listeners = set();
    }

    public SConsBuildTarget createTarget(IProject project, String targetName, String targetBuilderID, String targetDesc,
            String additionalCmdLineArgs) {
        return new SConsBuildTarget(targetName, project, targetBuilderID, targetDesc, additionalCmdLineArgs);
    }

    public void addTarget(IContainer container, SConsBuildTarget target) throws CoreException {
        if (container instanceof IWorkspaceRoot) throw new IllegalArgumentException("Wrong container given!");

        PersistentTargetsHandler projectTargets = projectMap.get(target.getProject());

        if (projectTargets == null) {
            projectTargets = readTargets(target.getProject());
        }

        target.setContainer(container == null ? target.getProject() : container);
        projectTargets.add(target);

        try {
            writeTargets(projectTargets);
        } catch (CoreException e) {
            projectTargets.remove(target);
            throw e;
        }
        notifyListeners(new SConsBuildTargetEvent(this, SConsBuildTargetEventType.TARGET_ADDED, target));
    }

    public boolean targetExists(SConsBuildTarget target) {
        PersistentTargetsHandler projectTargets = projectMap.get(target.getProject());
        if (projectTargets == null) {
            projectTargets = readTargets(target.getProject());
        }
        return projectTargets.contains(target);
    }

    public Collection<IProject> getTargetBuilderProjects() {
        return projects;
    }

    public Collection<SConsBuildTarget> getTargets(IContainer container) throws CoreException {
        PersistentTargetsHandler projectTargets = projectMap.get(container.getProject());

        if (projectTargets == null) {
            projectTargets = readTargets(container.getProject());
        }
        return projectTargets.getTargets();
    }

    private PersistentTargetsHandler readTargets(IProject project) {
        PersistentTargetsHandler projectTargets = new PersistentTargetsHandler(project);
        projectMap.put(project, projectTargets);
        return projectTargets;
    }

    public void startup() {
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            if (SConsNatureTypes.isOpenSConsProject(project)) {
                projects.add(project);
            }
        }
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    public void shutdown() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        IResourceDelta delta = event.getDelta();

        if (delta != null) {
            try {
                delta.accept(new SConsBuildTargetVisitor());
            } catch (CoreException e) {
                SConsPlugin.log(e);
            }
        }
    }

    private class SConsBuildTargetVisitor implements IResourceDeltaVisitor {

        @Override
        public boolean visit(IResourceDelta delta) {
            if (delta == null) return false;

            IResource resource = delta.getResource();
            if (resource.getType() == IResource.PROJECT) {
                IProject project = (IProject) resource;
                int flags = delta.getFlags();
                int deltaKind = delta.getKind();

                if (isAddedCase(project, deltaKind)) {
                    handleProjectAdded(project);
                } else if (isDeletedCase(project, deltaKind)) {
                    deleteTargets(project);
                    handleProjectRemoved(project);
                } else if (isChangedChase(deltaKind)) {
                    handleProjectChanged(project, flags);
                }
                return false;
            }
            return resource instanceof IWorkspaceRoot;
        }

        private boolean isChangedChase(int deltaKind) {
            return deltaKind == IResourceDelta.CHANGED;
        }

        private boolean isDeletedCase(IProject project, int deltaKind) {
            return deltaKind == IResourceDelta.REMOVED && projects.contains(project);
        }

        private boolean isAddedCase(IProject project, int deltaKind) {
            return deltaKind == IResourceDelta.ADDED && SConsNatureTypes.isOpenSConsProject(project) && !projects.contains(project);
        }

        private void handleProjectRemoved(IProject project) {
            projects.remove(project);
            notifyListeners(new SConsBuildTargetEvent(SConsBuildTargetManager.this, SConsBuildTargetEventType.PROJECT_REMOVED));
        }

        private void handleProjectAdded(IProject project) {
            projects.add(project);
            notifyListeners(new SConsBuildTargetEvent(SConsBuildTargetManager.this, SConsBuildTargetEventType.PROJECT_ADDED));
        }

        private void handleProjectChanged(IProject project, int flags) {
            boolean isSConsProject = SConsNatureTypes.isOpenSConsProject(project);

            if (0 != (flags & IResourceDelta.DESCRIPTION)) {
                if (projects.contains(project) && !isSConsProject) {
                    removeProject(project);
                } else if (!projects.contains(project) && isSConsProject) {
                    handleProjectAdded(project);
                }
            }

            if (0 != (flags & IResourceDelta.OPEN)) {
                if (!project.isOpen() && projects.contains(project)) {
                    removeProject(project);
                } else if (project.isOpen() && isSConsProject && !projects.contains(project)) {
                    handleProjectAdded(project);
                }
            }
        }

        private void removeProject(IProject project) {
            projectMap.remove(project);
            handleProjectRemoved(project);
        }

    }

    private void deleteTargets(IProject project) {
        IPath location = SConsPlugin.getDefault().getStateLocation().append(project.getName());
        IPath targetFilePath = location.addFileExtension(PersistentTargetsHandler.TARGETS_EXT);
        FileUtil.safelyDeleteFile(targetFilePath.toFile());
        projectMap.remove(project);
    }

    public void renameTarget(SConsBuildTarget target, String name) throws CoreException {
        SConsBuildTarget sconsTarget = target;
        PersistentTargetsHandler projectTargets = projectMap.get(sconsTarget.getProject());

        if (projectTargets == null) {
            projectTargets = readTargets(sconsTarget.getProject());
        }

        sconsTarget.setTargetName(name);

        if (projectTargets.contains(sconsTarget)) {
            updateTarget(sconsTarget);
        }
    }

    public void removeTarget(SConsBuildTarget target) throws CoreException {
        PersistentTargetsHandler projectTargets = projectMap.get(target.getProject());

        if (projectTargets == null) {
            projectTargets = readTargets(target.getProject());
        }

        if (projectTargets.remove(target)) {
            try {
                writeTargets(projectTargets);
            } catch (CoreException e) {
                projectTargets.add(target);
                throw e;
            }
            notifyListeners(new SConsBuildTargetEvent(this, SConsBuildTargetEventType.TARGET_REMOVED, target));
        }
    }

    public SConsBuildTarget findTarget(IContainer container, String name) throws CoreException {
        PersistentTargetsHandler projectTargets = projectMap.get(container.getProject());

        if (projectTargets == null) {
            projectTargets = readTargets(container.getProject());
        }

        return projectTargets.findTarget(name);
    }

    public void updateTarget(SConsBuildTarget target) throws CoreException {
        PersistentTargetsHandler projectTargets = projectMap.get(target.getProject());

        if (projectTargets == null || !projectTargets.contains(target)) return;

        writeTargets(projectTargets);
        notifyListeners(new SConsBuildTargetEvent(this, SConsBuildTargetEventType.TARGET_CHANGED, target));
    }

    private void writeTargets(PersistentTargetsHandler projectTargets) throws CoreException {
        projectTargets.saveTargets();
    }

    private void notifyListeners(SConsBuildTargetEvent event) {
        for (SConsBuildTargetListener listener : listeners) {
            listener.targetChanged(event);
        }
    }

    public void addListener(SConsBuildTargetListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SConsBuildTargetListener listener) {
        listeners.remove(listener);
    }

    public boolean hasTargetBuilder(IProject project) {
        return SConsNatureTypes.isOpenSConsProject(project);
    }
}
