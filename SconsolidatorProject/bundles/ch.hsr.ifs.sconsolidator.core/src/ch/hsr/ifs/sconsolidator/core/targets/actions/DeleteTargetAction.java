package ch.hsr.ifs.sconsolidator.core.targets.actions;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetManager;


public class DeleteTargetAction extends SelectionListenerAction {

    private final Shell shell;

    public DeleteTargetAction(Shell shell) {
        super(SConsI18N.DeleteTargetAction_DeleteTargetName);
        this.shell = shell;
        setToolTipText(SConsI18N.DeleteTargetAction_DeleteTargetTooltip);
        SConsImages.setImageDescriptors(this, SConsImages.SCONS_TARGET_DELETE);
        setEnabled(false);
    }

    private boolean confirmDelete() {
        List<?> targets = getSelectedElements();
        String title;
        String msg;

        if (targets.size() == 1) {
            title = SConsI18N.DeleteTargetAction_ConfirmTitle;
            SConsBuildTarget target = (SConsBuildTarget) targets.get(0);
            msg = NLS.bind(SConsI18N.DeleteTargetAction_ConfirmMessage, target.getDescription());
        } else {
            title = SConsI18N.DeleteTargetAction_ConfirmMultipleTitle;
            msg = NLS.bind(SConsI18N.DeleteTargetAction_ConfirmMultipleMessage, targets.size());
        }

        return MessageDialog.openQuestion(shell, title, msg);
    }

    @Override
    public void run() {
        if (!canDelete() || !confirmDelete()) return;

        SConsBuildTargetManager manager = SConsPlugin.getDefault().getSConsTargetManager();

        try {
            for (Object target : getSelectedElements())
                if (target instanceof SConsBuildTarget) {
                    manager.removeTarget((SConsBuildTarget) target);
                }
        } catch (CoreException e) {
            SConsPlugin.showExceptionInDisplayThread(SConsI18N.DeleteTargetAction_TargetDeletionFailedTitle,
                    SConsI18N.DeleteTargetAction_TargetDeletionFailedMessage, e);
        }
    }

    @Override
    protected boolean updateSelection(IStructuredSelection selection) {
        return super.updateSelection(selection) && canDelete();
    }

    private List<?> getSelectedElements() {
        return getStructuredSelection().toList();
    }

    private boolean canDelete() {
        List<?> elements = getSelectedElements();

        for (Object element : elements)
            if (!(element instanceof SConsBuildTarget)) return false;

        return !elements.isEmpty();
    }
}
