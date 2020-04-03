package net.kaaass.kflight.data.sort;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 以 IntroSort 为范本的一种稳定混合排序算法
 * <p>
 * 使用了：成对排序、稳定快速排序、自适应归并排序
 */
public class StableHybridSort {

    private static int INSERT_THRESHOLD = 16;

    /**
     * 递归式单层遍历的排序操作
     */
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
     */
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
    public static <S> void sort(S[] arr, int left, int right, Comparator<S> cmp) {
        if (right - left < 2) // 已经有序
            return;

        int len = right - left;
        sortOnce(arr, left, right, new Object[len], lg2(len) * 2, cmp);
        BiInsertSort.sort(arr, left, right, cmp);
    }
}
