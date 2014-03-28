package ch.hsr.ifs.sconsolidator.core.targets.actions;

import org.eclipse.ui.actions.SelectionListenerAction;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;

public class RebuildLastTargetAction extends SelectionListenerAction {

  public RebuildLastTargetAction() {
    super(SConsI18N.RebuildLastTargetAction_RebuildLastTargetActionName);
    setToolTipText(SConsI18N.RebuildLastTargetAction_RebuildLastTargetActionTooltip);
    setEnabled(false);
  }

  @Override
  public void run() {
    BuildLastTargetAction buildAction = new BuildLastTargetAction();
    buildAction.selectionChanged(null, getStructuredSelection());

    if (buildAction.isEnabled()) {
      buildAction.run(null);
    }
  }
}
