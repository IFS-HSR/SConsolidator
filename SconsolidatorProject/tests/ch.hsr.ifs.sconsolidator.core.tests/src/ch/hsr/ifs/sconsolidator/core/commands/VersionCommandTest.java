package ch.hsr.ifs.sconsolidator.core.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.console.NullConsole;


public class VersionCommandTest {

    private SConsVersionCommand versionCommand;

    @Before
    public void setUp() throws Exception {
        versionCommand = new SConsVersionCommand(getSConsPath(), new NullConsole());
    }

    private String getSConsPath() {
        return PlatformSpecifics.findSConsExecOnSystemPath().getAbsolutePath();
    }

    @Test
    public void testCreate() throws Exception {
        assertTrue(versionCommand.getArguments().isEmpty());
    }

    @Test
    public void testRun() throws Exception {
        SConsVersion version = versionCommand.run(new NullProgressMonitor());
        assertNotNull(version);
        assertTrue(version.toString().matches("\\d+\\.\\d+\\.\\d+"));
    }
}
