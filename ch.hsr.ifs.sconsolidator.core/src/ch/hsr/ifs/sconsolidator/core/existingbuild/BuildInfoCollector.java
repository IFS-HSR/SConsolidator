package ch.hsr.ifs.sconsolidator.core.existingbuild;

import static ch.hsr.ifs.sconsolidator.core.SConsHelper.BUILD_INFO_COLLECTOR;
import static ch.hsr.ifs.sconsolidator.core.SConsHelper.SCONS_FILES_DIR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.FileUtil;
import ch.hsr.ifs.sconsolidator.core.commands.BuildInfoCollectorCommand;
import ch.hsr.ifs.sconsolidator.core.console.BuildConsole;
import ch.hsr.ifs.sconsolidator.core.managed.ProjectSettingsWriter;
import ch.hsr.ifs.sconsolidator.core.preferences.pages.ExecutableNotFoundHandler;

public class BuildInfoCollector extends WorkspaceModifyOperation {
  private final IProject project;

  public BuildInfoCollector(IProject project) {
    this.project = project;
  }

  @Override
  protected void execute(IProgressMonitor pm) throws CoreException, InvocationTargetException,
      InterruptedException {
    pm.beginTask(SConsI18N.ExtractProjectInformationAction_ExtractingInProgressMessage,
        IProgressMonitor.UNKNOWN);

    try {
      copyProjectInfoScript();

      if (pm.isCanceled())
        return;

      String output = runSCons(pm);

      if (pm.isCanceled())
        return;

      addSConsInfosToProject(output);
    } catch (EmptySConsPathException e) {
      ExecutableNotFoundHandler.handleError();
    } catch (Exception e) {
      SConsPlugin.log(e);
      IStatus status =
          new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0,
              SConsI18N.ExtractProjectInformationAction_ExtractingFailedMessage, e);
      throw new CoreException(status);
    } finally {
      cleanUp();
      pm.done();
    }
  }

  private String runSCons(IProgressMonitor pm) throws EmptySConsPathException, IOException,
      InterruptedException {
    BuildConsole console = new BuildConsole(project);
    BuildConsole.showConsole(console);
    BuildInfoCollectorCommand command = new BuildInfoCollectorCommand(console, project);
    return command.run(project.getLocation().toFile(), new SubProgressMonitor(pm, 1));
  }

  private void copyProjectInfoScript() throws CoreException {
    String srcPath = SCONS_FILES_DIR + File.separator + BUILD_INFO_COLLECTOR;
    String destPath = project.getLocation().toOSString() + File.separator + BUILD_INFO_COLLECTOR;
    FileUtil.copyBundleFile(new Path(srcPath), destPath);
  }

  private void addSConsInfosToProject(String output) throws FileNotFoundException, CoreException {
    BuildInfoParser parser = new BuildInfoParser(output);
    ProjectSettingsWriter writer = new ProjectSettingsWriter(project);
    writer.setIncludePaths(parser.getCIncludes(), ProjectSettingsWriter.GNU_C);
    writer.setIncludePaths(parser.getCPPIncludes(), ProjectSettingsWriter.GNU_CPP);
    writer.setMacros(parser.getCMacros(), ProjectSettingsWriter.GNU_C);
    writer.setMacros(parser.getCPPMacros(), ProjectSettingsWriter.GNU_CPP);
  }

  private void cleanUp() {
    String extractorPath =
        SConsHelper.findFileAbovePath(project.getLocation().toFile(), BUILD_INFO_COLLECTOR);
    if (extractorPath == null)
      return;
    FileUtil.safelyDeleteFile(extractorPath + File.separator + BUILD_INFO_COLLECTOR);
  }

  private static class BuildInfoParser {
    private final String buildInfos;
    private final Collection<String> cIncludes;
    private final Collection<String> cPPIncludes;
    private final Collection<String> cMacros;
    private final Collection<String> cPPMacros;

    public BuildInfoParser(String buildInfos) throws FileNotFoundException {
      this.buildInfos = buildInfos;
      cIncludes = new ArrayList<String>();
      cPPIncludes = new ArrayList<String>();
      cMacros = new ArrayList<String>();
      cPPMacros = new ArrayList<String>();
      parse();
    }

    private void parse() throws FileNotFoundException {
      Scanner scanner = new Scanner(buildInfos);

      try {
        while (scanner.hasNextLine()) {
          processLine(scanner.nextLine());
        }
      } finally {
        scanner.close();
      }
    }

    private void processLine(String line) {
      Scanner s = new Scanner(line);

      try {
        s.useDelimiter(" = ");

        if (!s.hasNext())
          return;

        String name = s.next().trim();

        if (!s.hasNext())
          return;

        String list = s.next();

        if (name.equals("USER_INCLUDES")) {
          cIncludes.addAll(processBuildValuesList(list));
          cPPIncludes.addAll(processBuildValuesList(list));
        } else if (name.equals("SYS_CPP_INCLUDES")) {
          cPPIncludes.addAll(processBuildValuesList(list));
        } else if (name.equals("SYS_C_INCLUDES")) {
          cIncludes.addAll(processBuildValuesList(list));
        } else if (name.equals("MACROS")) {
          cMacros.addAll(processBuildValuesList(list));
          cPPMacros.addAll(processBuildValuesList(list));
        } else if (name.equals("SYS_C_MACROS")) {
          cMacros.addAll(processBuildValuesList(list));
        } else if (name.equals("SYS_CPP_MACROS")) {
          cPPMacros.addAll(processBuildValuesList(list));
        }
      } finally {
        s.close();
      }
    }

    private Collection<String> processBuildValuesList(String values) {
      List<String> elements = new ArrayList<String>();
      Scanner s = new Scanner(removeListBrackets(values));

      try {
        s.useDelimiter(",");

        while (s.hasNext()) {
          String value = s.next();
          elements.add(value.substring(1, value.length() - 1));
        }
      } finally {
        s.close();
      }

      return elements;
    }

    private String removeListBrackets(String list) {
      return list.replace("[", "").replace("]", "");
    }

    private String[] getCIncludes() {
      return cIncludes.toArray(new String[0]);
    }

    private String[] getCPPIncludes() {
      return cPPIncludes.toArray(new String[0]);
    }

    private String[] getCMacros() {
      return cMacros.toArray(new String[0]);
    }

    private String[] getCPPMacros() {
      return cPPMacros.toArray(new String[0]);
    }
  }
}
