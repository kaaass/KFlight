package net.kaaass.kflight.algorithm.sort;

import java.util.Comparator;

/**
 * 稳定化快速排序，加入了三者取中、双枢轴优化
 * <p>
 * 优化思路参考 {@see #sort}
 */
public class StableTriQuickSort {

    /**
     * 尾递归优化的稳定三者取中快速排序
     * 处理范围：[left, right)
     */
    public static <S> void sort(S[] arr, int left, int right, Comparator<S> cmp) {
        if (right - left < 2) // 已经有序
            return;
        sort(arr, left, right, new Object[right - left], cmp);
    }

    /**
     * 尾递归优化的稳定三者取中快速排序，需要传入临时数组 buf
     * 处理范围：[left, right)
     */
    static <S> void sort(S[] arr, int left, int right, Object[] buf, Comparator<S> cmpr) {
        if (right - left < 2) // 已经有序
            return;

        int lfPivot, rtPivot;
        while (right - left > 1) {
            // 处理分段
            lfPivot = left;
            rtPivot = right;
            if (right - left >= 2) {
                /*
                 * 选择 Key 算法的推导如下，假设三个数 a, b, c，首先计算：
                 *   c1 = a < b
                 *   c2 = b < c
                 *   c3 = a < c
                 * 之后，可以列出下标：
                 *   ret | c1 | c2 | c3
                 *    b  | 0  | 0  | 0
                 *    _  | 0  | 0  | 1
                 *    c  | 0  | 1  | 0
                 *    a  | 0  | 1  | 1
                 *    a  | 1  | 0  | 0
                 *    c  | 1  | 0  | 1
                 *    _  | 1  | 1  | 0
                 *    b  | 1  | 1  | 1
                 * 观察表格，可以发现表格上下对称，因此可以用异或进行判断。
                 * 由于 compare 的代价较高，并且一般情况下数据有一定顺序，
                 * 因此优先选择位置靠中的 b，若非 b 再计算 c3。
                 */
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
                /*
                 * 排序采用了双枢轴优化。不过此处的优化与 DualPivot 不同，
                 * 两个枢轴之间仅仅存放与 key 相同的数据。由于为了维持排序
                 * 稳定使用了额外空间 buf，因此可以利用该空间减少赋值操作。
                 * 为了减少比较次数，采用了在 buf 不同位置存放，并且使用
                 * null 做标记。
                 *
                 * left         lfPivot         rtPivot        right
                 * | ... < key ... | ... = key ... | ... > key ... |
                 */
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
            // 继续排序
            sort(arr, rtPivot, right, buf, cmpr);
            right = lfPivot;
        }
    }
}
