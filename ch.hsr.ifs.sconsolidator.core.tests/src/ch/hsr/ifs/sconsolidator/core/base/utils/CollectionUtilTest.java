package ch.hsr.ifs.sconsolidator.core.base.utils;

import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.list;
import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.map;
import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.set;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class CollectionUtilTest {
  @Test
  public void testList() {
    List<String> list = list();
    assertNotNull(list);
    assertTrue(list.isEmpty());
    assertTrue(list instanceof ArrayList<?>);
  }

  @Test
  public void testListWithElements() {
    List<String> list = list("scons", "is", "better", "than", "cmake");
    assertTrue(list instanceof ArrayList<?>);
    assertArrayEquals(new String[] {"scons", "is", "better", "than", "cmake"},
        list.toArray(new String[0]));
  }

  @Test
  public void testSet() {
    Set<String> set = set();
    assertNotNull(set);
    assertTrue(set.isEmpty());
    assertTrue(set instanceof HashSet<?>);
  }

  @Test
  public void testSetWithElements() {
    Set<String> set = set("scons", "is", "better", "than", "cmake");
    assertTrue(set instanceof HashSet<?>);
    assertTrue(set.containsAll(Arrays
        .asList(new String[] {"scons", "is", "better", "than", "cmake"})));
  }

  @Test
  public void testMap() {
    Map<String, String> map = map();
    assertNotNull(map);
    assertTrue(map.isEmpty());
    assertTrue(map instanceof HashMap<?, ?>);
  }
}
