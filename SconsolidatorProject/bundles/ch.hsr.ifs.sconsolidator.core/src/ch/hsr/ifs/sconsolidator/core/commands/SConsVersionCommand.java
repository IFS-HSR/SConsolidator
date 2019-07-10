package ch.hsr.ifs.sconsolidator.core.commands;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;


public class SConsVersionCommand extends SConsCommand {

    // --version: Print the SCons version, copyright information, list of
    // authors, and any other relevant information. Then exit.
    private static final String[] DEFAULT_ARGUMENTS = { "--version" };

    public SConsVersionCommand(String binaryPath, SConsConsole console) throws EmptySConsPathException {
        super(binaryPath, null, console, DEFAULT_ARGUMENTS);
    }

    public SConsVersion run(IProgressMonitor pm) throws IOException, InterruptedException {
        run(null, pm);
        return new SConsVersion(getOutput());
    }

    @Override
    protected Collection<String> getArguments() {
        return Collections.emptyList();
    }
}
