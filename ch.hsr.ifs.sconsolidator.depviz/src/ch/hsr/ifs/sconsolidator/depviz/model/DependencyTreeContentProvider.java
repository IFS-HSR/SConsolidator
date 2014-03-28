package ch.hsr.ifs.sconsolidator.depviz.model;

import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.list;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import ch.hsr.ifs.sconsolidator.core.depanalysis.DependencyTreeNode;

public class DependencyTreeContentProvider implements IGraphEntityContentProvider {

  @Override
  public Object[] getConnectedTo(Object node) {
    return getDependencies(node);
  }

  private Object[] getDependencies(Object node) {
    if (node == null)
      return new DependencyTreeNode[0];

    DependencyTreeNode n = (DependencyTreeNode) node;
    return n.getChildren().toArray(new DependencyTreeNode[n.getChildren().size()]);
  }

  @Override
  public Object[] getElements(Object node) {
    if (node == null)
      return new DependencyTreeNode[0];

    DependencyTreeNode n = (DependencyTreeNode) node;
    List<DependencyTreeNode> allNodes = list();
    collectTreeNodes(n, allNodes);
    return allNodes.toArray(new DependencyTreeNode[allNodes.size()]);
  }

  @Override
  public void dispose() {}

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

  private void collectTreeNodes(DependencyTreeNode node, List<DependencyTreeNode> nodes) {
    nodes.add(node);

    if (node.getChildren().isEmpty())
      return;

    for (DependencyTreeNode n : node.getChildren()) {
      collectTreeNodes(n, nodes);
    }
  }
}
