package net.kaaass.kflight.data.sort;

import java.util.Comparator;

/**
 * 自适应归并排序的非递归实现
 * <p>
 * 参考了 TimeSort 的思想与 C++ std::stable_sort 的实现
 */
public class AdaptiveMergeSort {

    /**
     * 将首个元素插入到之后的有序序列中
     * 传入序列必须保证 [left + 1, right) 有序
     */
    private static <S> void insertFirst(S[] arr, int left, int right, Comparator<S> cmp) {
        var cur = left;
        var target = arr[left];

        while (cur < right - 1 && cmp.compare(target, arr[cur + 1]) > 0) {
            arr[cur] = arr[cur + 1];
            cur++;
        }
        arr[cur] = target;
    }

    /**
     * 合并两个有序序列
     * 与普通归并排序的合并操作一致
     */
    private static <S> void merge(S[] arr, int lf, int mid, int rt, Object[] buf, Comparator<S> cmpr) {
        int lfCur = lf, rtCur = mid, cur = lf;
        var cmp = 0;

        while (lfCur < mid || rtCur < rt) {
            if (lfCur >= mid) { // no rest left
                cmp = 1;
            } else if (rtCur >= rt) { // no rest right
                cmp = -1;
            } else {
                cmp = cmpr.compare(arr[lfCur], arr[rtCur]);
            }
            if (cmp <= 0) { // equal => left
                buf[cur++] = arr[lfCur];
                lfCur++;
            } else {
                buf[cur++] = arr[rtCur];
                rtCur++;
            }
        }
        if (rt - lf >= 0)
            System.arraycopy(buf, lf, arr, lf, rt - lf);
    }

    /**
     * 自适应归并排序的非递归实现
     * 处理范围：[left, right)
     */
    public static <S> void sort(S[] arr, int left, int right, Comparator<S> cmp) {
        if (right - left < 2) // 已经有序
            return;
        sort(arr, left, right, new Object[right - left], cmp);
    }

    /**
     * 自适应归并排序的非递归实现，需要传入临时数组 buf
     * 处理范围：[left, right)
     */
    static <S> void sort(S[] arr, int left, int right, Object[] buf, Comparator<S> cmp) {
        if (right - left < 2) // 已经有序
            return;

        // buf 长度不足
        if (buf.length < arr.length) {
            sort(arr, left, right, cmp);
            return;
        }

        /*
         * 算法从右到左查找升序段，并按照策略合并升序段
         * 升序段维持性质：每个升序段至少比左侧的（即下一个升序段）大2倍
         * 通过维持这个性质，尽可能保证两个升序段在合并时长度接近（不小于一半）
         * 在合并时，若当前段过大，则优先合并之后两段
         */

        int head = right;

        // 存放升序段（称run）的栈
        var runStack = new int[32]; // 最多 2^32 - 1 元素
        int runSize = 0;

        do {
            int mid = head;
            head--;

            // 寻找升序段 head ... mid
            while (head > left) {
                if (cmp.compare(arr[head - 1], arr[head]) > 0) {
                    if (mid - head < 4) // 升序段太短
                        insertFirst(arr, head - 1, mid, cmp); // 从左到右进行插入
                    else
                        break;
                }
                head--;
            }

            // 此时新升序段未入栈。如果还有其他段，从左到右（弹栈）检查以维持性质
            // 最大同时弹出3段，指针分布：head ... mid ... tail ... nextTail
            while (runSize >= 1) {
                // 取最近一段：mid ... tail
                int tail = runStack[runSize - 1];

                // 如果有三段以上
                while (runSize >= 2) {
                    // 取之前一段：tail ... nextTail
                    int nextTail = runStack[runSize - 2];

                    // 如果当前升序段比之前段要短，则可以直接处理前2段
                    if ((mid - head) <= (nextTail - tail))
                        break;

                    // 如果当前升序段比之前段要长，则需要防止出现合并的两端长度差距过大
                    // 因此此时合并 mid ... tail ... nextTail
                    // 根据性质合并之后的新段小于更之前的段，继续循环处理
                    merge(arr, mid, tail, nextTail, buf, cmp);

                    // 弹栈并向后处理
                    tail = nextTail;
                    runSize--;
                }

                // 检查是否保持性质，或在 head <= left 时循环合并所有分段
                if (head > left && (mid - head) <= (tail - mid) / 2)
                    break;

                // 如果不符合性质，合并相邻两个升序段
                merge(arr, head, mid, tail, buf, cmp);

                // 遍历前一分段
                mid = tail;
                runSize--;
            }

            // 增加查找到的升序段
            runStack[runSize] = mid;
            runSize++;
        }
        while (head > left);
    }
}
