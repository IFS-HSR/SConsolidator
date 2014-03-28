package ch.hsr.ifs.sconsolidator.core.targets.actions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.targets.SConsTargetDialog;

public class AddTargetAction extends SelectionListenerAction {
  private final Shell shell;

  public AddTargetAction(Shell shell) {
    super(SConsI18N.AddTargetAction_AddTargetName);
    this.shell = shell;
    setToolTipText(SConsI18N.AddTargetAction_AddTargetTooltip);
    SConsImages.setImageDescriptors(this, SConsImages.SCONS_TARGET_ADD);
    setEnabled(false);
  }

  @Override
  public void run() {
    Object selection = getSelectedElement();

    if (selection instanceof IContainer) {
      SConsTargetDialog dialog = SConsTargetDialog.fromNewTarget(shell, (IContainer) selection);
      dialog.open();
    }
  }

  @Override
  protected boolean updateSelection(IStructuredSelection selection) {
    return super.updateSelection(selection) && getSelectedElement() != null;
  }

  private Object getSelectedElement() {
    if (getStructuredSelection().size() == 1) {
      Object element = getStructuredSelection().getFirstElement();

      if (element instanceof IContainer)
        return element;
    }

    return null;
  }
}
