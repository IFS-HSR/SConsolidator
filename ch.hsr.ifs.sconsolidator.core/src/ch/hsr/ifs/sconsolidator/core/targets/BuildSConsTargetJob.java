package ch.hsr.ifs.sconsolidator.core.targets;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.console.interactive.InteractiveConsole;

public class BuildSConsTargetJob extends Job {
  private final TargetCommand targetCommand;

  public BuildSConsTargetJob(TargetCommand targetCommand) {
    super(SConsI18N.AbstractCurrentTargetAction_BuildCurrentTargetOperationMessage);
    this.targetCommand = targetCommand;
  }

  @Override
  protected IStatus run(IProgressMonitor pm) {
    pm.beginTask(SConsI18N.AbstractCurrentTargetAction_BuildCurrentTargetOperationMessage, 1);

    try {
      InteractiveConsole console = startInteractiveConsole();
      console.sendCommand(targetCommand);
    } catch (CoreException e) {
      return new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, e.getMessage());
    } finally {
      pm.done();
    }
    return Status.OK_STATUS;
  }

  private InteractiveConsole startInteractiveConsole() throws CoreException {
    InteractiveConsole console =
        InteractiveConsole.getInstance(targetCommand.getAssociatedProject());
    console.startInteractiveMode();
    return console;
  }
}
