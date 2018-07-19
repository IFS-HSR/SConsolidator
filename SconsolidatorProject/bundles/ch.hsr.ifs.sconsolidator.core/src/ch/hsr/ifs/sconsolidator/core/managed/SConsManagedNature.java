package ch.hsr.ifs.sconsolidator.core.managed;

import ch.hsr.ifs.sconsolidator.core.SConsNature;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;

public class SConsManagedNature extends SConsNature {

  @Override
  protected SConsNatureTypes getNatureTypeId() {
    return SConsNatureTypes.MANAGED_PROJECT_NATURE;
  }
}
