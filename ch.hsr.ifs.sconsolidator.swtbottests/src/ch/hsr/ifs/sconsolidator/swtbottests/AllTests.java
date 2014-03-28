package ch.hsr.ifs.sconsolidator.swtbottests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
// @formatter:off
  NewManagedProjectWizardTest.class, 
  ImportExistingCodeWizardTest. class
// @formatter:on
})
public class AllTests {
}
