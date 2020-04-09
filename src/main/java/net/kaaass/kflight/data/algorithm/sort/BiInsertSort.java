package net.kaaass.kflight.data.algorithm.sort;

import java.util.Comparator;

/**
 * 成对插入排序，在快排场景下处理效率更高
 */
public class BiInsertSort {

    /**
     * 成对插入排序，在快排场景下处理效率更高
     * 处理范围：[left, right)
     */
    public static <S> void sort(S[] arr, int left, int right, Comparator<S> cmp) {
        if (right - left < 2) // 已经有序
            return;
        
        int cur = left; // 已处理部分的末尾
        right--;

        // 检测开头是否有升序段
        do {
            if (cur >= right)
                return;
        } while (cmp.compare(arr[++cur], arr[cur - 1]) >= 0);

        // 成对插入排序主逻辑
        for (var i = cur; cur < right; i = ++cur) {
            cur++;
            S fst = arr[i], snd = arr[cur];
            if (cmp.compare(fst, snd) < 0) {
                snd = fst;
                fst = arr[cur];
            }
            // 此时 i, cur 分别指向待插入元素，并且 fst > snd
            // 先插入较大元素 fst
            while (--i >= left && cmp.compare(fst, arr[i]) < 0) {
                arr[i + 2] = arr[i]; // 偏移为 2 保证元素位置比 fst, snd 都大
            }
            arr[++i + 1] = fst;
            // 再插入较小元素 snd
            while (--i >= left && cmp.compare(snd, arr[i]) < 0) {
                arr[i + 1] = arr[i];
            }
            arr[i + 1] = snd;
        }

        // 处理多余元素
        if (cur == right) {
            var tail = arr[right];
            while (--cur >= left && cmp.compare(tail, arr[cur]) < 0) {
                arr[cur + 1] = arr[cur];
            }
            arr[cur + 1] = tail;
        }
    }
}
