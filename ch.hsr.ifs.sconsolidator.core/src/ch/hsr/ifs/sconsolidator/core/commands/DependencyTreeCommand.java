package ch.hsr.ifs.sconsolidator.core.commands;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;

public class DependencyTreeCommand extends SConsCommand {
  // -s: silent. Do not print commands that are executed to rebuild target
  // files. Also suppresses SCons status messages.
  //
  // --tree=prune: Prunes the tree to avoid repeating dependency information
  // for nodes that have already been displayed. Any node that has already
  // been displayed will have its name printed in [square brackets], as an
  // indication that the dependencies for that node can be found by searching
  // for the relevant output higher up in the tree.
  private static final String[] DEFAULT_ARGUMENTS = {"-u", "--tree=prune", "-s"};   //$NON-NLS-3$ 
  private final Collection<String> arguments;

  public DependencyTreeCommand(SConsConsole console, IProject project, String targetPath)
      throws EmptySConsPathException {
    this(null, console, project, targetPath);
  }

  public DependencyTreeCommand(String binaryPath, SConsConsole console, IProject project,
      String targetPath) throws EmptySConsPathException {
    super(binaryPath, project, console, DEFAULT_ARGUMENTS);
    arguments = new LinkedList<String>();
    arguments.addAll(getAdditionalSConsOptions(project));
    arguments.addAll(StringUtil.split(targetPath));
  }

  public String run(File workingDir) throws IOException, InterruptedException {
    run(workingDir, new NullProgressMonitor());
    return getOutput();
  }

  @Override
  protected Collection<String> getArguments() {
    return arguments;
  }
}
