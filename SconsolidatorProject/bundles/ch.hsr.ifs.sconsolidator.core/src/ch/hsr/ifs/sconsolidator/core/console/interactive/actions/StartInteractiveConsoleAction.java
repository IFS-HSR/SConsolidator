package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;

import ch.hsr.ifs.sconsolidator.core.WithSelectedProjectsAction;
import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;
import ch.hsr.ifs.sconsolidator.core.console.interactive.StartInteractiveConsoleJob;


public class StartInteractiveConsoleAction extends WithSelectedProjectsAction {

    @Override
    public void run(IAction action) {
        withSingleProject(new VoidFunction<IProject>() {

            @Override
            public void apply(IProject project) {
                new StartInteractiveConsoleJob(project).schedule();
            }
        });
    }
}
