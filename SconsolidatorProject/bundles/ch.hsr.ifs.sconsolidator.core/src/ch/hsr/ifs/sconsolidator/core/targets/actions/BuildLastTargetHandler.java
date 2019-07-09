package ch.hsr.ifs.sconsolidator.core.targets.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


public class BuildLastTargetHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        BuildLastTargetAction buildAction = new BuildLastTargetAction();
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        buildAction.setActivePart(null, part);
        buildAction.selectionChanged(null, selection);
        if (buildAction.isEnabled()) {
            buildAction.run(null);
        }
        return null;
    }
}
