package net.kaaass.kflight.data.structure;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

/**
 * 红黑树的单元测试
 */
public class TestRBTree {

    @Test
    public void testInsert() {
        var tree = new RBTree<Integer, String>(Comparator.naturalOrder());
        tree.insert(4, "233");
        tree.insert(1, "2333");
        tree.insert(3, "23333");
        tree.insert(0, "233333");
        assertEquals(4, tree.size());
        assertEquals(Integer.valueOf(0), tree.getMinimumNode().key);
        assertEquals(Integer.valueOf(4), tree.getMaximumNode().key);
        //
        tree = new RBTree<Integer, String>(Comparator.naturalOrder());
        tree.insert(9, "233");
        tree.insert(8, "2333");
        tree.insert(7, "23333");
        tree.insert(6, "233333");
        tree.insert(5, "233333");
        tree.insert(4, "233333");
        tree.insert(3, "233333");
        tree.insert(2, "233333");
        assertEquals(8, tree.size());
        assertEquals(Integer.valueOf(2), tree.getMinimumNode().key);
        assertEquals(Integer.valueOf(9), tree.getMaximumNode().key);
    }

    @Test
    public void testNextAndPrev() {
        var tree = new RBTree<Integer, String>(Comparator.naturalOrder());
        tree.insert(4, "233");
        tree.insert(1, "2333");
        tree.insert(3, "23333");
        tree.insert(0, "233333");
        var p = tree.getMinimumNode();
        assertEquals(Integer.valueOf(0), p.key);
        p = tree.nextOf(p);
        assertEquals(Integer.valueOf(1), p.key);
        p = tree.nextOf(p);
        assertEquals(Integer.valueOf(3), p.key);
        p = tree.nextOf(p);
        assertEquals(Integer.valueOf(4), p.key);
        p = tree.nextOf(p);
        assertNull(p);
        //
        tree = new RBTree<Integer, String>(Comparator.naturalOrder());
        tree.insert(9, "233");
        tree.insert(8, "2333");
        tree.insert(7, "23333");
        tree.insert(6, "233333");
        tree.insert(5, "233333");
        tree.insert(4, "233333");
        tree.insert(3, "233333");
        tree.insert(2, "233333");
        p = tree.getMaximumNode();
        for (int i = 9; i >= 2; i--) {
            assertEquals(Integer.valueOf(i), p.key);
            p = tree.prevOf(p);
        }
        assertNull(p);
    }

    @Test
    public void testDelete() {
        var tree = new RBTree<Integer, String>(Comparator.naturalOrder());
        tree.insert(4, "233");
        tree.insert(1, "2333");
        tree.insert(3, "23333");
        tree.insert(0, "233333");

        var node3 = tree.prevOf(tree.getMaximumNode());
        tree.delete(node3);

        assertEquals(3, tree.size());
        var p = tree.getMinimumNode();
        assertEquals(Integer.valueOf(0), p.key);
        p = tree.nextOf(p);
        assertEquals(Integer.valueOf(1), p.key);
        p = tree.nextOf(p);
        assertEquals(Integer.valueOf(4), p.key);
        p = tree.nextOf(p);
        assertNull(p);
        //
        tree = new RBTree<Integer, String>(Comparator.naturalOrder());
        tree.insert(9, "233");
        tree.insert(8, "2333");
        tree.insert(7, "23333");
        tree.insert(6, "233333");
        tree.insert(5, "233333");
        tree.insert(4, "233333");
        tree.insert(3, "233333");
        tree.insert(2, "233333");

        for (int i = 0; i < 4; i++) {
            tree.delete(tree.getMinimumNode());
        }

        assertEquals(4, tree.size());
        p = tree.getMaximumNode();
        for (int i = 9; i >= 6; i--) {
            assertEquals(Integer.valueOf(i), p.key);
            p = tree.prevOf(p);
        }
        assertNull(p);
    }

