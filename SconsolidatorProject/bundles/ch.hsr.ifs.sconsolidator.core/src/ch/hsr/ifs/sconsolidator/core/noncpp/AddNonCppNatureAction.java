package ch.hsr.ifs.sconsolidator.core.noncpp;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.WithSelectedProjectsAction;
import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;

public class AddNonCppNatureAction extends WithSelectedProjectsAction {

  @Override
  public void run(final IAction action) {
    withProjects(new VoidFunction<Collection<IProject>>() {
      @Override
      public void apply(Collection<IProject> projects) {
        try {
          for (IProject p : projects) {
            new SConsNonCppNature().addSConsNature(p, new NullProgressMonitor());
          }
        } catch (Exception e) {
          SConsPlugin.showExceptionInDisplayThread("Adding SCons nature failed",
              "Adding of SCons nature could not be performed.", e);
        }
      }
    });
  }
}
