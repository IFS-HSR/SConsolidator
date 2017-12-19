package ch.hsr.ifs.sconsolidator.core.targets.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;

public class SConsBuildTarget extends PlatformObject {
  private final IProject project;
  private final String targetBuilderID;
  private String additionalCmdLineArgs;
  private String description;
  private IContainer container;
  private String targetName;
  private boolean isDefault;

  public SConsBuildTarget(String targetName, IProject project, String targetBuilderID,
      String description, String additionalCommandLineArgs) {
    this.targetName = targetName;
    this.project = project;
    this.targetBuilderID = targetBuilderID;
    this.description = description;
    this.additionalCmdLineArgs = additionalCommandLineArgs;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public IProject getProject() {
    return project;
  }

  public void setContainer(IContainer container) {
    this.container = container;
  }

  public String getAdditionalCmdLineArgs() {
    return additionalCmdLineArgs;
  }

  public void setAdditionalCmdLineArgs(String additionalCommandLineArgs) {
    this.additionalCmdLineArgs = additionalCommandLineArgs;
  }

  public void build(IProgressMonitor pm) throws CoreException {
    ResourcesPlugin.getWorkspace().run(createBuildRunnable(), null, IResource.NONE, pm);
  }

  private IWorkspaceRunnable createBuildRunnable() {
    final Map<String, String> buildInfoMap = new HashMap<String, String>();
    buildInfoMap.put("targetName", getTargetName());
    buildInfoMap.put("additionalArgs", getAdditionalCmdLineArgs());
    return new IWorkspaceRunnable() {
      @Override
      public void run(IProgressMonitor monitor) throws CoreException {
        project.build(IncrementalProjectBuilder.FULL_BUILD, getTargetBuilderID(), buildInfoMap,
            monitor);
      }
    };
  }

  public IContainer getContainer() {
    return container;
  }

  public String getTargetBuilderID() {
    return targetBuilderID;
  }

  public String getTargetName() {
    return targetName;
  }

  public void setTargetName(String targetName) {
    this.targetName = targetName;
  }

  @Override
  public String toString() {
    return description
        + (additionalCmdLineArgs == null ? "" : String.format(" [%s]", additionalCmdLineArgs));
  }

  public String getCommandLine() {
    if (additionalCmdLineArgs != null && !additionalCmdLineArgs.trim().equals(""))
      return additionalCmdLineArgs + " " + targetName;
    return targetName;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  public boolean isDefault() {
    return isDefault;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Class<T> adapter) {
    if (adapter.equals(IProject.class))
      return (T) getProject();
    else if (adapter.equals(IResource.class))
      return (T) container;
    return super.getAdapter(adapter);
  }
}
