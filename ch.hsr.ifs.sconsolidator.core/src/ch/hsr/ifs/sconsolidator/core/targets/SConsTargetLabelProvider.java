package ch.hsr.ifs.sconsolidator.core.targets;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;

class SConsTargetLabelProvider extends LabelProvider implements ITableLabelProvider {
  private final Image targetImg = SConsImages.getImageDescriptor(SConsImages.SCONS_TARGET)
      .createImage();
  private final WorkbenchLabelProvider labelProvider = new WorkbenchLabelProvider();

  @Override
  public Image getImage(Object obj) {
    if (obj instanceof SConsBuildTarget)
      return targetImg;
    else if (obj instanceof IProject)
      return labelProvider.getImage(obj);

    return null;
  }

  @Override
  public String getText(Object obj) {
    if (obj instanceof SConsBuildTarget)
      return ((SConsBuildTarget) obj).toString();
    else if (obj instanceof IProject)
      return labelProvider.getText(obj);

    return null;
  }

  @Override
  public void dispose() {
    super.dispose();
    labelProvider.dispose();
    targetImg.dispose();
  }

  @Override
  public Image getColumnImage(Object obj, int columnIndex) {
    return columnIndex == 0 ? getImage(obj) : null;
  }

  @Override
  public String getColumnText(Object obj, int columnIndex) {
    if (columnIndex != 0)
      throw new IllegalArgumentException("Unknown column index");

    return getText(obj);
  }
}
