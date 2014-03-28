package ch.hsr.ifs.sconsolidator.core.noncpp;

import ch.hsr.ifs.sconsolidator.core.SConsNature;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;

public class SConsNonCppNature extends SConsNature {

  @Override
  protected SConsNatureTypes getNatureTypeId() {
    return SConsNatureTypes.NON_CPP_PROJECT_NATURE;
  }
}
