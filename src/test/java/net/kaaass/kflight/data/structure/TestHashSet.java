package net.kaaass.kflight.data.structure;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * 哈希集合的单元测试
 */
public class TestHashSet {

    @Test
    public void testInsertAndSize() {
        var set = new HashSet<String>();

        assertEquals(0, set.size());
        assertTrue(set.insert("233"));
        assertEquals(1, set.size());

        assertTrue(set.insert("666"));
        assertFalse(set.insert("233"));
        assertEquals(2, set.size());

        assertFalse(set.insert("666"));
        assertFalse(set.insert("233"));
        assertFalse(set.insert("666"));
    }

    @Test
    public void testInSet() {
        var set = new HashSet<String>();

        set.insert("233");
        set.insert("666");
        set.insert("123456");
        set.insert("www");
        assertEquals(4, set.size());

        assertTrue(set.inSet("233"));
        assertTrue(set.inSet("666"));
        assertTrue(set.inSet("123456"));
        assertTrue(set.inSet("www"));

        assertFalse(set.inSet("123456789"));
        assertFalse(set.inSet("FFF"));
        assertFalse(set.inSet("w"));
    }

    @Test
    public void testToList() {
        var set = new HashSet<String>();

        set.insert("233");
        set.insert("666");
        set.insert("123456");
        set.insert("www");

        var list = new ArrayList<String>();
        list.add("233");
        list.add("666");
        list.add("123456");
        list.add("www");
        list.sort(Comparator.comparingInt(String::hashCode));

        assertEquals(list, set.toList());
    }
}
