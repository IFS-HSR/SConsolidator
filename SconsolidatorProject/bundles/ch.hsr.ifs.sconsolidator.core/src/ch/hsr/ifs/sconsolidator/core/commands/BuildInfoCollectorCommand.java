package ch.hsr.ifs.sconsolidator.core.commands;

import static ch.hsr.ifs.sconsolidator.core.SConsHelper.BUILD_INFO_COLLECTOR;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.SConsTwoStepBuild;

public class BuildInfoCollectorCommand extends SConsCommand {
  private static final String[] DEFAULT_ARGUMENTS = {"-u", "-s"};  
  private final Collection<String> arguments;

  public BuildInfoCollectorCommand(SConsConsole console, IProject project)
      throws EmptySConsPathException {
    this(null, console, project);
  }

  public BuildInfoCollectorCommand(String binaryPath, SConsConsole console, IProject project)
      throws EmptySConsPathException {
    super(binaryPath, project, console, DEFAULT_ARGUMENTS);
    arguments = new LinkedList<String>();
    fillArguments(project);
  }

  private void fillArguments(IProject project) {
    String collectorPath = project.getFile(BUILD_INFO_COLLECTOR).getLocation().toOSString();
    SConsTwoStepBuild twoStepBuild = new SConsTwoStepBuild(project, collectorPath);
    arguments.addAll(twoStepBuild.getCommandLine());
  }

  @Override
  protected Collection<String> getArguments() {
    return arguments;
  }
}
