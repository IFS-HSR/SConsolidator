package ch.hsr.ifs.sconsolidator.core.existingbuild;

import ch.hsr.ifs.sconsolidator.core.SConsNature;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;


public class SConsExistingCodeNature extends SConsNature {

    @Override
    protected SConsNatureTypes getNatureTypeId() {
        return SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE;
    }
}
