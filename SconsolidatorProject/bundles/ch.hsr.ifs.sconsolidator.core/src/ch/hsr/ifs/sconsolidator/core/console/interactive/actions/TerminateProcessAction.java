package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.jface.action.Action;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.console.interactive.InteractiveConsole;


public class TerminateProcessAction extends Action {

    private final InteractiveConsole console;

    public TerminateProcessAction(InteractiveConsole console) {
        this.console = console;
        setToolTipText(SConsI18N.TerminateProcessAction_TerminateDescription);
        SConsImages.setImageDescriptors(this, SConsImages.CONSOLE_TERMINATE);
        setEnabled(false);
    }

    @Override
    public void run() {
        console.terminateProcess();
        console.onProcessFinish();
    }
}
