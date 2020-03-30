package net.kaaass.kflight.data;

import net.kaaass.kflight.data.entry.IEntry;
import net.kaaass.kflight.data.structure.RBTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 数据的索引
 *
 * @param <S> Entry 类型
 * @param <T> 索引项数据类型
 * @param <I> 对应索引类型
 */
public class Index<S extends IEntry, T, I> {

    /**
     * 由 Entry 获得数据的函数
     */
    private Function<S, T> funcDataFetcher;

    /**
     * 由数据构建索引
     */
    private Function<T, I> funcIndex;

    /**
     * 索引比较器
     */
    private Comparator<I> comparator;

    /**
     * 红黑树，用于存放索引
     */
    private RBTree<I, S> rbTree;

    public Index(Function<S, T> funcDataFetcher, Function<T, I> funcIndex, Comparator<I> comparator) {
        this.funcDataFetcher = funcDataFetcher;
        this.funcIndex = funcIndex;
        this.comparator = comparator;
        this.rbTree = new RBTree<>(comparator);
    }

    /**
     * 返回索引大小
     */
    public int size() {
        return rbTree.size();
    }

    /**
     * 计算 entry 的索引值
     */
    public I indexOf(S entry) {
        return funcIndex.apply(funcDataFetcher.apply(entry));
    }

    /**
     * 计算 entry 的索引值
     */
    public I indexOf(T entry) {
        return funcIndex.apply(entry);
    }

    /**
     * 向索引中添加 entry
     */
    public void addIndexFor(S entry) {
        rbTree.insert(indexOf(entry), entry);
    }

    /**
     * 删除 entry 的索引
     */
    public boolean removeIndexFor(S entry) {
        return rbTree.deletePair(indexOf(entry), entry);
    }

    /**
     * 通过索引项寻找一个对象
     *
     * @return 若不存在，返回 null
     */
    public S findOneRaw(T ind) {
        var node = rbTree.findOne(indexOf(ind));
        if (node == null)
            return null;
        return node.getValue();
    }

    /**
     * 通过索引项寻找一个 Optional 对象
     */
    public Optional<S> findOne(T ind) {
        return Optional.ofNullable(findOneRaw(ind));
    }

    /**
     * 通过索引项寻找全部对象
     */
    public List<S> findAll(T ind) {
        var result = new ArrayList<S>();
        var index = indexOf(ind);
        var cur = rbTree.findLowerBound(index);
        if (cur != null) {
            for (; cur != null && cur.getKey().equals(index); cur = rbTree.nextOf(cur)) {
                result.add(cur.getValue());
            }
        }
        return result;
    }

    /**
     * 通过索引项寻找索引项之间的全部对象
     * <p>
     * 寻找范围为 [low, high]
     */
    public List<S> findBetween(T low, T high) {
        var result = new ArrayList<S>();
        var indLow = indexOf(low);
        var indHigh = indexOf(high);
        var cur = rbTree.findLowerBound(indLow);
        var end = rbTree.findUpperBound(indHigh);
        if (cur == null || end == null)
            return result;
        if (rbTree.compareNode(cur, end) > 0) // 即 low > high
            return result;
        for (; cur != null && cur != end; cur = rbTree.nextOf(cur)) {
            result.add(cur.getValue());
        }
        return result;
    }

    /**
     * 是否索引项目 entry
     */
    public boolean has(S entry) {
        var index = indexOf(entry);
        return rbTree.has(index, entry);
    }

    /**
     * 是否存在索引
     */
    public boolean has(T ind) {
        var index = indexOf(ind);
        return rbTree.has(index);
    }
}
