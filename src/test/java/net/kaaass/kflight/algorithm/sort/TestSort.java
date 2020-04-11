package net.kaaass.kflight.algorithm.sort;

import net.kaaass.kflight.algorithm.Sorter;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class TestSort {

    public static Integer[] randomInts(int len) {
        var ret = new Integer[len];
        for (int i = 0; i < len; i++)
            ret[i] = i;
        var rand = new Random();
        for (int i = 0; i < len; i++) {
            int a = rand.nextInt(len);
            int b = rand.nextInt(len);
            Integer t;
            t = ret[a];
            ret[a] = ret[b];
            ret[b] = t;
        }
        return ret;
    }

    public void assertSort(Object[] data, Object[] ret) {
        Arrays.sort(data);
        assertArrayEquals(data, ret);
    }

    private static long ts;

    public static void tick(String info) {
        if (info.length() > 0)
            System.out.println(info);
        ts = System.nanoTime();
    }

    public static long tock(String info) {
        long diff = System.nanoTime() - ts;
        if (info.length() > 0) {
            System.out.print(info);
            System.out.printf(" Done in %f ms.\n", diff * 1e-6d);
        }
        return diff;
    }

    /*
      Start Test
     */

    private static int TEST_LEN = 10000;

    @Test
    public void testBiInsertSort() {
        var data = randomInts(TEST_LEN);
        var ret = data.clone();
        tick("Start test BiInsertSort.");
        BiInsertSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("End test BiInsertSort.");
        assertSort(data, ret);
    }

    @Test
    public void testStableQuickSort() {
        var data = randomInts(TEST_LEN);
        var ret = data.clone();
        tick("Start test StableQuickSort.");
        StableQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("End test StableQuickSort.");
        assertSort(data, ret);
    }

    @Test
    public void testStableTriQuickSort() {
        var data = randomInts(TEST_LEN);
        var ret = data.clone();
        tick("Start test StableTriQuickSort.");
        StableTriQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("End test StableTriQuickSort.");
        assertSort(data, ret);
    }

    @Test
    public void testAdaptiveMergeSort() {
        var data = randomInts(TEST_LEN);
        var ret = data.clone();
        tick("Start test AdaptiveMergeSort.");
        AdaptiveMergeSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("End test AdaptiveMergeSort.");
        assertSort(data, ret);
    }

    @Test
    public void testStableHybridSort() {
        var data = randomInts(TEST_LEN);
        var ret = data.clone();
        tick("Start test StableHybridSort.");
        StableHybridSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("End test StableHybridSort.");
        assertSort(data, ret);
    }

    @Test
    public void testSorter() {
        var data = randomInts(TEST_LEN);
        var ret = data.clone();
        var list = Arrays.asList(ret);
        tick("Start test Sorter.");
        Sorter.sort(list, Integer::compareTo);
        tock("End test Sorter.");
        assertSort(data, list.toArray());
    }
}
