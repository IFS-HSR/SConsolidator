package ch.hsr.ifs.sconsolidator.core.existingbuild.actions;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;

import ch.hsr.ifs.sconsolidator.core.WithSelectedProjectsAction;
import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;
import ch.hsr.ifs.sconsolidator.core.existingbuild.RefreshFromSConsJob;

public class RefreshProjectFromSConsAction extends WithSelectedProjectsAction {

  @Override
  public void run(IAction action) {
    withProjects(new VoidFunction<Collection<IProject>>() {
      @Override
      public void apply(Collection<IProject> projects) {
        new RefreshFromSConsJob(projects).schedule();
      }
    });
  }
}
