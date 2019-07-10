package ch.hsr.ifs.sconsolidator.core.console;

import java.util.regex.Matcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;


public class FortranErrorPatternMatcher extends ErrorPatternMatcher implements IPatternMatchListener {

    public FortranErrorPatternMatcher(IProject project) {
        super(project);
    }

    @Override
    public void matchFound(PatternMatchEvent event) {
        try {
            IOConsole console = (IOConsole) event.getSource();
            IDocument document = console.getDocument();
            int offset = event.getOffset();
            int length = event.getLength();
            String message = document.get(offset, length);
            Matcher m = PlatformSpecifics.FORT_RE.matcher(message);
            if (!m.matches()) return;
            String fileName = m.group(1);
            IFile file = findFile(fileName);

            if (file != null && file.exists()) {
                String lineNumber = m.group(2);
                FileLink link = new FileLink(file, null, -1, -1, Integer.parseInt(lineNumber));
                console.addHyperlink(link, offset, length);
            }
        } catch (BadLocationException e) {
            SConsPlugin.log(e);
        }
    }

    @Override
    public String getPattern() {
        return PlatformSpecifics.FORT_RE.pattern();
    }

}
