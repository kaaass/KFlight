package net.kaaass.kflight.data;

import net.kaaass.kflight.data.entry.IEntry;

import java.util.Comparator;
import java.util.function.Function;

/**
 * 数据的索引
 *
 * @param <S> Entry 类型
 * @param <T> 对应数据类型
 */
public class Index<S extends IEntry, T> {

    /**
     * 由 Entry 获得数据的函数
     */
    private Function<S, T> funcDataFetcher;

    private Comparator<? super S> comparator;


}
