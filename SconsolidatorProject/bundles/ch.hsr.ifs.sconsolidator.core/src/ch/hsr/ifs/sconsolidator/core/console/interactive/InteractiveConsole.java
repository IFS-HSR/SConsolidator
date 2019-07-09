package ch.hsr.ifs.sconsolidator.core.console.interactive;

import static ch.hsr.ifs.sconsolidator.core.base.functional.FunctionalHelper.map;
import static ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil.join;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.resources.ACBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.actions.CloseConsoleAction;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.SConsTwoStepBuild;
import ch.hsr.ifs.sconsolidator.core.base.functional.UnaryFunction;
import ch.hsr.ifs.sconsolidator.core.base.utils.FileUtil;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;
import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.console.CompileErrorPatternMatcher;
import ch.hsr.ifs.sconsolidator.core.console.FortranErrorPatternMatcher;
import ch.hsr.ifs.sconsolidator.core.console.interactive.actions.BuildCurrentTargetAction;
import ch.hsr.ifs.sconsolidator.core.console.interactive.actions.CleanCurrentTargetAction;
import ch.hsr.ifs.sconsolidator.core.console.interactive.actions.RedoLastTargetAction;
import ch.hsr.ifs.sconsolidator.core.console.interactive.actions.TerminateProcessAction;
import ch.hsr.ifs.sconsolidator.core.console.interactive.io.DependencyTreeCollector;
import ch.hsr.ifs.sconsolidator.core.console.interactive.io.SConsPromptWait;
import ch.hsr.ifs.sconsolidator.core.console.interactive.io.StreamCommunicationHandler;
import ch.hsr.ifs.sconsolidator.core.depanalysis.DependencyTreeAnalyzer;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;
import ch.hsr.ifs.sconsolidator.core.preferences.SConsOptionHandler;
import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;


public final class InteractiveConsole extends IOConsole {

    private static final String        CDT_ERROR_MARKER_TYPE = "org.eclipse.cdt.core.problem";
    private final IProject             project;
    private TerminateProcessAction     terminateAction;
    private CloseConsoleAction         closeAction;
    private RedoLastTargetAction       redoAction;
    private BuildCurrentTargetAction   buildCurrentAction;
    private CleanCurrentTargetAction   cleanCurrentAction;
    private TargetCommand              lastTargetCommand;
    private Process                    process;
    private SourceFileEditorListener   sourceFileListener;
    private DependencyTreeAnalyzer     analyzer;
    private ErrorParserManager         epm;
    private StreamCommunicationHandler streamHandler;

    private InteractiveConsole(String name, ImageDescriptor imageDesc, IProject project) {
        super(name, imageDesc);
        this.project = project;
        initToolbarActions();
        initErrorParserManager();
        addCompileErrorListener();
        registerActiveTargetListener();
        registerConsole();
    }

    private void registerConsole() {
        IConsoleManager manager = getConsoleManager();
        manager.addConsoleListener(new ConsoleListener());
        manager.addConsoles(new IConsole[] { this });
    }

    private void addCompileErrorListener() {
        addPatternMatchListener(new CompileErrorPatternMatcher(project));
        addPatternMatchListener(new FortranErrorPatternMatcher(project));
    }

    private void initToolbarActions() {
        terminateAction = new TerminateProcessAction(this);
        closeAction = new CloseConsoleAction(this);
        redoAction = new RedoLastTargetAction(this);
        buildCurrentAction = new BuildCurrentTargetAction();
        cleanCurrentAction = new CleanCurrentTargetAction();
    }

    public ErrorParserManager getErrorParserManager() {
        return epm;
    }

    public void startInteractiveMode() throws CoreException {
        if (isProcessStarted()) return;

        try {
            copyDepAnalyzerScript();
            startSConsProcess();
            initStreamCommunication();
            waitForSConsPrompt();
            initTargetDependencies();
            startStreamPumpers();
            onProcessStart();
        } finally {
            cleanUp();
        }
    }

