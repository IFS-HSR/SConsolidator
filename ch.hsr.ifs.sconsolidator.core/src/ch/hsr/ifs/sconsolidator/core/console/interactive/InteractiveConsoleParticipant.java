package ch.hsr.ifs.sconsolidator.core.console.interactive;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

public class InteractiveConsoleParticipant implements IConsolePageParticipant {

  @Override
  public void init(IPageBookViewPage page, IConsole console) {
    if (!(console instanceof InteractiveConsole))
      throw new IllegalArgumentException("InteractiveConsole expected");
    initToolBar((InteractiveConsole) console, getToolbarManager(page));
  }

  private IToolBarManager getToolbarManager(IPageBookViewPage page) {
    return page.getSite().getActionBars().getToolBarManager();
  }

  private void initToolBar(InteractiveConsole console, IToolBarManager toolbar) {
    toolbar.appendToGroup(IConsoleConstants.LAUNCH_GROUP, console.getTerminateAction());
    toolbar.appendToGroup(IConsoleConstants.LAUNCH_GROUP, console.getCloseAction());
    toolbar.appendToGroup(IConsoleConstants.LAUNCH_GROUP, console.getRedoLastTargetAction());
    toolbar.appendToGroup(IConsoleConstants.LAUNCH_GROUP, console.getBuildCurrentTargetAction());
    toolbar.appendToGroup(IConsoleConstants.LAUNCH_GROUP, console.getCleanCurrentTargetAction());
  }

  @Override
  public void dispose() {}

  @Override
  public void activated() {}

  @Override
  public void deactivated() {}

  @Override
  public <T> T getAdapter(Class<T> adapter) {
    return null;
  }
}
