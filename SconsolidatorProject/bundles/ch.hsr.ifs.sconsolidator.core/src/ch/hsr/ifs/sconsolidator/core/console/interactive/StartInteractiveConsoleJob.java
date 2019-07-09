package ch.hsr.ifs.sconsolidator.core.console.interactive;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;


public class StartInteractiveConsoleJob extends Job {

    private final IProject project;

    public StartInteractiveConsoleJob(IProject project) {
        super(SConsI18N.AbstractSConsTargetAction_StartingSConsInteractiveMode);
        this.project = project;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        InteractiveConsole console = InteractiveConsole.getInstance(project);
        try {
            console.startInteractiveMode();
            return Status.OK_STATUS;
        } catch (CoreException e) {
            console.stopInteractiveMode();
            return new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0, "An error occured while starting interactive mode", e);
        }
    }
}
