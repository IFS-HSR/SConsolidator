package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;

import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;


public interface CurrentTargetAction extends IAction {

    TargetCommand getTargetCommand();

    void setCurrentTarget(IFile target);
}