    private void initErrorParserManager() {
        epm = new ErrorParserManager(project, new ACBuilder() {

            @Override
            protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor) throws CoreException {
                return null;
            }
        }, getErrorParsersForProject());
    }

    private String[] getErrorParsersForProject() {
        IConfiguration conf = ManagedBuildManager.getBuildInfo(project).getDefaultConfiguration();
        if (conf == null) throw new IllegalArgumentException("Project does not contain a valid CDT configuration");
        return conf.getErrorParserList();
    }

    private static void showConsoleIfNecessary(IConsole console) {
        if (shouldOpenConsole()) {
            getConsoleManager().showConsoleView(console);
        }
    }

    private void initTargetDependencies() throws CoreException {
        DependencyTreeCollector collector = new DependencyTreeCollector(project);
        analyzer = new DependencyTreeAnalyzer(collector.collectTargetDependencies());
    }

    private void cleanUp() {
        FileUtil.safelyDeleteFile(project.getLocation().append(SConsHelper.ASCII_TREE).toOSString());
        FileUtil.safelyDeleteFile(getTargetLocationForDepAnalyzer());
    }

    private void copyDepAnalyzerScript() throws CoreException {
        FileUtil.copyBundleFile(new Path(getDepAnalyzerFilePath()), getTargetLocationForDepAnalyzer());
    }

    private String getTargetLocationForDepAnalyzer() {
        return project.getLocation().append(SConsHelper.DEPENDENCY_ANALYZER).toOSString();
    }

    private String getDepAnalyzerFilePath() {
        return SConsHelper.SCONS_FILES_DIR + File.separator + SConsHelper.DEPENDENCY_ANALYZER;
    }

    private void startSConsProcess() throws CoreException {
        try {
            ProcessBuilder builder = createInteractiveModeProcess();
            process = builder.start();
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, SConsPlugin.getPluginId(), e.getMessage(), e);
            throw new CoreException(status);
        }
    }

    public static InteractiveConsole getInstance(IProject project) {
        InteractiveConsole console = getInteractiveConsole(project);
        if (console == null) {
            ImageDescriptor img = SConsImages.getImageDescriptor(SConsImages.LOGO);
            String consoleName = getConsoleName(project);
            console = new InteractiveConsole(consoleName, img, project);
        }
        showConsoleIfNecessary(console);
        return console;
    }

    private static InteractiveConsole getInteractiveConsole(IProject project) {
        String consoleName = getConsoleName(project);

        for (IConsole console : getConsoleManager().getConsoles()) {
            if (consoleName.equals(console.getName())) return (InteractiveConsole) console;
        }

        return null;
    }

    private static String getConsoleName(IProject project) {
        return NLS.bind(SConsI18N.InteractiveConsole_ConsoleName, project.getName());
    }

    private static IConsoleManager getConsoleManager() {
        return ConsolePlugin.getDefault().getConsoleManager();
    }

    private void initStreamCommunication() {
        streamHandler = new StreamCommunicationHandler(this, process);
    }

    private void registerActiveTargetListener() {
        sourceFileListener = new SourceFileEditorListener(this, project, cleanCurrentAction, buildCurrentAction);
        // needs to run in UI thread because getActiveWorkbenchWindow returns null otherwise
        UIUtil.runInDisplayThread(new Runnable() {

            @Override
            public void run() {
                getActivePage().addPartListener(sourceFileListener);
            }
        });
    }

    private void unregisterActiveTargetListener() {
        UIUtil.runInDisplayThread(new Runnable() {

            @Override
            public void run() {
                getActivePage().removePartListener(sourceFileListener);
            }
        });
    }

    private ProcessBuilder createInteractiveModeProcess() throws EmptySConsPathException {
        List<String> command = getInteractiveModeCommand(getBinaryPath());
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(new File(project.getLocation().toOSString()));
        return builder;
    }

    private List<String> getInteractiveModeCommand(String binaryPath) {
        SConsTwoStepBuild twoStepBuild = new SConsTwoStepBuild(project, getTargetLocationForDepAnalyzer());
        List<String> command = StringUtil.split(binaryPath, "-u --interactive");
        command.addAll(twoStepBuild.getCommandLine());
        command.addAll(new SConsOptionHandler(project).getCommandLineOptions());
        SConsPlugin.log(StringUtil.join(command, " "));
        return command;
    }

    private String getBinaryPath() throws EmptySConsPathException {
        String binaryPath = SConsPlugin.getConfigPreferenceStore().getString(PreferenceConstants.EXECUTABLE_PATH);

        if (binaryPath.isEmpty()) throw new EmptySConsPathException();

        return binaryPath;
    }

    private void waitForSConsPrompt() {
        SConsPromptWait waiter = new SConsPromptWait(process.getInputStream(), streamHandler.getConsoleOutputStream());
        waiter.waitForSConsPrompt();
    }

    public TerminateProcessAction getTerminateAction() {
        return terminateAction;
    }

    public CloseConsoleAction getCloseAction() {
        return closeAction;
    }

    public RedoLastTargetAction getRedoLastTargetAction() {
        return redoAction;
    }

    public BuildCurrentTargetAction getBuildCurrentTargetAction() {
        return buildCurrentAction;
    }

    public CleanCurrentTargetAction getCleanCurrentTargetAction() {
        return cleanCurrentAction;
    }

    private void startStreamPumpers() {
        streamHandler.startPumpers();
    }

    public void stopInteractiveMode() {
        terminateProcess();
        onProcessFinish();
        unregisterActiveTargetListener();
        removeConsole();
    }

    private void removeConsole() {
        IConsoleManager manager = getConsoleManager();
        manager.removeConsoles(new IConsole[] { this });
    }

    public void terminateProcess() {
        if (!isProcessStarted()) return;

        closeStreams();
        stopPumpers();
        process.destroy();
        process = null;
    }

    void restartInteractiveMode() throws CoreException {
        stopInteractiveMode();
        startInteractiveMode();
    }

    private void stopPumpers() {
        streamHandler.stopPumpers();
    }

    public void stopInputPumper() {
        streamHandler.stopInputPumper();
    }

    public void closeStreams() {
        streamHandler.closeStreams();
    }

    private void onProcessStart() {
        UIUtil.runInDisplayThread(new Runnable() {

            @Override
            public void run() {
                terminateAction.setEnabled(true);
                closeAction.setEnabled(false);
            }
        });
    }

    public void onProcessFinish() {
        UIUtil.runInDisplayThread(new Runnable() {

            @Override
            public void run() {
                if (!terminateAction.isEnabled()) return;
                terminateAction.setEnabled(false);
                redoAction.setEnabled(false);
                buildCurrentAction.setEnabled(false);
                cleanCurrentAction.setEnabled(false);
                closeAction.setEnabled(true);
                setName(SConsI18N.InteractiveConsole_ConsoleTerminatedPrefix + getName());
            }
        });
    }

    public void sendCommand(TargetCommand command) throws CoreException {
        String commandString = command.getCommandForConsole(this);
        byte[] commandLine = (commandString + "\n").getBytes();
        deleteAllMarkers(command);
        streamHandler.writeCommand(commandLine);
        handleRedo(command, commandString);
    }

    private void handleRedo(TargetCommand command, String commandString) {
        redoAction.setEnabled(true);
        redoAction.setToolTipText(String.format("Redo '%s'", commandString));
        lastTargetCommand = command;
    }

    private void deleteAllMarkers(TargetCommand command) {
        try {
            if (command.getBuildTarget() != null) {
                deleteMarkerForTarget(command);
            } else if (command.getAssociatedProject() != null) {
                deleteAllMarkersInProject(command.getAssociatedProject());
            }
        } catch (CoreException e) {
            SConsPlugin.log(e);
        }
    }

    private void deleteAllMarkersInProject(IProject project) throws CoreException {
        project.deleteMarkers(CDT_ERROR_MARKER_TYPE, true, IResource.DEPTH_INFINITE);
    }

    private void deleteMarkerForTarget(TargetCommand command) throws CoreException {
        command.getBuildTarget().deleteMarkers(CDT_ERROR_MARKER_TYPE, false, IResource.DEPTH_ZERO);
    }

    public void redoLastTargetAction() throws CoreException {
        sendCommand(lastTargetCommand);
    }

    public void show() {
        if (!shouldOpenConsole()) return;

        try {
            IConsoleView view = (IConsoleView) getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW);
            view.display(this);
        } catch (PartInitException e) {
            SConsPlugin.showExceptionInDisplayThread(e);
        }
    }

    private IWorkbenchPage getActivePage() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    }

    private static boolean shouldOpenConsole() {
        return SConsPlugin.getConfigPreferenceStore().getBoolean(PreferenceConstants.OPEN_CONSOLE_WHEN_BUILDING);
    }

    private class ConsoleListener implements IConsoleListener {

        @Override
        public void consolesAdded(IConsole[] consoles) {}

        @Override
        public void consolesRemoved(IConsole[] consoles) {
            for (IConsole console : consoles) {
                if (InteractiveConsole.this == console) {
                    getConsoleManager().removeConsoleListener(this);
                    stopInteractiveMode();
                }
            }
        }
    }

    public String getTarget(IResource target) throws CoreException {
        Collection<String> targets = collectTargets(target);
        if (targets.isEmpty()) throw new CoreException(new Status(IStatus.ERROR, SConsPlugin.getPluginId(),
                SConsI18N.InteractiveConsole_CouldNotDetermineTargetErrorMessage));
        return normalizeTargets(targets);
    }

    private Collection<String> collectTargets(IResource target) {
        String sourcePath = target.getRawLocation().makeRelativeTo(project.getLocation()).toOSString();
        if (target.getType() == IResource.FOLDER) return Arrays.asList(sourcePath);
        return analyzer.collectTargets(sourcePath);
    }

    private String normalizeTargets(Collection<String> targets) {
        return join(map(targets, new UnaryFunction<String, String>() {

            @Override
            public String apply(final String target) {
                if (new File(target).isAbsolute()) return target;
                return project.getLocation().append(target).toOSString();
            }
        }), " ");
    }

    public boolean isProcessStarted() {
        if (process == null) return false;

        try {
            process.exitValue();
        } catch (IllegalThreadStateException e) {
            // expected when not yet terminated
            return true;
        }
        return false;
    }
}
