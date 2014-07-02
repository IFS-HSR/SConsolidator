package ch.hsr.ifs.sconsolidator.core;

import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.USE_PARENT_SUFFIX;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceInitializer;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetManager;

public class SConsPlugin extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "ch.hsr.ifs.sconsolidator.core";
  private static SConsPlugin plugin;
  private IPersistentPreferenceStore workspacePrefStore;
  private IPersistentPreferenceStore configPrefStore;
  private SConsBuildTargetManager targetManager;

  public static IPreferenceStore getActivePreferences(IProject project, String settingsPage) {
    IPreferenceStore settings = getProjectPreferenceStore(project);
    boolean useWorkspacePref = settings.getBoolean(settingsPage + USE_PARENT_SUFFIX);

    if (useWorkspacePref) {
      settings = getWorkspacePreferenceStore();
    }

    return settings;
  }

  public static IPersistentPreferenceStore getConfigPreferenceStore() {
    return getDefault().getInternalConfigPrefStore();
  }

  public static SConsPlugin getDefault() {
    return plugin;
  }

  public static String getPluginId() {
    return getDefault().getBundle().getSymbolicName();
  }

  public static IPersistentPreferenceStore getProjectPreferenceStore(IProject project) {
    ProjectScope ps = new ProjectScope(project);
    ScopedPreferenceStore scoped = new ScopedPreferenceStore(ps, getPluginId());
    PreferenceInitializer.initializePropertiesDefault(scoped);
    return scoped;
  }

  public static IPersistentPreferenceStore getWorkspacePreferenceStore() {
    return getDefault().getInternalWorkspacePreferenceStore();
  }

  public static void log(int severity, int style, String message, Throwable exception) {
    IStatus status = new Status(severity, getPluginId(), 1, message, exception);
    StatusManager.getManager().handle(status, style);
    SConsPlugin.getDefault().getLog().log(status);
  }

  public static void log(final String message) {
    log(IStatus.INFO, StatusManager.LOG, message, null);
  }

  public static void log(final Throwable e) {
    log(IStatus.ERROR, StatusManager.LOG, SConsI18N.SConsPlugin_InternalErrorMsg, e);
  }

  private IPersistentPreferenceStore getInternalConfigPrefStore() {
    if (configPrefStore == null) {
      configPrefStore = new ScopedPreferenceStore(ConfigurationScope.INSTANCE, getPluginId());
    }
    return configPrefStore;
  }

  private IPersistentPreferenceStore getInternalWorkspacePreferenceStore() {
    if (workspacePrefStore == null) {
      workspacePrefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, getPluginId());
    }
    return workspacePrefStore;
  }

  public SConsBuildTargetManager getSConsTargetManager() {
    if (targetManager == null) {
      targetManager = new SConsBuildTargetManager();
      targetManager.startup();
    }
    return targetManager;
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    try {
      if (targetManager != null) {
        targetManager.shutdown();
        targetManager = null;
      }
    } finally {
      plugin = null;
      super.stop(context);
    }
  }

  public static void showExceptionInDisplayThread(final Exception e) {
    UIUtil.runInDisplayThread(new Runnable() {
      @Override
      public void run() {
        showException(e);
      }
    });
  }

  public static void showExceptionInDisplayThread(final String title, final String message,
      final Throwable throwable) {
    UIUtil.runInDisplayThread(new Runnable() {
      @Override
      public void run() {
        showException(title, message, throwable);
      }
    });
  }

  private static void showException(String title, String message, Throwable throwable) {
    IStatus status =
        new Status(IStatus.ERROR, SConsPlugin.getPluginId(), IStatus.OK,
            (throwable.getMessage() == null) ? throwable.getClass().getName()
                : throwable.getMessage(), throwable);
    SConsPlugin.getDefault().getLog().log(status);
    ErrorDialog.openError(UIUtil.getWindowShell(), title, message, status);
  }

  private static void showException(Exception e) {
    showException(SConsI18N.UIUtils_ExceptionTitleExceptionCaughtTitle,
        SConsI18N.UIUtils_ExceptionTitleExceptionCaughtMessage, e);
  }
}
