package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;

import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.console.interactive.InteractiveConsole;

public class RedoLastTargetAction extends Action {
  private final InteractiveConsole console;

  public RedoLastTargetAction(InteractiveConsole console) {
    this.console = console;
    SConsImages.setImageDescriptors(this, SConsImages.REDO_LAST_TARGET);
    setEnabled(false);
  }

  @Override
  public void run() {
    try {
      console.redoLastTargetAction();
    } catch (CoreException e) {
      SConsPlugin.showExceptionInDisplayThread(e);
    }
  }
}
