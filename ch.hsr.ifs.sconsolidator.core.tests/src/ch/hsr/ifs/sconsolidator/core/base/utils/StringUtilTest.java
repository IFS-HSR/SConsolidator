package ch.hsr.ifs.sconsolidator.core.base.utils;

import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.list;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class StringUtilTest {
  @Test
  public void testJoin() {
    List<String> famousPainters = list();
    assertEquals("", StringUtil.join(famousPainters, ", "));

    famousPainters =
        list("Andy Warhol", "Claude Monet", "Pablo Picasso", "Paul Gaguin", "Wassily Kandinsky");
    String result = StringUtil.join(famousPainters, ", ");
    assertEquals("Andy Warhol, Claude Monet, Pablo Picasso, Paul Gaguin, Wassily Kandinsky", result);
  }

  @Test
  public void testSplitWithoutSpaces() {
    assertEquals(list(), StringUtil.split(""));
    assertEquals(list("scons"), StringUtil.split("scons", ""));
  }

  @Test
  public void testSplitWithSpaces() {
    String initialElement = "scons";
    String tailToSplit = "  -u   --implicit-deps-changed 	--tree=all ";
    Collection<String> splitted = StringUtil.split(initialElement, tailToSplit);
    List<String> expected = list("scons", "-u", "--implicit-deps-changed", "--tree=all");
    assertEquals(expected, splitted);
  }

  @Test
  public void testSplitWithQuotesAndSpaces() {
    String initialElement = "scons";
    String tailToSplit =
        "  -u  --runparams=\"-- RECompilerTest\" --tree=all --runparams='-- bonk' ";
    Collection<String> splitted = StringUtil.split(initialElement, tailToSplit);
    List<String> expected =
        list("scons", "-u", "--runparams=-- RECompilerTest", "--tree=all", "--runparams=-- bonk");
    assertEquals(expected, splitted);
  }
}
