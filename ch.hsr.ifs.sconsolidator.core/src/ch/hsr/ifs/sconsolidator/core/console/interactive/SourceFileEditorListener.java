package ch.hsr.ifs.sconsolidator.core.console.interactive;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import ch.hsr.ifs.sconsolidator.core.console.interactive.actions.BuildCurrentTargetAction;
import ch.hsr.ifs.sconsolidator.core.console.interactive.actions.CurrentTargetAction;
import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;

@SuppressWarnings("restriction")
class SourceFileEditorListener implements IPartListener {
  private final IProject project;
  private final CurrentTargetAction cleanAction;
  private final BuildCurrentTargetAction buildAction;
  private final InteractiveConsole console;

  public SourceFileEditorListener(InteractiveConsole console, IProject project,
      CurrentTargetAction cleanAction, BuildCurrentTargetAction buildAction) {
    this.console = console;
    this.project = project;
    this.cleanAction = cleanAction;
    this.buildAction = buildAction;
  }

  private void refreshCleanAction(IFile file) {
    refreshAction(cleanAction, TargetCommand.CommandType.CleanFileTarget, file);
  }

  private void refreshBuildAction(IFile file) {
    refreshAction(buildAction, TargetCommand.CommandType.BuildFileTarget, file);
  }

  private void refreshAction(CurrentTargetAction action, TargetCommand.CommandType commandType,
      IFile file) {
    if (!console.isProcessStarted())
      return;
    action.setCurrentTarget(file);
    action.setEnabled(true);
    action.setToolTipText(getToolTipText(commandType.toString(), file));
  }

  private String getToolTipText(String prefix, IFile file) {
    return String.format("%s '%s'", prefix, file.getName());
  }

  @Override
  public void partBroughtToTop(IWorkbenchPart part) {}

  @Override
  public void partClosed(IWorkbenchPart part) {}

  @Override
  public void partDeactivated(IWorkbenchPart part) {
    if (!isProjectFileOpenInActiveEditor(part)) {
      buildAction.setEnabled(false);
      cleanAction.setEnabled(false);
    }
  }

  private boolean isProjectFileOpenInActiveEditor(IWorkbenchPart part) {
    IWorkbenchPage activePage = part.getSite().getWorkbenchWindow().getActivePage();
    IEditorPart activeEditor = activePage.getActiveEditor();
    if (!(activeEditor instanceof CEditor))
      return false;
    IFile editorFile = getProjectFileInEditor((CEditor) activeEditor);
    return editorFile != null;
  }

  private IFile getProjectFileInEditor(CEditor editor) {
    IEditorInput editorInput = editor.getEditorInput();
    if (!(editorInput instanceof IFileEditorInput))
      return null;
    IFile file = ((IFileEditorInput) editorInput).getFile();
    if (file.getProject().equals(project))
      return file;
    return null;
  }

  @Override
  public void partOpened(IWorkbenchPart part) {}

  @Override
  public void partActivated(IWorkbenchPart part) {
    if (!(part instanceof CEditor))
      return;
    IFile editorFile = getProjectFileInEditor((CEditor) part);
    if (editorFile != null) {
      refreshBuildAction(editorFile);
      refreshCleanAction(editorFile);
    }
  }
}
