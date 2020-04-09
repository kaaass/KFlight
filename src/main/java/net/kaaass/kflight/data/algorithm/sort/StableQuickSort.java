package net.kaaass.kflight.data.algorithm.sort;

import java.util.Comparator;

/**
 * 稳定快速排序
 */
public class StableQuickSort {

    /**
     * 快速排序处理分段
     */
    static <S> int partition(S[] arr, int left, int right, Object[] buf, Comparator<S> cmp) {
        if (right - left < 2)
            return left;
        var key = arr[left];
        int lfCur = left, rtCur = right - 1;
        for (var i = left; i < right; i++) {
            if (cmp.compare(arr[i], key) < 0) {
                buf[lfCur] = arr[i];
                lfCur++;
                arr[i] = null; // 作标记
            }
        }
        for (var i = right - 1; i >= left; i--) {
            if (arr[i] != null) {
                if (rtCur != i)
                    arr[rtCur] = arr[i];
                rtCur--;
            }
        }
        if (lfCur - left >= 0)
            System.arraycopy(buf, left, arr, left, lfCur - left);
        return lfCur + 1;
    }

    /**
     * 尾递归优化的稳定快速排序
     * 处理范围：[left, right)
     */
    public static <S> void sort(S[] arr, int left, int right, Comparator<S> cmp) {
        if (right - left < 2) // 已经有序
            return;
        sort(arr, left, right, new Object[right - left], cmp);
    }

    /**
     * 尾递归优化的稳定快速排序，需要传入临时数组 buf
     * 处理范围：[left, right)
     */
    static <S> void sort(S[] arr, int left, int right, Object[] buf, Comparator<S> cmp) {
        if (right - left < 2) // 已经有序
            return;

        int pivot;
        while (right - left > 1) {
            pivot = partition(arr, left, right, buf, cmp);
            sort(arr, pivot, right, buf, cmp);
            right = pivot;
        }
    }
}
