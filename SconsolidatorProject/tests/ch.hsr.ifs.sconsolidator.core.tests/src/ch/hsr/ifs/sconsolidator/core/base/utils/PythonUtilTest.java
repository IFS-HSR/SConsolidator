package ch.hsr.ifs.sconsolidator.core.base.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.base.utils.PythonUtil;


public class PythonUtilTest {

    @Test
    public void testToPythonStringLiteral() {
        assertEquals("None", PythonUtil.toPythonStringLiteral("None"));
        assertEquals("'Yes'", PythonUtil.toPythonStringLiteral("Yes"));
        assertEquals("'Tour d\\'horizon'", PythonUtil.toPythonStringLiteral("Tour d'horizon"));
    }

    @Test
    public void testToPythonList() {
        assertEquals("[]", PythonUtil.toPythonList(Collections.emptyList()));
        assertEquals("['eins']", PythonUtil.toPythonList(Collections.singletonList("eins")));
        assertEquals("['eins', 'zwei', 'drei']", PythonUtil.toPythonList(Arrays.asList(new String[] { "eins", "zwei", "drei" })));
    }

    @Test
    public void testToPythonDict() {
        assertEquals("{}", PythonUtil.toPythonDict((Collections.emptyMap())));
        assertEquals("{'color':'blue'}", PythonUtil.toPythonDict(Collections.singletonMap("color", "blue")));
        Map<String, String> properties = new TreeMap<String, String>();
        properties.put("color", "red");
        properties.put("name", "schiri");
        properties.put("price", "1 million");
        assertEquals("{'color':'red', 'name':'schiri', 'price':'1 million'}", PythonUtil.toPythonDict(properties));
    }

    @Test
    public void testToPythonBoolean() {
        assertEquals("True", PythonUtil.toPythonBoolean(true));
        assertEquals("False", PythonUtil.toPythonBoolean(false));
    }
}
