package ch.hsr.ifs.sconsolidator.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.model.ICModelMarker;
import org.eclipse.cdt.core.resources.ACBuilder;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;

import ch.hsr.ifs.sconsolidator.core.base.utils.IOUtil;
import ch.hsr.ifs.sconsolidator.core.commands.BuildCommand;
import ch.hsr.ifs.sconsolidator.core.commands.CleanCommand;
import ch.hsr.ifs.sconsolidator.core.console.BuildConsole;
import ch.hsr.ifs.sconsolidator.core.managed.SConsFileWriter;
import ch.hsr.ifs.sconsolidator.core.preferences.pages.ExecutableNotFoundHandler;

public class SConsBuilder extends ACBuilder {
  public static final String BUILDER_ID = "ch.hsr.ifs.sconsolidator.Builder";

  @Override
  protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor pm)
      throws CoreException {
    pm.beginTask(SConsI18N.SConsBuilder_BuildingInProgressMessage, IProgressMonitor.UNKNOWN);
    IProject project = getProject();

    try {
      refreshSConfigFileIfManaged(project);
      removeAllMarkers(project);

      if (checkCancel(pm))
        return null;

      runSConsBuild(args, pm, project);

      if (checkCancel(pm))
        return null;

      refreshProject(project, pm);
    } catch (InterruptedException e) {
      // silently ignore, the user has chosen to cancel the build
    } catch (EmptySConsPathException e) {
      ExecutableNotFoundHandler.handleError();
    } catch (Exception e) {
      IStatus status = new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0, e.getMessage(), e);
      throw new CoreException(status);
    } finally {
      disableAutoBuildIfNecessary(project);
      pm.done();
    }
    return null; // We do not need the deltas next time
  }

  private boolean checkCancel(IProgressMonitor pm) {
    if (pm.isCanceled())
      throw new OperationCanceledException();
    return isInterrupted();
  }

  @Override
  protected void clean(IProgressMonitor pm) throws CoreException {
    IProject project = getProject();

    try {
      pm.beginTask(SConsI18N.SConsBuilder_CleaningInProgressMessage, IProgressMonitor.UNKNOWN);

      if (checkCancel(pm))
        return;

      runSConsClean(pm, project);

      if (checkCancel(pm))
        return;

      removeAllMarkers(project);
      refreshProject(project, pm);
    } catch (InterruptedException e) {
      // silently ignore, the user has chosen to cancel the clean
    } catch (Exception e) {
      IStatus status = new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0, e.getMessage(), e);
      throw new CoreException(status);
    } finally {
      pm.done();
    }
  }

  private void removeAllMarkers(IProject project) throws CoreException {
    if (project == null || !project.isAccessible())
      return;

    if (project.hasNature("org.eclipse.cdt.core.cnature")) {
      boolean includeSubTypes = true;
      IMarker[] markers =
          project.findMarkers(ICModelMarker.C_MODEL_PROBLEM_MARKER, includeSubTypes,
              IResource.DEPTH_INFINITE);
      project.getWorkspace().deleteMarkers(markers);
    }
  }

  private void disableAutoBuildIfNecessary(IProject project) {
    try {
      if (!project.hasNature(SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE.getId()))
        return;

      if (project.getWorkspace().isAutoBuilding()) {
        IWorkspaceDescription description = ResourcesPlugin.getWorkspace().getDescription();
        description.setAutoBuilding(false);
        ResourcesPlugin.getWorkspace().setDescription(description);
      }
    } catch (CoreException e) {
      // no real issue
    }
  }

  private void refreshProject(IProject project, IProgressMonitor pm) throws CoreException {
    project.refreshLocal(IResource.DEPTH_INFINITE, pm);
  }

  private void refreshSConfigFileIfManaged(IProject project) throws CoreException {
    if (project.hasNature(SConsNatureTypes.MANAGED_PROJECT_NATURE.getId())) {
      new SConsFileWriter(project).writeSConfig();
    }
  }

  private void runSConsBuild(Map<String, String> args, IProgressMonitor pm, IProject project)
      throws EmptySConsPathException, IOException, InterruptedException, CoreException {
    BuildConsole console = showConsole(project);
    BuildCommand buildCommand = getBuildCommand(args, project, console);
    buildCommand.run(project.getLocation().toFile(), pm);

    if (!project.hasNature(SConsNatureTypes.NON_CPP_PROJECT_NATURE.getId())) {
      new ErrorParserHandler(project, buildCommand).createMarkers();
    }
  }

  private BuildConsole showConsole(IProject project) {
    BuildConsole console = new BuildConsole(project);
    BuildConsole.showConsole(console);
    return console;
  }

  private BuildCommand getBuildCommand(Map<String, String> args, IProject project,
      BuildConsole console) throws EmptySConsPathException {
    return new BuildCommand(console, project, args.get("targetName"), args.get("additionalArgs"));
  }

  private void runSConsClean(IProgressMonitor pm, IProject project) throws EmptySConsPathException,
      IOException, InterruptedException {
    BuildConsole console = showConsole(project);
    CleanCommand clean =
        new CleanCommand(console, project, SConsPlugin.getProjectPreferenceStore(project));
    clean.run(project.getLocation().toFile(), pm);
  }

  private final class ErrorParserHandler {
    private final IProject project;
    private final BuildCommand buildCommand;

    private ErrorParserHandler(IProject project, BuildCommand buildCommand) {
      this.project = project;
      this.buildCommand = buildCommand;
    }

    private void createMarkers() throws IOException {
      InputStream is = new ByteArrayInputStream(buildCommand.getErrorStream().toByteArray());
      ErrorParserManager epm =
          new ErrorParserManager(project, SConsBuilder.this, getErrorParsers());
      try {
        writeOutputToErrorParser(is, epm);
      } finally {
        closeStreams(is, epm);
      }
    }

    private String[] getErrorParsers() {
      IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
      if (buildInfo == null)
        return null;
      String[] errorParsers = buildInfo.getDefaultConfiguration().getErrorParserList();
      return errorParsers;
    }

    private void closeStreams(InputStream is, ErrorParserManager epm) {
      IOUtil.safeClose(epm);
      if (epm != null) {
        IOUtil.safeClose(epm.getOutputStream());
      }
      IOUtil.safeClose(is);
    }

    private void writeOutputToErrorParser(InputStream is, OutputStream os) throws IOException {
      byte[] buffer = new byte[1024];
      int bytesRead;

      while ((bytesRead = is.read(buffer)) >= 0) {
        os.write(buffer, 0, bytesRead);
      }
    }
  }
}
