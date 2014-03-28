package ch.hsr.ifs.sconsolidator.core.targets;

import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import ch.hsr.ifs.sconsolidator.core.SConsImages;

class DialogInformationLine extends CLabel {
  private final Color normalMessageBackground;
  private boolean errorMessage;

  public DialogInformationLine(Composite parent) {
    super(parent, SWT.LEFT);
    normalMessageBackground = getBackground();
  }

  public void setErrorMessage(String message) {
    if (isValidMessage(message)) {
      errorMessage = true;
      setText(message);
      setImage(SConsImages.getImageDescriptor(SConsImages.ERROR).createImage());
      setBackground(JFaceColors.getErrorBackground(getDisplay()));
    } else {
      errorMessage = false;
      setText("");
      setImage(null);
      setBackground(normalMessageBackground);
    }
  }

  private boolean isValidMessage(String message) {
    return message != null && message.length() > 0;
  }

  public boolean hasErrorMessage() {
    return errorMessage;
  }
}
