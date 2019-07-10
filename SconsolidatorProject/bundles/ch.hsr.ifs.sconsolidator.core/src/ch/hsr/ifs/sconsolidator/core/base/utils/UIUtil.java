package ch.hsr.ifs.sconsolidator.core.base.utils;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


public final class UIUtil {

    private UIUtil() {}

    public static void runInDisplayThread(Runnable runnable) {
        Display display = PlatformUI.getWorkbench().getDisplay();
        if (display == null || display.isDisposed()) return;

        if (isCurrentThreadDisplayThread(display)) {
            runnable.run();
        } else {
            display.syncExec(runnable);
        }
    }

    private static boolean isCurrentThreadDisplayThread(Display display) {
        return Thread.currentThread().equals(display.getThread());
    }

    public static Shell getWindowShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }
}
