package ch.hsr.ifs.sconsolidator.swtbottests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ //
                NewManagedProjectWizardTest.class, //
                ImportExistingCodeWizardTest.class //
})
public class SWTBotTestSuiteAll {}
