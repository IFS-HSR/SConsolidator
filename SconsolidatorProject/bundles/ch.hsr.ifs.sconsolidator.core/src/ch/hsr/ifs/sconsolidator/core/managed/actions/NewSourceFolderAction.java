package ch.hsr.ifs.sconsolidator.core.managed.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.managed.SConsFileWriter;


public class NewSourceFolderAction extends WorkspaceModifyOperation {

    private Collection<IResource> paths;

    @Override
    protected void execute(IProgressMonitor pm) throws CoreException, InvocationTargetException, InterruptedException {
        try {
            pm.beginTask(SConsI18N.NewSourceFolderAction_AddingSConscriptsMessage, paths.size());

            if (!pm.isCanceled()) {
                for (IResource source : paths) {
                    new SConsFileWriter(source.getProject()).writeSConscript(source.getProjectRelativePath());
                    pm.worked(1);
                }
            }
        } finally {
            pm.done();
        }
    }

    public void run(Collection<IResource> paths, IProgressMonitor pm) throws CoreException, InvocationTargetException, InterruptedException {
        this.paths = paths;
        execute(pm);
    }
}
