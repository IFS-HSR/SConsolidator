package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.core.resources.IFile;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;
import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand.CommandType;

public class BuildCurrentTargetAction extends AbstractCurrentTargetAction {

  public BuildCurrentTargetAction() {
    super(SConsI18N.BuildCurrentTargetAction_Tooltip, SConsImages.BUILD_CURRENT_TARGET);
  }

  @Override
  public TargetCommand getTargetCommand() {
    IFile target = getCurrentTarget();
    CommandType commandType = TargetCommand.CommandType.BuildFileTarget;
    return new TargetCommand(commandType, target, target.getProject());
  }
}
