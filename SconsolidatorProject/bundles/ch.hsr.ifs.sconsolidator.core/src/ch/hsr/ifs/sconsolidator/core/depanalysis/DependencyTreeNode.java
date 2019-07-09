package ch.hsr.ifs.sconsolidator.core.depanalysis;

import java.util.ArrayList;
import java.util.List;


public class DependencyTreeNode {

    private final String                   value;
    private final List<DependencyTreeNode> children;
    private DependencyTreeNode             parent;

    public DependencyTreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<DependencyTreeNode>();
    }

    public String getValue() {
        return value;
    }

    public DependencyTreeNode getParent() {
        return parent;
    }

    public List<DependencyTreeNode> getChildren() {
        return children;
    }

    public void setParent(DependencyTreeNode parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return value;
    }
}
