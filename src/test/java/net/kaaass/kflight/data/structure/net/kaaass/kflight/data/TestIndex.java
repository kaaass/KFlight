package net.kaaass.kflight.data.structure.net.kaaass.kflight.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kaaass.kflight.data.Index;
import net.kaaass.kflight.data.entry.IEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        // TODO
    }

    @Test
    public void testRemove() {
        // TODO
    }
}
