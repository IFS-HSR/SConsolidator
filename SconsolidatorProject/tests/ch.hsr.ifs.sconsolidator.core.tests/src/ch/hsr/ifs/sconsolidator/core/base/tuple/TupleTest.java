package ch.hsr.ifs.sconsolidator.core.base.tuple;

import static ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple._1;
import static ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple._2;
import static ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple._3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;


public class TupleTest extends TestCase {

    @Test
    public void testPairCreation() {
        Map<Pair<String, String>, Integer> marriage = new HashMap<Pair<String, String>, Integer>();
        marriage.put(Tuple.from("Ursus", "Nadeschkin"), 12);
        marriage.put(Tuple.from("Bill", "Hillary"), 15);
        marriage.put(Tuple.from("Bill", "Melinda"), 22);

        int numberOfYears = marriage.get(Tuple.from("Bill", "Hillary"));
        assertEquals(15, numberOfYears);
        assertNull(marriage.get(Tuple.from("Charles", "Camilla")));
    }

    @Test
    public void testPairGet() {
        Pair<String, Integer> t = Tuple.from("QuestionOfLive", 42);
        String s = _1(t);
        int i = _2(t);
        assertEquals("QuestionOfLive", s);
        assertEquals(42, i);
    }

    @Test
    public void testPairToString() {
        Pair<String, String> couple = Tuple.from("Bill", "Hillary");
        String s = couple.toString();
        assertEquals("(Bill, Hillary)", s);
    }

    @Test
    public void testSingletonCreation() {
        Set<Singleton<Integer>> integers = new HashSet<Singleton<Integer>>();
        Singleton<Integer> questionOfLive = Tuple.from(42);
        integers.add(questionOfLive);
        assertTrue(integers.remove(Tuple.from(42)));
        assertFalse(integers.contains(Tuple.from(42)));
    }

    @Test
    public void testSingletonGet() {
        Singleton<Integer> t = Tuple.from(42);
        int i = _1(t);
        assertEquals(42, i);
    }

    @Test
    public void testSingletonToString() {
        Singleton<String> one = Tuple.from("One");
        String s = one.toString();
        assertEquals("(One)", s);
    }

    @Test
    public void testTripleCreation() {
        Map<Triple<String, String, String>, Integer> numbers = new HashMap<Triple<String, String, String>, Integer>();
        numbers.put(Tuple.from("One", "Two", "Three"), 2);
        numbers.put(Tuple.from("Four", "Five", "Six"), 5);
        numbers.put(Tuple.from("Seven", "Eight", "Nine"), 8);

        int middle = numbers.get(Tuple.from("Four", "Five", "Six"));
        assertEquals(5, middle);
        assertNull(numbers.get(Tuple.from("Eleven", "Twelve", "Thirteen")));
    }

    @Test
    public void testTripleGet() {
        Triple<String, String, Integer> marriage = Tuple.from("Bill", "Hillary", 15);
        String husband = _1(marriage);
        String spouse = _2(marriage);
        int numberOfYears = _3(marriage);
        assertEquals("Bill", husband);
        assertEquals("Hillary", spouse);
        assertEquals(15, numberOfYears);
    }

    @Test
    public void testTripleToString() {
        Triple<String, String, Integer> marriage = Tuple.from("Bill", "Hillary", 15);
        String s = marriage.toString();
        assertEquals("(Bill, Hillary, 15)", s);
    }
}
