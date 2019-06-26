package ch.hsr.ifs.sconsolidator.swtbottests;

import static ch.hsr.ifs.sconsolidator.core.base.functional.FunctionalHelper.map;
import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.list;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.base.functional.UnaryFunction;

@RunWith(SWTBotJunit4ClassRunner.class)
public class NewManagedProjectWizardTest {
	private static String PROJECT_NAME = "SConsProject";
	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeClass() throws Exception {
		initSWTBot();
		try {
			bot.viewByTitle("Welcome").close();
		} catch (WidgetNotFoundException e) {
			// ignore
		}
		activateCppPerspective();
		setSConsPathInWorkbenchPreferences();
	}

	private static void initSWTBot() {
		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
		bot = new SWTWorkbenchBot();
	}

	private static void activateCppPerspective() {
		bot.perspectiveByLabel("C/C++").activate();
	}

	private static void setSConsPathInWorkbenchPreferences() {
		bot.menu("Window").menu("Preferences").click();
		bot.tree().select("SCons");
		SWTBotText text = bot.textWithLabel("Path to SCons Executable");
		text.setFocus();
		text.typeText("scons");
		bot.button("Cancel").click();
	}

	@After
	public void cleanup() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		map(list(projects), new UnaryFunction<IProject, Void>() {
			@Override
			public Void apply(IProject project) {
				try {
					project.delete(true, new NullProgressMonitor());
				} catch (CoreException e) {
					// Ignore
				}
				return null;
			}
		});
	}

	@SuppressWarnings("nls")
	@Test
	public void testNewSConsExecutableProject() throws CoreException {
		verifyNewlyCreatedProject("SCons executable project");
	}

	@SuppressWarnings("nls")
	@Test
	public void testNewSConsStaticLibProject() throws CoreException {
		verifyNewlyCreatedProject("SCons static library project");
	}

	@SuppressWarnings("nls")
	@Test
	public void testNewSConsSharedLibProject() throws CoreException {
		verifyNewlyCreatedProject("SCons shared library project");
	}

	@SuppressWarnings("nls")
	private void verifyNewlyCreatedProject(String projectType)
			throws CoreException {
		executeProjectWizard(projectType);
		IProject proj = getNewProject();
		assertNotNull("No Project", proj);
		assertTrue(proj.hasNature(SConsNatureTypes.MANAGED_PROJECT_NATURE
				.getId()));
	}

	private IProject getNewProject() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot().getProject(PROJECT_NAME);
	}

	private void executeProjectWizard(String projectType) {
		activateCppPerspective();
		SWTBotShell shell = selectNewCppProject();
		fillValuesInDialog(projectType, shell);
	}

	private void fillValuesInDialog(String projectType, SWTBotShell shell) {
		bot.textWithLabel("Project name:").setText(PROJECT_NAME);
		SWTBotTree swtTree = bot.tree();
		bot.checkBox(
				"Show project types and toolchains only if they are supported on the platform")
				.click();
		SWTBotTreeItem expandedSConsGroup = swtTree.expandNode("SCons");
		expandedSConsGroup.select(projectType);
		bot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell), 10000);
	}

	private SWTBotShell selectNewCppProject() {
		bot.menu("File").menu("New").menu("Project...").click();
		SWTBotShell shell = bot.shell("New Project");
		shell.activate();
		bot.tree().expandNode("C/C++").select("C++ Project");
		bot.button("Next >").click();
		return shell;
	}
}