    @Test
    public void testFind() {
        var tree = new RBTree<Integer, String>(Comparator.naturalOrder());
        tree.insert(1, "1");
        tree.insert(2, "2");
        tree.insert(5, "3");
        tree.insert(5, "4");
        tree.insert(5, "5");
        tree.insert(6, "6");
        tree.insert(8, "7");
        tree.insert(3, "8");
        tree.insert(8, "9");
        tree.insert(9, "10");
        tree.insert(8, "11");
        //
        System.out.println("Order in tree: ");
        for (var node = tree.getMinimumNode();
             node != null;
             node = tree.nextOf(node)) {
            System.out.printf("Node(key = %d, value = %s)\n", node.key, node.value);
        }
        //
        assertEquals("2", tree.findOne(2).value);
        assertEquals("8", tree.findOne(3).value);
        //
        var ret = tree.findPrev(5);
        assertEquals(Integer.valueOf(3), ret.key);
        assertEquals("8", ret.value);

        ret = tree.findPrev(8);
        assertEquals(Integer.valueOf(6), ret.key);
        assertEquals("6", ret.value);

        assertNull(tree.findPrev(1));
        //
        ret = tree.findNext(5);
        assertEquals(Integer.valueOf(6), ret.key);
        assertEquals("6", ret.value);

        ret = tree.findNext(8);
        assertEquals(Integer.valueOf(9), ret.key);
        assertEquals("10", ret.value);

        assertNull(tree.findNext(9));
        //
        assertEquals("1", tree.findLowerBound(1).value);
        assertEquals("2", tree.findLowerBound(2).value);
        assertEquals("8", tree.findLowerBound(3).value);
        assertEquals("3", tree.findLowerBound(5).value);
        assertEquals("6", tree.findLowerBound(6).value);
        assertEquals("7", tree.findLowerBound(8).value);
        assertEquals("10", tree.findLowerBound(9).value);
        //
        assertEquals("1", tree.findUpperBound(1).value);
        assertEquals("2", tree.findUpperBound(2).value);
        assertEquals("8", tree.findUpperBound(3).value);
        assertEquals("5", tree.findUpperBound(5).value);
        assertEquals("6", tree.findUpperBound(6).value);
        assertEquals("11", tree.findUpperBound(8).value);
        assertEquals("10", tree.findUpperBound(9).value);
    }

    @Test
    public void testCompareNode() {
        var tree = new RBTree<Integer, String>(Comparator.naturalOrder());
        tree.insert(1, "1");
        tree.insert(2, "2");
        tree.insert(5, "3");
        tree.insert(5, "4");
        tree.insert(5, "5");
        tree.insert(6, "6");
        tree.insert(8, "7");
        tree.insert(3, "8");
        tree.insert(8, "9");
        tree.insert(9, "10");
        tree.insert(8, "11");
        //
        assertEquals(0, tree.compareNode(
                tree.findOne(1),
                tree.findOne(1)
        ));
        assertTrue(tree.compareNode(
                tree.findOne(1),
                tree.findOne(2)
        ) < 0);
        assertTrue(tree.compareNode(
                tree.findOne(9),
                tree.findOne(6)
        ) > 0);
        assertTrue(tree.compareNode(
                tree.findLowerBound(5),
                tree.findUpperBound(5)
        ) < 0);
        assertTrue(tree.compareNode(
                tree.findUpperBound(8),
                tree.findLowerBound(8)
        ) > 0);
    }

    @Test
    public void testHas() {
        var tree = new RBTree<Integer, String>(Comparator.naturalOrder());
        tree.insert(1, "1");
        tree.insert(2, "2");
        tree.insert(5, "3");
        tree.insert(5, "4");
        tree.insert(5, "5");
        tree.insert(6, "6");
        tree.insert(8, "7");
        tree.insert(3, "8");
        tree.insert(8, "9");
        tree.insert(9, "10");
        tree.insert(8, "11");
        //
        assertTrue(tree.has(1));
        assertTrue(tree.has(2));
        assertTrue(tree.has(3));
        assertFalse(tree.has(4));
        assertTrue(tree.has(5));
        //
        assertTrue(tree.has(1, "1"));
        assertTrue(tree.has(5, "5"));
        assertFalse(tree.has(5, "7"));
    }
}
