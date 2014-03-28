package ch.hsr.ifs.sconsolidator.core.console.interactive.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.IOUtil;

public class DependencyTreeCollector {
  private final IPath asciiTreePath;

  public DependencyTreeCollector(IProject project) {
    asciiTreePath = getAsciiTreePath(project);
  }

  private IPath getAsciiTreePath(IProject project) {
    IPath asciiTreePath = project.getLocation().append(SConsHelper.ASCII_TREE);
    if (!asciiTreePath.toFile().exists())
      throw new IllegalArgumentException("Project must have a dependency tree file");
    return asciiTreePath;
  }

  public String collectTargetDependencies() throws CoreException {
    BufferedReader input = null;

    try {
      input = new BufferedReader(new FileReader(asciiTreePath.toOSString()));
      StringBuilder result = new StringBuilder();
      String line;

      while ((line = input.readLine()) != null) {
        if (line.equals("***FINISHED***")) {
          break;
        }
        result.append(line + PlatformSpecifics.NEW_LINE);
      }
      return result.toString();
    } catch (IOException e) {
      IStatus status =
          new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0,
              "Target dependencies could not be resolved", e);
      throw new CoreException(status);
    } finally {
      IOUtil.safeClose(input);
    }
  }
}
