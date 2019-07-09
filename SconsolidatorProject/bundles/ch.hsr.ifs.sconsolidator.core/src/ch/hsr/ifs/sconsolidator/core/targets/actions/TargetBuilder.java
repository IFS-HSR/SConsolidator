package ch.hsr.ifs.sconsolidator.core.targets.actions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubMonitor;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.console.interactive.InteractiveConsole;
import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;


public final class TargetBuilder {

    private TargetBuilder() {}

    public static void buildTarget(SConsBuildTarget target, IProgressMonitor pm) throws CoreException {
        target.build(SubMonitor.convert(pm, 1));
        rememberLastTarget(target, false);
    }

    public static void buildTargetInteractive(SConsBuildTarget target, IProgressMonitor pm) throws CoreException {
        InteractiveConsole console = getConsole(target.getProject());
        console.startInteractiveMode();
        TargetCommand command = createDefaultTargetCommand(target);
        console.sendCommand(command);
        rememberLastTarget(target, true);
        pm.worked(1);
    }

    private static TargetCommand createDefaultTargetCommand(SConsBuildTarget target) {
        return new TargetCommand(TargetCommand.CommandType.BuildDefaultTarget, null, target.getProject(), target.getAdditionalCmdLineArgs());
    }

    static InteractiveConsole getConsole(IProject project) {
        final InteractiveConsole console = InteractiveConsole.getInstance(project);
        UIUtil.runInDisplayThread(new Runnable() {

            @Override
            public void run() {
                console.show();
            }
        });
        return console;
    }

    private static void rememberLastTarget(SConsBuildTarget target, boolean interactive) throws CoreException {
        IContainer container = target.getContainer();
        IPath path = getTargetPath(target, container);
        String pluginId = SConsPlugin.getPluginId();
        container.setSessionProperty(new QualifiedName(pluginId, "lastTarget"), path.toString());
        container.setSessionProperty(new QualifiedName(pluginId, "lastTargetInteractive"), interactive);
    }

    private static IPath getTargetPath(SConsBuildTarget target, IContainer c) {
        IPath relPath = c.getProjectRelativePath();
        IPath path = relPath.removeFirstSegments(relPath.segmentCount());
        return path.append(target.getTargetName());
    }
}
