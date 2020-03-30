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
}
