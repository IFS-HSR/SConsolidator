package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.core.resources.IResource;

import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;


public class CleanProjectTargetAction extends AbstractProjectTargetAction {

    @Override
    public TargetCommand getTargetCommand() {
        IResource file = getSelectedResource();
        return new TargetCommand(getCommandTypeFor(file), file, getProject());
    }

    private TargetCommand.CommandType getCommandTypeFor(IResource resource) {
        return resource == null ? TargetCommand.CommandType.CleanDefaultTarget : TargetCommand.CommandType.CleanFileTarget;
    }
}
