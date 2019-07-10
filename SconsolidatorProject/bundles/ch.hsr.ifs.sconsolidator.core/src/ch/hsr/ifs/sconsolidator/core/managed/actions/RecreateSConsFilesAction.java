package ch.hsr.ifs.sconsolidator.core.managed.actions;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.WithSelectedProjectsAction;
import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;


public class RecreateSConsFilesAction extends WithSelectedProjectsAction {

    @Override
    public void run(IAction action) {
        withProjects(new VoidFunction<Collection<IProject>>() {

            @Override
            public void apply(final Collection<IProject> projects) {
                try {
                    for (IProject p : projects) {
                        new ConfigureSConsFilesAction(p).run(new NullProgressMonitor());
                    }
                } catch (Exception e) {
                    SConsPlugin.showExceptionInDisplayThread(e);
                }
            }
        });
    }
}
