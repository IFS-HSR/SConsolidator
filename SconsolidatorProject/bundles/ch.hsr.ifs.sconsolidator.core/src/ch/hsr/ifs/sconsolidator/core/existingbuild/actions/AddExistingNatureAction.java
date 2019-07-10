package ch.hsr.ifs.sconsolidator.core.existingbuild.actions;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;

import ch.hsr.ifs.sconsolidator.core.WithSelectedProjectsAction;
import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;


public class AddExistingNatureAction extends WithSelectedProjectsAction {

    @Override
    public void run(IAction action) {
        withProjects(new VoidFunction<Collection<IProject>>() {

            @Override
            public void apply(final Collection<IProject> projects) {
                AddSConsSupportDialog dialog = new AddSConsSupportDialog(projects);
                dialog.open();
            }
        });
    }
}
