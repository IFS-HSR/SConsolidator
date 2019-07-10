package ch.hsr.ifs.sconsolidator.core.existingbuild;

import static ch.hsr.ifs.sconsolidator.core.base.functional.FunctionalHelper.filter;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.functional.UnaryFunction;


public class RefreshFromSConsJob extends Job {

    private final Collection<IProject> projects;

    public RefreshFromSConsJob(Collection<IProject> projects) {
        super(SConsI18N.RefreshProjectFromSConsAction_RefreshProjectFromSConsInProgess);
        this.projects = filter(projects, new OnlySConsProjectsFilter());
    }

    @Override
    protected IStatus run(IProgressMonitor pm) {
        pm.beginTask(getName(), projects.size());

        try {
            for (IProject p : projects) {
                collectProjectBuildInfo(p, pm);

                if (pm.isCanceled()) return Status.CANCEL_STATUS;
            }
        } finally {
            pm.done();
        }

        return Status.OK_STATUS;
    }

    private void collectProjectBuildInfo(IProject project, IProgressMonitor pm) {
        try {
            new BuildInfoCollector(project).run(SubMonitor.convert(pm));
        } catch (Exception e) {
            String msg = NLS.bind(SConsI18N.ExtractProjectInformationAction_ExtractingFailedMessageDetail, project.getName());
            String title = SConsI18N.ExtractProjectInformationAction_ExtractingFailedMessage;
            SConsPlugin.showExceptionInDisplayThread(title, msg, e);
        }
    }

    private static class OnlySConsProjectsFilter implements UnaryFunction<IProject, Boolean> {

        @Override
        public Boolean apply(IProject project) {
            try {
                return project.isOpen() && hasExistingSConsProjectNature(project);
            } catch (CoreException e) {
                return false;
            }
        }

        private boolean hasExistingSConsProjectNature(IProject project) throws CoreException {
            return project.hasNature(SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE.getId());
        }
    }
}
