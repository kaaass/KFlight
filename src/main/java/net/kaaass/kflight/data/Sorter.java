package net.kaaass.kflight.data;

import net.kaaass.kflight.data.sort.StableHybridSort;

import java.util.Comparator;
import java.util.List;

/**
 * 排序器
 */
public class Sorter {

    /**
     * 排序
     */
    public static <S> void sort(List<S> list, Comparator<S> comparator) {
        var arr = list.toArray();
        StableHybridSort.sort((S[]) arr, 0, arr.length, comparator);
        for (int i = 0; i < arr.length; i++) {
            list.set(i, (S) arr[i]);
        }
    }
}
