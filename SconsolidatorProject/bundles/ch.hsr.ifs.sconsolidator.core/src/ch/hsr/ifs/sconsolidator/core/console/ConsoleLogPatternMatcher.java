package ch.hsr.ifs.sconsolidator.core.console;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.dialogs.PreferencesUtil;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;


public class ConsoleLogPatternMatcher implements IPatternMatchListener {

    private final String fMatch;

    public ConsoleLogPatternMatcher(String match) {
        fMatch = match;
    }

    @Override
    public void matchFound(PatternMatchEvent event) {
        try {
            IOConsole console = (IOConsole) event.getSource();
            IDocument document = console.getDocument();
            int offset = event.getOffset();
            int length = event.getLength();
            String message = document.get(offset, length);
            if (fMatch.equals(message)) {
                console.addHyperlink(new PreferencesHyperlink(), offset, length);
            }
        } catch (BadLocationException e) {
            SConsPlugin.log(e);
        }
    }

    @Override
    public String getPattern() {
        return fMatch;
    }

    @Override
    public void connect(TextConsole console) {}

    @Override
    public void disconnect() {}

    @Override
    public int getCompilerFlags() {
        return 0;
    }

    @Override
    public String getLineQualifier() {
        return null;
    }

    private static class PreferencesHyperlink implements IHyperlink {

        @Override
        public void linkEntered() {}

        @Override
        public void linkExited() {}

        @Override
        public void linkActivated() {
            Shell activeShell = Display.getCurrent().getActiveShell();
            PreferenceDialog consolePreferencesPage = PreferencesUtil.createPreferenceDialogOn(activeShell,
                    "org.eclipse.cdt.ui.preferences.CBuildConsolePreferences", null, null); //$NON-NLS-1$
            consolePreferencesPage.open();
        }
    }
}
