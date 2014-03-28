package ch.hsr.ifs.sconsolidator.core.commands;

import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.DEFAULT_TARGET;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;
import ch.hsr.ifs.sconsolidator.core.preferences.SConsOptionHandler;

public class BuildCommand extends SConsCommand {
  // -u: Walks up the directory structure until an SConstruct file is found,
  // and uses that as the top of the directory tree
  private static final String[] DEFAULT_ARGUMENTS = {"-u"};
  private final Collection<String> arguments;

  public BuildCommand(SConsConsole console, IProject project, String targetName,
      String additionalArgs) throws EmptySConsPathException {
    this(null, console, project, targetName, additionalArgs);
  }

  public BuildCommand(String binaryPath, SConsConsole console, IProject project,
      String targetName, String additionalArgs) throws EmptySConsPathException {
    super(binaryPath, project, console, DEFAULT_ARGUMENTS);
    arguments = new LinkedList<String>();
    addProjectOptions(project);
    addAdditionalSConsOptions(project);
    addAdditionalCmdLineArgs(additionalArgs);
    addTargets(project, targetName);
  }

  private void addAdditionalSConsOptions(IProject project) {
    arguments.addAll(getAdditionalSConsOptions(project));
  }

  private void addProjectOptions(IProject project) {
    arguments.addAll(new SConsOptionHandler(project).getCommandLineOptions());
  }

  private void addAdditionalCmdLineArgs(String additionalArgs) {
    if (additionalArgs != null) {
      arguments.addAll(StringUtil.split(additionalArgs));
    }
  }

  private void addTargets(IProject project, String targetName) {
    if (targetName == null) {
      arguments.addAll(getDefaultTargets(project));
    } else {
      arguments.addAll(StringUtil.split(targetName));
    }
  }

  private List<String> getDefaultTargets(IProject project) {
    return StringUtil.split(SConsPlugin.getProjectPreferenceStore(project)
        .getString(DEFAULT_TARGET));
  }

  @Override
  protected Collection<String> getArguments() {
    return arguments;
  }
}
