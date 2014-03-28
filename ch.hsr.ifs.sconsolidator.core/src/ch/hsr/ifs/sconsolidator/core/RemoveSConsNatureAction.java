package ch.hsr.ifs.sconsolidator.core;

import static ch.hsr.ifs.sconsolidator.core.SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE;
import static ch.hsr.ifs.sconsolidator.core.SConsNatureTypes.MANAGED_PROJECT_NATURE;
import static ch.hsr.ifs.sconsolidator.core.SConsNatureTypes.NON_CPP_PROJECT_NATURE;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;

import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;
import ch.hsr.ifs.sconsolidator.core.existingbuild.SConsExistingProjectHandler;
import ch.hsr.ifs.sconsolidator.core.managed.SConsManagedProjectHandler;
import ch.hsr.ifs.sconsolidator.core.noncpp.SConsNonCppNature;

public class RemoveSConsNatureAction extends WithSelectedProjectsAction {

  @Override
  public void run(IAction action) {
    withProjects(new VoidFunction<Collection<IProject>>() {
      @Override
      public void apply(Collection<IProject> projects) {
        for (IProject p : projects) {
          performRemoval(p);
        }
      }
    });
  }

  private void performRemoval(IProject project) {
    try {
      if (hasNature(project, EXISTING_CODE_PROJECT_NATURE)) {
        removeExistingNature(project);
      } else if (hasNature(project, MANAGED_PROJECT_NATURE)) {
        removeManagedNature(project);
      } else if (hasNature(project, NON_CPP_PROJECT_NATURE)) {
        removeNonCppNature(project);
      }
    } catch (CoreException e) {
      SConsPlugin.showExceptionInDisplayThread(
          SConsI18N.RemoveSConsNature_NatureRemovalFailedTitle,
          SConsI18N.RemoveSConsNature_NatureRemovalFailedMsg, e);
    }
  }

  private boolean hasNature(IProject project, SConsNatureTypes sconsNature) throws CoreException {
    return project.hasNature(sconsNature.getId());
  }

  private void removeNonCppNature(IProject project) throws CoreException {
    new SConsNonCppNature().removeSConsNature(project, new NullProgressMonitor());
  }

  private void removeManagedNature(IProject project) throws CoreException {
    new SConsManagedProjectHandler(project, new NullProgressMonitor()).deconfigureProject();
  }

  private void removeExistingNature(IProject project) throws CoreException {
    new SConsExistingProjectHandler(project, new NullProgressMonitor()).deconfigureProject();
  }
}
