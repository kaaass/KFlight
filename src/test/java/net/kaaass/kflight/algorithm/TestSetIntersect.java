package net.kaaass.kflight.algorithm;

import net.kaaass.kflight.data.structure.HashSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 哈希集合交集的单元测试
 */
public class TestSetIntersect {

    @Test
    public void testIntersect1() {
        var setA = new HashSet<String>(){{
            insert("test");
            insert("aaa");
            insert("23333");
        }};
        var setB = new HashSet<String>(){{
            insert("bbb");
            insert("23333");
            insert("test");
            insert("66666");
        }};

        var setAns = new HashSet<String>(){{
            insert("23333");
            insert("test");
        }};

        assertEquals(setAns, SetIntersect.intersect(setA, setB));
    }

    @Test
    public void testIntersect2() {
        var setA = new HashSet<String>(){{
            insert("test");
            insert("java");
            insert("aaa");
            insert("kaaass");
            insert("23333");
        }};
        var setB = new HashSet<String>(){{
            insert("bbb");
            insert("kas");
        }};

        var setAns = new HashSet<String>();

        assertEquals(setAns, SetIntersect.intersect(setA, setB));
    }
}
