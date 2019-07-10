package ch.hsr.ifs.sconsolidator.core.targets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;
import ch.hsr.ifs.sconsolidator.core.console.interactive.InteractiveConsole;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;


public class TargetCommand {

    private static String BUILD_COMMAND = "build";
    private static String CLEAN_COMMAND = "clean";

    public enum CommandType {
        BuildDefaultTarget(BUILD_COMMAND), // 
        BuildFileTarget(BUILD_COMMAND), //
        CleanDefaultTarget(CLEAN_COMMAND), //
        CleanFileTarget(CLEAN_COMMAND);

        CommandType(String commandName) {
            this.commandName = commandName;
        }

        @Override
        public String toString() {
            return commandName;
        }

        private String commandName;
    }

    private final CommandType commandType;
    private final String      additionalArgs;
    private final IResource   buildTarget;
    private final IProject    associatedProject;

    public TargetCommand(CommandType commandType, IResource buildTarget, IProject associatedProject, String additionalArgs) {
        this.commandType = commandType;
        this.additionalArgs = additionalArgs;
        this.buildTarget = buildTarget;
        this.associatedProject = associatedProject;
    }

    public TargetCommand(CommandType commandType, IResource buildTarget, IProject associatedProject) {
        this(commandType, buildTarget, associatedProject, null);
    }

    public IResource getBuildTarget() {
        return buildTarget;
    }

    public IProject getAssociatedProject() {
        return associatedProject;
    }

    public String getCommandForConsole(InteractiveConsole console) throws CoreException {
        String target = getTarget(console);
        return String.format("%s%s %s", commandType.toString(), (additionalArgs != null) ? " " + additionalArgs.trim() : "", (target != null) ? target
                .trim() : "");
    }

    private String getTarget(InteractiveConsole console) throws CoreException {
        switch (commandType) {
        case BuildDefaultTarget:
        case CleanDefaultTarget:
            return getDefaultTarget();
        case BuildFileTarget:
        case CleanFileTarget:
            return console.getTarget(buildTarget);
        default:
            throw new IllegalArgumentException("unknown target type");
        }
    }

    private String getDefaultTarget() {
        IPreferenceStore activePreferences = SConsPlugin.getProjectPreferenceStore(associatedProject);
        String defaultTarget = activePreferences.getString(PreferenceConstants.DEFAULT_TARGET);
        return defaultTarget.equals("") ? null : StringUtil.split(defaultTarget).get(0);
    }
}
