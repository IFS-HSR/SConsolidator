package ch.hsr.ifs.sconsolidator.core.console.interactive;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;


class RestartInteractiveListener implements IResourceChangeListener {

    private final IProject           project;
    private final InteractiveConsole console;

    public RestartInteractiveListener(IProject project, InteractiveConsole console) {
        this.project = project;
        this.console = console;
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            IResourceDelta delta = event.getDelta();
            if (delta == null) return;

            delta.accept(new IResourceDeltaVisitor() {

                @Override
                public boolean visit(IResourceDelta delta) throws CoreException {
                    IResource resource = delta.getResource();
                    int resourceType = resource.getType();

                    if (resourceType != IResource.FILE) return true;

                    IFile file = (IFile) resource;
                    IProject affectedProject = file.getProject();

                    if (!affectedProject.equals(project)) return false;

                    if (file.isDerived()) return false;

                    if (isRestartNecessary(delta, file, affectedProject)) {
                        console.restartInteractiveMode();
                    }
                    return true;
                }
            });
        } catch (CoreException e) {
            SConsPlugin.log(e);
        }
    }

    private boolean isRestartNecessary(IResourceDelta delta, IFile file, IProject affectedProject) throws CoreException {
        if (isExistingCodeProject(affectedProject))
            return isAddOrDeleteOfCppFile(delta, file) || hasSConsFileChanged(delta, file);
        else if (isManagedProject(affectedProject)) return isAddOrDeleteOfCppFile(delta, file) || hasSConfigChanged(delta, file);
        return false;
    }

    private boolean isExistingCodeProject(IProject affectedProject) throws CoreException {
        return affectedProject.hasNature(SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE.getId());
    }

    private boolean hasSConfigChanged(IResourceDelta delta, IFile file) {
        return delta.getKind() == IResourceDelta.CHANGED && file.getName().equals(SConsHelper.SCONFIG_PY);
    }

    private boolean hasSConsFileChanged(IResourceDelta delta, IFile file) {
        return delta.getKind() == IResourceDelta.CHANGED && SConsHelper.isSConsFile(file);
    }

    private boolean isAddOrDeleteOfCppFile(IResourceDelta delta, IFile file) {
        return isCppFile(file) && (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.REMOVED);
    }

    private boolean isCppFile(IFile file) {
        String fileExtension = file.getFileExtension();

        if (fileExtension == null) return false;

        return PlatformSpecifics.CPP_RE.matcher(fileExtension).matches();
    }

    private boolean isManagedProject(IProject affectedProject) throws CoreException {
        return affectedProject.hasNature(SConsNatureTypes.MANAGED_PROJECT_NATURE.getId());
    }
}
