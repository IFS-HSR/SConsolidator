package ch.hsr.ifs.sconsolidator.core.managed.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsI18N;

public class DeconfigureSConsFilesAction extends WorkspaceModifyOperation {
  private final IProject project;

  public DeconfigureSConsFilesAction(IProject project) {
    this.project = project;
  }

  @Override
  protected void execute(IProgressMonitor pm) throws CoreException, InvocationTargetException,
      InterruptedException {
    pm.beginTask(SConsI18N.ConfigureSConsFilesAction_CreatingSConsBuilderFilesMessage, 1);

    try {
      removeSConsFiles(pm);
    } finally {
      pm.done();
    }
  }

  private void removeSConsFiles(final IProgressMonitor pm) throws CoreException {
    project.accept(new IResourceVisitor() {
      @Override
      public boolean visit(IResource resource) throws CoreException {
        if (resource instanceof IFile) {
          IFile file = (IFile) resource;

          if (SConsHelper.isSConsFile(file)) {
            file.delete(true, pm);
          }
        }
        return true;
      }
    });
  }
}
