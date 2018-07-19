package ch.hsr.ifs.sconsolidator.core.managed.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;

public class NewSourceFolderListener implements IResourceChangeListener {
  private static NewSourceFolderListener listener;
  private final NewSourceFolderAction callback;

  public NewSourceFolderListener(NewSourceFolderAction callback) {
    this.callback = callback;
  }

  public static synchronized void startTracking(NewSourceFolderAction callback) {
    if (listener == null) {
      listener = new NewSourceFolderListener(callback);
      ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
          IResourceChangeEvent.PRE_BUILD);
    }
  }

  public static synchronized void stopTracking() {
    if (listener != null) {
      ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
      listener = null;
    }
  }

  @Override
  public void resourceChanged(IResourceChangeEvent event) {
    Collection<IResource> paths = new ArrayList<IResource>();

    try {
      IResourceDelta delta = event.getDelta();

      if (delta == null)
        return;

      delta.accept(getDeltaVisitor(paths));
    } catch (CoreException e) {
      SConsPlugin.log(e);
    }

    try {
      callback.run(paths, new NullProgressMonitor());
    } catch (Exception e) {
      SConsPlugin.log(e);
    }
  }

  private static IResourceDeltaVisitor getDeltaVisitor(final Collection<IResource> paths) {
    return new IResourceDeltaVisitor() {
      @Override
      public boolean visit(IResourceDelta delta) throws CoreException {
        IResource resource = delta.getResource();
        int resourceType = resource.getType();

        if (isAddedFolder(delta, resourceType)) {
          IFolder folder = (IFolder) resource;
          IProject project = resource.getProject();

          if (hasManagedProjectNature(project)) {
            for (ICSourceEntry entry : getSourceEntries(project)) {
              if (isEquivalent(folder, entry)) {
                paths.add(resource);
              }
            }
          }
        }
        return true;
      }
    };
  }

  private static ICSourceEntry[] getSourceEntries(IProject project) {
    ICProjectDescription projDesc = CoreModel.getDefault().getProjectDescription(project);
    ICSourceEntry[] entries = projDesc.getActiveConfiguration().getSourceEntries();
    return entries;
  }

  private static boolean isEquivalent(IFolder folder, ICSourceEntry entry) {
    return entry.getLocation() != null && entry.getLocation().equals(folder.getLocation());
  }

  private static boolean hasManagedProjectNature(IProject project) throws CoreException {
    return project.hasNature(SConsNatureTypes.MANAGED_PROJECT_NATURE.getId());
  }

  private static boolean isAddedFolder(IResourceDelta delta, int resourceType) {
    return delta.getKind() == IResourceDelta.ADDED && resourceType == IResource.FOLDER;
  }
}
