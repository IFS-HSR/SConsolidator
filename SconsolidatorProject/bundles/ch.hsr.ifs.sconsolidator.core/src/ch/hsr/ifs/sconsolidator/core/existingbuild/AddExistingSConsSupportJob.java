package ch.hsr.ifs.sconsolidator.core.existingbuild;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;


public class AddExistingSConsSupportJob extends Job {

    private final String               additionalCmdArgs;
    private final Collection<IProject> projects;

    public AddExistingSConsSupportJob(Collection<IProject> projects, String additionalCmdArgs) {
        super(SConsI18N.SConsExtractInformationDialog_ConvertToSConsProjectMsg);
        this.projects = projects;
        this.additionalCmdArgs = additionalCmdArgs;
    }

    @Override
    protected IStatus run(IProgressMonitor pm) {
        pm.beginTask(getName(), projects.size());

        try {
            for (IProject p : projects) {
                addSConsSupport(p, pm);

                if (pm.isCanceled()) return Status.CANCEL_STATUS;
            }
        } catch (CoreException e) {
            return new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, e.getMessage());
        } finally {
            pm.done();
        }
        return Status.OK_STATUS;
    }

    private void addSConsSupport(IProject project, IProgressMonitor pm) throws CoreException {
        new SConsExistingProjectHandler(project, SubMonitor.convert(pm)).configureProject(additionalCmdArgs);
    }
}
