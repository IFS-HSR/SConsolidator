package ch.hsr.ifs.sconsolidator.core.existingbuild.actions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.existingbuild.RefreshFromSConsJob;

public class RefreshProjectFromSConsHandler extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Collection<IProject> projects = collectActiveSConsProjects();
    new RefreshFromSConsJob(projects).schedule();
    return null;
  }

  private static Collection<IProject> collectActiveSConsProjects() {
    ISelection selection = getSelection();

    if ((selection instanceof IStructuredSelection))
      return getSelectedSconsProjects((IStructuredSelection) selection);

    IProject project = getActiveProject();

    if (isOpenSConsProject(project))
      return Arrays.asList(project);

    return Collections.emptyList();
  }

  private static ISelection getSelection() {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    return window.getActivePage().getSelection();
  }

  private static Collection<IProject> getSelectedSconsProjects(IStructuredSelection selection) {
    Set<IProject> sconsProjects = new HashSet<IProject>();

    for (Iterator<?> it = selection.toList().iterator(); it.hasNext();) {
      IProject project = getProject(it.next());

      if (isOpenSConsProject(project)) {
        sconsProjects.add(project);
      }
    }

    return sconsProjects;
  }

  private static IProject getActiveProject() {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window == null)
      return null;
    IWorkbenchPage page = window.getActivePage();
    if (page == null)
      return null;
    IEditorPart editor = page.getActiveEditor();
    if (editor == null)
      return null;
    IEditorInput input = editor.getEditorInput();
    if ((input instanceof IFileEditorInput))
      return ((IFileEditorInput) input).getFile().getProject();
    return null;
  }

  private static IProject getProject(Object obj) {
    if (!(obj instanceof IAdaptable))
      return null;

    IAdaptable adaptable = (IAdaptable) obj;
    Object adapter = adaptable.getAdapter(IResource.class);

    if (adapter == null)
      return null;

    return ((IResource) adapter).getProject();
  }

  private static boolean isOpenSConsProject(IProject project) {
    return project != null && project.isOpen() && SConsNatureTypes.isOpenSConsProject(project);
  }
}
