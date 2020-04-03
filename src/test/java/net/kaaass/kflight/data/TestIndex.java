package net.kaaass.kflight.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kaaass.kflight.data.entry.IEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * 索引的单元测试
 */
public class TestIndex {

    @Data
    @AllArgsConstructor
    static final class TestEntry implements IEntry {
        int intAttr;
        String strAttr;
    }

    Index<TestEntry, Integer, Integer> intIndex;
    Index<TestEntry, String, Integer> strIndex;

    List<TestEntry> data = new ArrayList<>() {{
        add(new TestEntry(1, "1"));
        add(new TestEntry(3, "2"));
        add(new TestEntry(2, "3"));
        add(new TestEntry(3, "4"));
        add(new TestEntry(4, "5"));
        add(new TestEntry(4, "6"));
        add(new TestEntry(4, "7"));
        add(new TestEntry(7, "8"));
    }};

    @Before
    public void prepareIndex() {
        // Int
        intIndex = new Index<>(TestEntry::getIntAttr, Function.identity(), Comparator.naturalOrder());
        data.forEach(intIndex::addIndexFor);
        // String
        strIndex = new Index<>(TestEntry::getStrAttr, String::hashCode, Comparator.naturalOrder());
        data.forEach(strIndex::addIndexFor);
    }

    @Test
    public void testAddAndHas() {
        var index = new Index<>(TestEntry::getIntAttr, Function.identity(), Comparator.naturalOrder());
        index.addIndexFor(new TestEntry(1, "a"));
        index.addIndexFor(new TestEntry(1, "b"));
        index.addIndexFor(new TestEntry(2, "c"));
        assertTrue(index.has(1));
        assertTrue(index.has(2));
        assertFalse(index.has(3));
        assertTrue(index.has(new TestEntry(1, "a")));
        assertTrue(index.has(new TestEntry(2, "c")));
        assertFalse(index.has(new TestEntry(1, "c")));
    }

    @Test
    public void testFind() {
        // findOne
        assertEquals(data.get(0), intIndex.findOneRaw(1));
        assertEquals(data.get(2), intIndex.findOneRaw(2));
        assertEquals(data.get(2), strIndex.findOneRaw("3"));
        assertEquals(data.get(6), strIndex.findOneRaw("7"));

        // findAll
        var ret = intIndex.findAll(7);
        assertEquals(1, ret.size());
        assertTrue(ret.contains(data.get(7)));

        ret = intIndex.findAll(3);
        assertEquals(2, ret.size());
        assertTrue(ret.contains(data.get(1)));
        assertTrue(ret.contains(data.get(3)));

        // findBetween
        ret = intIndex.findBetween(2, 4);
        assertEquals(6, ret.size());
        for (int i = 1; i <= 6; i++) {
            assertTrue(ret.contains(data.get(i)));
        }

        ret = intIndex.findBetween(6, 233);
        assertEquals(1, ret.size());
        assertTrue(ret.contains(data.get(7)));

        ret = intIndex.findBetween(5, 6);
        assertEquals(0, ret.size());

        ret = intIndex.findBetween(2, 2);
        assertEquals(1, ret.size());
        assertTrue(ret.contains(data.get(2)));
    }

    @Test
    public void testRemove() {
        var index = new Index<>(TestEntry::getIntAttr, Function.identity(), Comparator.naturalOrder());
        index.addIndexFor(new TestEntry(1, "a"));
        index.addIndexFor(new TestEntry(1, "b"));
        index.addIndexFor(new TestEntry(2, "c"));

        var ans = index.removeIndexFor(new TestEntry(1, "b"));
        assertTrue(ans);
        assertEquals(2, index.size());

        assertTrue(index.has(1));
        assertTrue(index.has(2));

        assertTrue(index.has(new TestEntry(1, "a")));
        assertTrue(index.has(new TestEntry(2, "c")));
        assertFalse(index.has(new TestEntry(1, "b")));
        assertFalse(index.has(new TestEntry(1, "c")));
        //
        ans = index.removeIndexFor(new TestEntry(1, "c"));
        assertFalse(ans);
        assertEquals(2, index.size());
        //
        ans = index.removeIndexFor(new TestEntry(2, "c"));
        assertTrue(ans);
        ans = index.removeIndexFor(new TestEntry(1, "a"));
        assertTrue(ans);
        assertEquals(0, index.size());
    }
}
