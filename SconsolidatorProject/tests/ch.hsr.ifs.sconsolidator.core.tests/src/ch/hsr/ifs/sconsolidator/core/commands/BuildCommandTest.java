package ch.hsr.ifs.sconsolidator.core.commands;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.console.NullConsole;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;


public class BuildCommandTest {

    private static final String          ADDITIONAL_CMD_LINE_ARGS = "-m -D";
    private static final String[]        EXPECTED_ARGS            = new String[] { String.format("--jobs=%d", SConsHelper.getNumOfPreferredJobs()),
                                                                                   "-m", "-D" };
    private static CppManagedTestProject testProject;
    private BuildCommand                 buildCommand;

    @BeforeClass
    public static void beforeClass() throws Exception {
        String sconsPath = PlatformSpecifics.findSConsExecOnSystemPath().getAbsolutePath();
        SConsPlugin.getConfigPreferenceStore().setValue(PreferenceConstants.EXECUTABLE_PATH, sconsPath);
        testProject = new CppManagedTestProject(true);
        initNumberOfJobs();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        assertNotNull("TestProject is null", testProject);
        testProject.dispose();
    }

    @Test
    public void testCreateWithoutTarget() throws Exception {
        buildCommand = new BuildCommand(getSConsPath(), new NullConsole(), testProject.getProject(), null, ADDITIONAL_CMD_LINE_ARGS);
        assertArrayEquals(EXPECTED_ARGS, buildCommand.getArguments().toArray(new String[0]));
    }

    private String getSConsPath() {
        return PlatformSpecifics.findSConsExecOnSystemPath().getAbsolutePath();
    }

    @Test
    public void testCreateWithTarget() throws Exception {
        buildCommand = new BuildCommand(getSConsPath(), new NullConsole(), testProject.getProject(), "hello", ADDITIONAL_CMD_LINE_ARGS);
        String[] expectedArgsWithTarget = new String[EXPECTED_ARGS.length + 1];
        System.arraycopy(EXPECTED_ARGS, 0, expectedArgsWithTarget, 0, EXPECTED_ARGS.length);
        expectedArgsWithTarget[expectedArgsWithTarget.length - 1] = "hello";
        assertArrayEquals(expectedArgsWithTarget, buildCommand.getArguments().toArray(new String[0]));
    }

    @Test
    public void testRun() throws Exception {
        File projectPath = testProject.getProject().getLocation().toFile();
        buildCommand = new BuildCommand(getSConsPath(), new NullConsole(), testProject.getProject(), null, ADDITIONAL_CMD_LINE_ARGS);
        buildCommand.run(projectPath, new NullProgressMonitor());
        assertTrue(new File(projectPath + File.separator + "hello").exists());
        assertTrue(new File(projectPath + File.separator + "src/main.o").exists());
        String output = buildCommand.getOutput();
        assertEquals(
                "scons: Reading SConscript files ...\nscons: done reading SConscript files.\nscons: Building targets ...\ng++ -o src/main.o -c src/main.cpp\ng++ -o hello src/main.o\nscons: done building targets.\n",
                output);
        String error = buildCommand.getError();
        assertTrue(error.startsWith("Warning:  ignoring -m option"));
    }

    private static void initNumberOfJobs() {
        final IPreferenceStore preferences = SConsPlugin.getActivePreferences(testProject.getProject(), PreferenceConstants.BUILD_SETTINGS_PAGE_ID);
        preferences.putValue(PreferenceConstants.NUMBER_OF_JOBS, "" + SConsHelper.getNumOfPreferredJobs());
    }
}
