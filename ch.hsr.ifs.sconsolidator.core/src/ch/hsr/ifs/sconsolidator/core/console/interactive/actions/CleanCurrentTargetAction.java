package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.core.resources.IFile;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;
import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand.CommandType;

public class CleanCurrentTargetAction extends AbstractCurrentTargetAction {

  public CleanCurrentTargetAction() {
    super(SConsI18N.CleanCurrentTargetAction_Tooltip, SConsImages.CLEAN_CURRENT_TARGET);
  }

  @Override
  public TargetCommand getTargetCommand() {
    IFile target = getCurrentTarget();
    CommandType commandType = TargetCommand.CommandType.CleanFileTarget;
    return new TargetCommand(commandType, target, target.getProject());
  }
}
