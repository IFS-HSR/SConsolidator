package ch.hsr.ifs.sconsolidator.depviz;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class DependencyVisualizationPlugin extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "ch.hsr.ifs.sconsolidator.depviz";
  private static DependencyVisualizationPlugin plugin;

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static DependencyVisualizationPlugin getDefault() {
    return plugin;
  }
}
