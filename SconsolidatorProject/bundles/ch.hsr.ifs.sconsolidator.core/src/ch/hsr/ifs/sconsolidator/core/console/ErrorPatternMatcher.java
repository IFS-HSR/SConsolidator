package ch.hsr.ifs.sconsolidator.core.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.TextConsole;

import ch.hsr.ifs.sconsolidator.core.SConsHelper;


public abstract class ErrorPatternMatcher implements IPatternMatchListener {

    protected final IProject project;
    protected final String   startPath;

    public ErrorPatternMatcher(IProject project) {
        super();
        this.project = project;
        this.startPath = SConsHelper.determineStartingDirectory(project);
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
    public void connect(TextConsole console) {}

    @Override
    public void disconnect() {}

    protected IFile findFile(String fileName) {
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
        if (projLoc == null) return null;
        IPath path = projLoc.makeRelativeTo(new Path(startPath));
        String newPath = new Path(fileName).makeRelativeTo(path).toOSString();
        return project.findMember(newPath);
    }

}
