package net.kaaass.kflight.algorithm.sort;

import java.util.Comparator;
import java.util.Stack;

/**
 * 以 IntroSort 为范本的一种稳定混合排序算法
 * <p>
 * 使用了：成对排序、稳定三者取中快速排序、自适应归并排序
 * 由于处理数据存在较多重复数据，因此连续上升段数量有限，自适应归并排序效
 * 果不会太好，因此以快速排序为基础范本，并对快速排序的部分低效情况进行优化
 */
public class StableHybridSort {

    private static int INSERT_THRESHOLD = 16;

    /**
     * 递归式单层遍历的排序操作
     *
     * @deprecated
     */
    @Deprecated
    private static <S> void sortOnceRecursive(S[] arr, int left, int right, Object[] buf, int depthLimit, Comparator<S> cmp) {
        if (right - left > INSERT_THRESHOLD) { // 过小部分使用插入排序
            if (depthLimit <= 0) {
                // 快排退化时使用自适应归并排序
                AdaptiveMergeSort.sort(arr, left, right, buf, cmp);
                return;
            }
            // 正常进行快排
            int pivot = StableQuickSort.partition(arr, left, right, buf, cmp);
            sortOnceRecursive(arr, left, pivot, buf, depthLimit - 1, cmp);
            sortOnceRecursive(arr, pivot, right, buf, depthLimit - 1, cmp);
        }
    }

    /**
     * 去递归单层遍历的排序操作
     * <p>
     * 和递归操作 {@see #sortOnceRecursive} 速度几乎一致，究其因
     * 可能是 JVM 本身基于堆栈，递归时数据分配代价接近，并且编译时
     * 递归版本还可能会被自动展开进行优化
     *
     * @deprecated
     */
    @Deprecated
    private static <S> void sortOnceDeRecursive(S[] arr, int left, int right, Object[] buf, int depthLimit, Comparator<S> cmp) {
        if (right - left <= INSERT_THRESHOLD) return;

        var stack = new Stack<Integer>();
        stack.ensureCapacity(depthLimit);
        // 初始参数
        stack.push(depthLimit);
        stack.push(right);
        stack.push(left);
        // 循环
        while (!stack.isEmpty()) {
            int lf = stack.pop();
            int rt = stack.pop();
            int depth = stack.pop();
            // 进行逻辑
            if (rt - lf > INSERT_THRESHOLD) {
                if (depth <= 0) {
                    // 快排退化时使用自适应归并排序
                    AdaptiveMergeSort.sort(arr, lf, rt, buf, cmp);
                    continue;
                }
                // 正常进行快排
                int pivot = StableQuickSort.partition(arr, lf, rt, buf, cmp);
                depth--;
                // 左侧
                stack.push(depth);
                stack.push(pivot);
                stack.push(lf);
                // 右侧
                stack.push(depth);
                stack.push(rt);
                stack.push(pivot);
            }
        }
    }

    /**
     * 尾递归优化单层遍历的排序操作
     * <p>
     * 尾递归优化是效果最为明显的优化。因为尾递归优化真正减少了递归时
     * 复制的数据量。
     */
    private static <S> void sortOnce(S[] arr, int left, int right, Object[] buf, int depthLimit, Comparator<S> cmp) {
        while (right - left > INSERT_THRESHOLD) { // 过小部分使用插入排序
            if (depthLimit <= 0) {
                // 快排退化时使用自适应归并排序
                AdaptiveMergeSort.sort(arr, left, right, buf, cmp);
                return;
            }
            // 正常进行快排
            int pivot = StableQuickSort.partition(arr, left, right, buf, cmp);
            depthLimit--;
            sortOnce(arr, pivot, right, buf, depthLimit, cmp);
            right = pivot;
        }
    }

    /**
     * 尾递归优化单层遍历的排序操作，采用稳定三者取中快速排序
     * <p>
     * 优化思路参考 {@see StableTriQuickSort}
     */
    private static <S> void sortOnceTri(S[] arr, int left, int right, Object[] buf, int depthLimit, Comparator<S> cmpr) {
        int lfPivot, rtPivot;
        while (right - left > INSERT_THRESHOLD) { // 过小部分使用插入排序
            if (depthLimit <= 0) {
                // 快排退化时使用自适应归并排序
                AdaptiveMergeSort.sort(arr, left, right, buf, cmpr);
                return;
            }
            // 正常进行快排
            {
                // 处理分段
                lfPivot = left;
                rtPivot = right;
                if (right - left >= 2) {
                    // 选择 key
                    int mid = (left >> 1) + (right >> 1);
                    S aLf = arr[left], key = arr[mid], aRt = arr[right - 1];
                    boolean cmp1 = cmpr.compare(aLf, key) < 0,
                            cmp2 = cmpr.compare(key, aRt) < 0;
                    if (cmp1 ^ cmp2) {
                        boolean cmp3 = cmpr.compare(aLf, aRt) < 0;
                        if (cmp2 ^ cmp3)
                            key = aRt;
                        else
                            key = aLf;
                    }
                    // 排序
                    int lfCur = left, midCur, rtCur, cmp;
                    midCur = rtCur = right - 1;
                    for (var i = left; i < right; i++) {
                        cmp = cmpr.compare(arr[i], key);
                        if (cmp < 0) {
                            buf[lfCur] = arr[i];
                            lfCur++;
                            arr[i] = null; // 作标记
                        } else if (cmp == 0) {
                            buf[midCur] = arr[i];
                            midCur--;
                            arr[i] = null; // 作标记
                        }  // else: pass
                    }
                    for (var i = right - 1; i >= left; i--) {
                        if (arr[i] != null) {
                            if (rtCur != i)
                                arr[rtCur] = arr[i];
                            rtCur--;
                        }
                    }
                    // 复制
                    if (lfCur - left >= 0)
                        System.arraycopy(buf, left, arr, left, lfCur - left);
                    for (var i = 0; i < right - 1 - midCur; i++)
                        arr[lfCur + i] = (S) buf[right - 1 - i];
                    // 分段
                    lfPivot = lfCur;
                    rtPivot = rtCur + 1;
                }
            }
            // 递归操作
            depthLimit--;
            sortOnceTri(arr, rtPivot, right, buf, depthLimit, cmpr);
            right = lfPivot;
        }
    }

    /**
     * 计算 lg2
     */
    private static int lg2(int n) {
        int k = 0;
        for (; n > 1; n >>= 1)
            k++;
        return k;
    }

    /**
     * 一种稳定混合排序算法
     * 处理范围：[left, right)
     */
    public static <S> void sort(S[] arr, int left, int right, Comparator<S> cmpr) {
        if (right - left < 2) // 已经有序
            return;

        int len = right - left;
        sortOnceTri(arr, left, right, new Object[len], lg2(len) * 2, cmpr);
        BiInsertSort.sort(arr, left, right, cmpr);
    }

    /**
     * 一种稳定混合排序算法，使用正常快排
     * 处理范围：[left, right)
     */
    public static <S> void normalSort(S[] arr, int left, int right, Comparator<S> cmpr) {
        if (right - left < 2) // 已经有序
            return;

        int len = right - left;
        sortOnce(arr, left, right, new Object[len], lg2(len) * 2, cmpr);
        BiInsertSort.sort(arr, left, right, cmpr);
    }
}
