package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;


class SConsBuildPropertyValue implements IBuildPropertyValue {

    private final String id;
    private final String name;

    public SConsBuildPropertyValue(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
