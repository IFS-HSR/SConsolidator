package ch.hsr.ifs.sconsolidator.core.commands;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;
import ch.hsr.ifs.sconsolidator.core.preferences.SConsOptionHandler;

public class CleanCommand extends SConsCommand {
  // -c: Clean up by removing all target files for which a construction command is specified
  private static final String[] DEFAULT_ARGUMENTS = {"-c", "-u"};
  private final Collection<String> arguments;

  public CleanCommand(SConsConsole console, IProject project, IPreferenceStore settingsStore)
      throws EmptySConsPathException {
    this(null, console, project, settingsStore);
  }

  public CleanCommand(String binaryPath, SConsConsole console, IProject project,
      IPreferenceStore settingsStore) throws EmptySConsPathException {
    super(binaryPath, project, console, DEFAULT_ARGUMENTS);
    arguments = new LinkedList<String>();
    addProjectOptions(project);
    addAdditionalSConsOptions(project);
    addTargets(settingsStore);
  }

  private void addTargets(IPreferenceStore settingsStore) {
    arguments.addAll(StringUtil.split(settingsStore.getString(PreferenceConstants.DEFAULT_TARGET)));
  }

  private void addAdditionalSConsOptions(IProject project) {
    arguments.addAll(getAdditionalSConsOptions(project));
  }

  private void addProjectOptions(IProject project) {
    arguments.addAll(new SConsOptionHandler(project).getCommandLineOptions());
  }

  @Override
  protected Collection<String> getArguments() {
    return arguments;
  }
}
