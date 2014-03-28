package ch.hsr.ifs.sconsolidator.swtbottests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class SWTTestPlugin extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "ch.hsr.ifs.sconsolidator.swttests";
  private static SWTTestPlugin plugin;

  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static SWTTestPlugin getDefault() {
    return plugin;
  }
}
