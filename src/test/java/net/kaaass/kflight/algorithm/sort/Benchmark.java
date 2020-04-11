package net.kaaass.kflight.algorithm.sort;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * 排序效率测试
 */
@Ignore
public class Benchmark {

    public static Integer[] randomInts(int len) {
        var ret = new Integer[len];
        for (int i = 0; i < len; i++)
            ret[i] = i;
        var rand = new Random();
        for (int i = 0; i < len * 2; i++) {
            int a = rand.nextInt(len);
            int b = rand.nextInt(len);
            Integer t;
            t = ret[a];
            ret[a] = ret[b];
            ret[b] = t;
        }
        return ret;
    }

    public static Integer[] nearlySortedInts(int len, int step, int radius) {
        var ret = new Integer[len];
        for (int i = 0; i < len; i++)
            ret[i] = i;
        var rand = new Random();
        for (int i = 0; i < len; i += step) {
            int offset = rand.nextInt(2 * radius) - radius;
            int b = i + offset;
            if (b < 0) b = 0;
            if (b >= len) b = len - 1;
            Integer t;
            t = ret[i];
            ret[i] = ret[b];
            ret[b] = t;
        }
        return ret;
    }

    public static Integer[] mostSameInts(int len, int same) {
        var ret = new Integer[len];
        for (int i = 0; i < len; i++)
            ret[i] = i / same;
        var rand = new Random();
        for (int i = 0; i < len * 2; i++) {
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

    /**
     * 测试随机顺序
     */
    @Test
    public void benchmarkRandom() {
        int BENCHMARK_LEN = 200000;
        int ROUND = 30;
        var NAMES = new String[]{"StableQuickSort", "StableTriQuickSort", "AdaptiveMergeSort", "StableHybridSort",
                "StableHybridSort.normal", "Java Arrays.sort"};

        var result = new long[ROUND];

        for (int i = 0; i < ROUND; i++) {
            var data = randomInts(BENCHMARK_LEN);
            //
            var ret = data.clone();
            tick("");
            StableQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[0] += tock("");
            //
            ret = data.clone();
            tick("");
            StableTriQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[1] += tock("");
            //
            ret = data.clone();
            tick("");
            AdaptiveMergeSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[2] += tock("");
            //
            ret = data.clone();
            tick("");
            StableHybridSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[3] += tock("");
            //
            ret = data.clone();
            tick("");
            StableHybridSort.normalSort(ret, 0, ret.length, Integer::compareTo);
            result[4] += tock("");
            //
            ret = data.clone();
            tick("");
            Arrays.sort(ret);
            result[5] += tock("");
        }
        // 打印结果
        System.out.println("====== Random ======");
        for (int i = 0; i < NAMES.length; i++) {
            System.out.printf("%s avg: %f ms\n", NAMES[i], result[i] * 1e-6d / ROUND);
        }
        System.out.println();
    }

    /**
     * 测试几乎有序顺序
     */
    @Test
    public void benchmarkNearlySorted() {
        int BENCHMARK_LEN = 20000;
        int BENCHMARK_STEP = 2;
        int BENCHMARK_RADIUS = 10;
        int ROUND = 30;
        var NAMES = new String[]{"StableQuickSort", "StableTriQuickSort", "AdaptiveMergeSort", "StableHybridSort",
                "StableHybridSort.normal", "Java Arrays.sort", "BiInsertSort"};

        var result = new long[NAMES.length];

        for (int i = 0; i < ROUND; i++) {
            var data = nearlySortedInts(BENCHMARK_LEN, BENCHMARK_STEP, BENCHMARK_RADIUS);
            //
            var ret = data.clone();
            tick("");
            StableQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[0] += tock("");
            //
            ret = data.clone();
            tick("");
            StableTriQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[1] += tock("");
            //
            ret = data.clone();
            tick("");
            AdaptiveMergeSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[2] += tock("");
            //
            ret = data.clone();
            tick("");
            StableHybridSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[3] += tock("");
            //
            ret = data.clone();
            tick("");
            StableHybridSort.normalSort(ret, 0, ret.length, Integer::compareTo);
            result[4] += tock("");
            //
            ret = data.clone();
            tick("");
            Arrays.sort(ret);
            result[5] += tock("");
            //
            ret = data.clone();
            tick("");
            BiInsertSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[6] += tock("");
        }
        // 打印结果
        System.out.println("====== NearlySorted ======");
        for (int i = 0; i < NAMES.length; i++) {
            System.out.printf("%s avg: %f ms\n", NAMES[i], result[i] * 1e-6d / ROUND);
        }
        System.out.println();
    }

    /**
     * 测试多数重复顺序
     */
    @Test
    public void benchmarkMostSame() {
        int BENCHMARK_LEN = 100000;
        int BENCHMARK_SAME = 1000;
        int ROUND = 30;
        var NAMES = new String[]{"StableQuickSort", "StableTriQuickSort", "AdaptiveMergeSort", "StableHybridSort",
                "StableHybridSort.normal", "Java Arrays.sort"};

        var result = new long[ROUND];

        for (int i = 0; i < ROUND; i++) {
            var data = mostSameInts(BENCHMARK_LEN, BENCHMARK_SAME);
            //
            var ret = data.clone();
            tick("");
            StableQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[0] += tock("");
            //
            ret = data.clone();
            tick("");
            StableTriQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[1] += tock("");
            //
            ret = data.clone();
            tick("");
            AdaptiveMergeSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[2] += tock("");
            //
            ret = data.clone();
            tick("");
            StableHybridSort.sort(ret, 0, ret.length, Integer::compareTo);
            result[3] += tock("");
            //
            ret = data.clone();
            tick("");
            StableHybridSort.normalSort(ret, 0, ret.length, Integer::compareTo);
            result[4] += tock("");
            //
            ret = data.clone();
            tick("");
            Arrays.sort(ret);
            result[5] += tock("");
        }
        // 打印结果
        System.out.println("====== MostSame ======");
        for (int i = 0; i < NAMES.length; i++) {
            System.out.printf("%s avg: %f ms\n", NAMES[i], result[i] * 1e-6d / ROUND);
        }
        System.out.println();
    }

    /**
     * 测试逆顺序
     */
    @Test
    public void benchmarkReverse() {
        int BENCHMARK_LEN = 30000;
        var data = revInts(BENCHMARK_LEN);
        System.out.println("====== Reverse ======");
        //
        var ret = data.clone();
        tick("");
        StableQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("StableQuickSort:");
        //
        ret = data.clone();
        tick("");
        StableTriQuickSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("StableTriQuickSort:");
        //
        ret = data.clone();
        tick("");
        AdaptiveMergeSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("AdaptiveMergeSort:");
        //
        ret = data.clone();
        tick("");
        StableHybridSort.sort(ret, 0, ret.length, Integer::compareTo);
        tock("StableHybridSort:");
        //
        ret = data.clone();
        tick("");
        StableHybridSort.normalSort(ret, 0, ret.length, Integer::compareTo);
        tock("StableHybridSort.normal:");
        //
        ret = data.clone();
        tick("");
        Arrays.sort(ret);
        tock("Java Arrays.sort:");
        //
        System.out.println("");
    }
}
