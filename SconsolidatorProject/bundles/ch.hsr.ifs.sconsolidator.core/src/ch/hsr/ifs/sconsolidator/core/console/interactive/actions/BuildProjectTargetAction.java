package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.core.resources.IResource;

import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;


public class BuildProjectTargetAction extends AbstractProjectTargetAction {

    @Override
    public TargetCommand getTargetCommand() {
        IResource file = getSelectedResource();
        return new TargetCommand(getCommandType(file), file, getProject());
    }

    private TargetCommand.CommandType getCommandType(IResource resource) {
        return resource == null ? TargetCommand.CommandType.BuildDefaultTarget : TargetCommand.CommandType.BuildFileTarget;
    }
}
