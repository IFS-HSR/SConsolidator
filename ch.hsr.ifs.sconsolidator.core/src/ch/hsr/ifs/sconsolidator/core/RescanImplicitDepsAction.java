package ch.hsr.ifs.sconsolidator.core;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;

public class RescanImplicitDepsAction extends WithSelectedProjectsAction {

  @Override
  public void run(IAction action) {
    withProjects(new VoidFunction<Collection<IProject>>() {
      @Override
      public void apply(Collection<IProject> projects) {
        for (IProject p : projects) {
          implicitDependenciesChanged(p);
        }
      }
    });
  }

  private void implicitDependenciesChanged(IProject project) {
    IPreferenceStore p =
        SConsPlugin.getActivePreferences(project, PreferenceConstants.PERF_VS_ACCURACY_PAGE_ID);
    p.setValue(PreferenceConstants.IMPLICIT_DEPS_CHANGED, true);
  }
}
