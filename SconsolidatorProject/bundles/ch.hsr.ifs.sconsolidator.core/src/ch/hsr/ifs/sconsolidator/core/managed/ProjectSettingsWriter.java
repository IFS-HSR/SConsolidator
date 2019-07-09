package ch.hsr.ifs.sconsolidator.core.managed;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;


@SuppressWarnings("restriction")
public class ProjectSettingsWriter {

    public static String   GNU_CPP = "GNU C++";
    public static String   GNU_C   = "GNU C";
    private final IProject project;

    public ProjectSettingsWriter(IProject project) {
        this.project = project;
    }

    public void configureCDTProject() throws CoreException {
        ICProjectDescriptionManager pdMgr = CoreModel.getDefault().getProjectDescriptionManager();
        ICProjectDescription projDesc = pdMgr.createProjectDescription(project, true);
        ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(project);
        ManagedProject mProj = new ManagedProject(projDesc);
        info.setManagedProject(mProj);
        Configuration config = getConfigration(mProj);
        IBuilder builder = config.getEditableBuilder();
        builder.setManagedBuildOn(false);
        CConfigurationData data = config.getConfigurationData();
        projDesc.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);
        pdMgr.setProjectDescription(project, projDesc);
    }

    private Configuration getConfigration(ManagedProject mProj) {
        String childId = ManagedBuildManager.calculateChildId("0", null);
        String cfgHolder = new CfgHolder(null, null).getName();
        return new Configuration(mProj, null, childId, cfgHolder);
    }

    public void setIncludePaths(String[] paths, String toolName) throws CoreException {
        setOptionInAllConfigs(IOption.INCLUDE_PATH, paths, toolName);
        ManagedBuildManager.saveBuildInfo(project, true);
    }

    public void setMacros(String[] macros, String toolName) throws CoreException {
        setOptionInAllConfigs(IOption.PREPROCESSOR_SYMBOLS, macros, toolName);
        ManagedBuildManager.saveBuildInfo(project, true);
    }

    private void setOptionInAllConfigs(int optionType, String[] newValues, String toolName) throws CoreException {
        // we check if the project is still in a valid state here; otherwise, we
        // recreate the .cproject file (see ticket #24 for details)
        checkAndConfigProject();
        IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);

        if (info == null || info.getManagedProject() == null) return;

        try {
            for (IConfiguration conf : info.getManagedProject().getConfigurations()) {
                IToolChain tc = conf.getToolChain();
                setOptionInConfig(conf, tc.getOptions(), tc, optionType, newValues);

                for (ITool tool : conf.getTools()) {
                    if (toolName == null || tool.getName().equals(toolName)) {
                        setOptionInConfig(conf, tool.getOptions(), tool, optionType, newValues);
                    }
                }
            }
        } catch (BuildException e) {
            IStatus status = new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, e.getMessage());
            throw new CoreException(status);
        }
    }

    private void setOptionInConfig(IConfiguration config, IOption[] options, IHoldsOptions optionHolder, int optionType, String[] newValues)
            throws BuildException {
        for (IOption option : options) {
            if (option.getValueType() == optionType) {
                ManagedBuildManager.setOption(config, optionHolder, option, newValues);
            }
        }
    }

    private void checkAndConfigProject() throws CoreException {
        ICProjectDescription projectDescription = CoreModel.getDefault().getProjectDescription(project);
        if (projectDescription == null) {
            configureCDTProject();
        }
    }
}
