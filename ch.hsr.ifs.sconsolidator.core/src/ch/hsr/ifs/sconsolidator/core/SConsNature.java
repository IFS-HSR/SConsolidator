package ch.hsr.ifs.sconsolidator.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import ch.hsr.ifs.sconsolidator.core.base.utils.NatureUtil;

public abstract class SConsNature implements IProjectNature {
  private IProject project;

  public void addSConsNature(IProject project, IProgressMonitor pm) throws CoreException {
    new NatureUtil(project).addNature(getNatureTypeId().getId(), pm);
  }

  protected abstract SConsNatureTypes getNatureTypeId();

  public void removeSConsNature(IProject project, IProgressMonitor pm) throws CoreException {
    new NatureUtil(project).removeNature(getNatureTypeId().getId(), pm);
  }

  @Override
  public void configure() throws CoreException {
    new NatureUtil(project).configureBuilder(SConsBuilder.BUILDER_ID);
  }

  @Override
  public void deconfigure() throws CoreException {
    new NatureUtil(project).deconfigureBuilder(SConsBuilder.BUILDER_ID);
  }

  @Override
  public IProject getProject() {
    return project;
  }

  @Override
  public void setProject(IProject project) {
    this.project = project;
  }
}
