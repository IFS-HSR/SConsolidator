package ch.hsr.ifs.sconsolidator.swtbottests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ImportExistingCodeWizardTest {
  private static String PROJECT_NAME = "SConsProject";
  private static SWTWorkbenchBot bot;

  @BeforeClass
  public static void beforeClass() throws Exception {
    bot = new SWTWorkbenchBot();
  }

  @SuppressWarnings("nls")
  @Test
  public void testImportOfExistingCode() throws CoreException, IOException {
    importExistingSConsProject();
    IProject proj = getNewProject();
    assertNotNull("No Project", proj);
    assertTrue(proj.hasNature(SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE.getId()));
  }

  private void importExistingSConsProject() throws IOException {
    activateCppPerspective();
    SWTBotShell shell = createNewProject();
    selectNewSConsProject();
    fillValuesInImportDialog(shell);
  }

  private void fillValuesInImportDialog(SWTBotShell shell) throws IOException {
    shell.pressShortcut(SWT.ALT, 'p');
    bot.text(1).setText(PROJECT_NAME);
    shell.pressShortcut(SWT.ALT, 'e');
    URL location = getTestProjectUrl();
    bot.text(1).setText(FileLocator.toFileURL(location).getPath());
    bot.button("Finish").click();
    bot.waitUntil(Conditions.shellCloses(shell), 10000);
  }

  private void selectNewSConsProject() {
    bot.tree().expandNode("C/C++").select("New SCons project from existing source");
    bot.button("Next >").click();
  }

  private SWTBotShell createNewProject() {
    bot.menu("File").menu("New").menu("Project...").click();
    SWTBotShell shell = bot.shell("New Project");
    shell.activate();
    return shell;
  }

  private void activateCppPerspective() {
    bot.perspectiveByLabel("C/C++").activate();
  }

  private URL getTestProjectUrl() {
    return SWTTestPlugin.getDefault().getBundle().getResource("sconsTestProject");
  }

  private IProject getNewProject() {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    return workspace.getRoot().getProject(PROJECT_NAME);
  }
}
