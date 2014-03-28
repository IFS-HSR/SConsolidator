package ch.hsr.ifs.sconsolidator.core.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;

public class CompileErrorPatternMatcher implements IPatternMatchListener {
  private final IProject project;
  private final String startPath;

  public CompileErrorPatternMatcher(IProject project) {
    this.project = project;
    startPath = SConsHelper.determineStartingDirectory(project);
  }

  @Override
  public int getCompilerFlags() {
    return 0;
  }

  @Override
  public String getLineQualifier() {
    return null;
  }

  @Override
  public String getPattern() {
    return PlatformSpecifics.CPP_RE;
  }

  @Override
  public void connect(TextConsole console) {}

  @Override
  public void disconnect() {}

  @Override
  public void matchFound(PatternMatchEvent event) {
    try {
      IOConsole console = (IOConsole) event.getSource();
      IDocument document = console.getDocument();
      int offset = event.getOffset();
      int length = event.getLength();
      String message = document.get(offset, length);
      String fileName = message.substring(0, message.indexOf(':'));
      IFile file = findFile(fileName);

      if (file != null && file.exists()) {
        String lineNumber = message.substring(message.indexOf(':') + 1);
        FileLink link = new FileLink(file, null, -1, -1, Integer.parseInt(lineNumber));
        console.addHyperlink(link, offset, length);
      }
    } catch (BadLocationException e) {
      SConsPlugin.log(e);
    }
  }

  private IFile findFile(String fileName) {
    IResource foundMember = project.findMember(fileName);

    if (foundMember == null) {
      foundMember = findRelativeToSConsProject(fileName);

      if (foundMember == null) {
        foundMember = findInWorkspace(fileName);
      }
    }
    return (IFile) foundMember;
  }

  private IResource findInWorkspace(String fileName) {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    return workspaceRoot.getFileForLocation(new Path(startPath).append(fileName));
  }

  private IResource findRelativeToSConsProject(String fileName) {
    IPath projLoc = project.getLocation();
    if (projLoc == null)
      return null;
    IPath path = projLoc.makeRelativeTo(new Path(startPath));
    String newPath = new Path(fileName).makeRelativeTo(path).toOSString();
    return project.findMember(newPath);
  }
}
