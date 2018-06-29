package ch.hsr.ifs.sconsolidator.core.base.utils;

import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.list;

import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public class NatureUtil {
  private final IProject project;

  public NatureUtil(IProject project) {
    if (!project.isOpen())
      throw new IllegalArgumentException("Project must not be closed");
    this.project = project;
  }

  public void addNature(String natureId, IProgressMonitor pm) throws CoreException {
    if (hasNature(natureId))
      return;
    IProjectDescription desc = project.getDescription();
    List<String> natures = list(desc.getNatureIds());
    natures.add(natureId);
    String[] newNatures = natures.toArray(new String[natures.size()]);
    validateNewNatures(newNatures);
    desc.setNatureIds(newNatures);
    project.setDescription(desc, pm);
  }

  private static void validateNewNatures(String[] newNatures) {
    IStatus status = ResourcesPlugin.getWorkspace().validateNatureSet(newNatures);
    if (status.getCode() == IStatus.ERROR)
      throw new IllegalArgumentException(status.getMessage());
  }

  public void removeNature(String natureId, IProgressMonitor pm) throws CoreException {
    IProjectDescription description = project.getDescription();
    List<String> newNatures = list(description.getNatureIds());
    newNatures.remove(natureId);
    description.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
    project.setDescription(description, pm);
  }

  public boolean hasNature(String natureId) {
    try {
      return project.hasNature(natureId);
    } catch (CoreException e) {
      return false;
    }
  }

  public boolean hasBuilder(String builderID) throws CoreException {
    for (ICommand command : project.getDescription().getBuildSpec())
      if (command.getBuilderName().equals(builderID))
        return true;
    return false;
  }

  public void configureBuilder(String builderID) throws CoreException {
    if (hasBuilder(builderID))
      return;
    IProjectDescription desc = project.getDescription();
    List<ICommand> commands = list(desc.getBuildSpec());
    ICommand newBuilder = desc.newCommand();
    newBuilder.setBuilderName(builderID);
    commands.add(newBuilder);
    desc.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
    project.setDescription(desc, null);
  }

  public void deconfigureBuilder(String builderId) throws CoreException {
    IProjectDescription projectDesc = project.getDescription();
    List<ICommand> commands = list(projectDesc.getBuildSpec());

    for (ICommand c : commands) {
      if (c.getBuilderName().equals(builderId)) {
        commands.remove(c);
        projectDesc.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
        project.setDescription(projectDesc, null);
        return;
      }
    }
  }
}
