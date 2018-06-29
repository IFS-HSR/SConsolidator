package ch.hsr.ifs.sconsolidator.core.targets.actions;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.targets.SConsTargetDialog;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;

public class EditTargetAction extends SelectionListenerAction {
  private final Shell shell;

  public EditTargetAction(Shell shell) {
    super(SConsI18N.EditTargetAction_EditTargetName);
    this.shell = shell;
    setToolTipText(SConsI18N.EditTargetAction_EditTargetTooltip);
    SConsImages.setImageDescriptors(this, SConsImages.SCONS_TARGET_EDIT);
  }

  @Override
  public void run() {
    if (canRename()) {
      SConsTargetDialog dialog = createTargetDialog();
      dialog.open();
    }
  }

  private SConsTargetDialog createTargetDialog() {
    SConsBuildTarget selected = (SConsBuildTarget) getStructuredSelection().getFirstElement();
    return SConsTargetDialog.fromExistingTarget(shell, selected);
  }

  @Override
  protected boolean updateSelection(IStructuredSelection selection) {
    return super.updateSelection(selection) && canRename();
  }

  private boolean canRename() {
    List<?> elements = getStructuredSelection().toList();
    if (elements.size() == 1 && elements.get(0) instanceof SConsBuildTarget)
      return true;
    return false;
  }
}
