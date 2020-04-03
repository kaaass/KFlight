package net.kaaass.kflight.data.sort;

import org.junit.Ignore;
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

    public static Integer[] revInts(int len) {
        var ret = new Integer[len];
        for (int i = 0; i < len; i++)
            ret[i] = len - i - 1;
        return ret;
    }

    public void assertSort(Integer[] data, Integer[] ret) {
        Arrays.sort(data);
        assertArrayEquals(data, ret);
    }

    private static long ts;

    public static void tick(String info) {
        if (info.length() > 0)
            System.out.println(info);
        ts = System.currentTimeMillis();
    }

    public static long tock(String info) {
        long diff = System.currentTimeMillis() - ts;
        if (info.length() > 0) {
            System.out.print(info);
            System.out.printf(" Done in %d ms.\n", diff);
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

    /*
     Benchmark
     */

    /**
     * 测试随机顺序
     */
    @Test()
    public void benchmarkRandom() {
        int BENCHMARK_LEN = 3000000;

        var data = randomInts(BENCHMARK_LEN);
        //
        var ret = data.clone();
        tick("");
        StableQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("Random StableQuickSort:");
        //
        ret = data.clone();
        tick("");
        AdaptiveMergeSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("Random AdaptiveMergeSort:");
        //
        ret = data.clone();
        tick("");
        StableHybridSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("Random StableHybridSort:");
        //
        ret = data.clone();
        tick("");
        Arrays.sort(ret);
        tock("Random Java Arrays.sort:");
        //
        System.out.println("");
    }

    /**
     * 测试逆顺序
     */
    @Test()
    public void benchmarkReverse() {
        int BENCHMARK_LEN = 50000;
        var data = revInts(BENCHMARK_LEN);
        //
        var ret = data.clone();
        tick("");
        StableQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("Reverse StableQuickSort:");
        //
        ret = data.clone();
        tick("");
        AdaptiveMergeSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("Reverse AdaptiveMergeSort:");
        //
        ret = data.clone();
        tick("");
        StableHybridSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("Reverse StableHybridSort:");
        //
        ret = data.clone();
        tick("");
        Arrays.sort(ret);
        tock("Reverse Java Arrays.sort:");
        //
        System.out.println("");
    }
}
