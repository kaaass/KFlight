package net.kaaass.kflight.data.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 基于红黑树的哈希集合，不允许重复元素
 */
public class HashSet<S> {

    private RBTree<Integer, S> tree;

    public HashSet() {
        tree = new RBTree<>(Integer::compareTo);
    }

    /**
     * 向集合插入元素
     *
     * @return 是否成功
     */
    public boolean insert(S value) {
        var hash = value.hashCode();

        // 检查重复
        if (inSet(value))
            return false;
        // 若无重复则插入
        tree.insert(hash, value);
        return true;
    }

    /**
     * 将另一集合的所有元素加入本集合
     */
    public void addAll(HashSet<S> anotherSet) {
        anotherSet.forEach(this::insert);
    }

    /**
     * 检查元素是否在集合内
     */
    public boolean inSet(S value) {
        var hash = value.hashCode();
        var cur = tree.findLowerBound(hash);
        if (cur != null) {
            for (; cur != null && cur.getKey().equals(hash); cur = tree.nextOf(cur)) {
                if (cur.getValue().equals(value))
                    return true;
            }
        }
        return false;
    }

    /**
     * 获得集合大小
     */
    public int size() {
        return tree.size();
    }

    /**
     * 节点遍历
     */
    public void forEachNode(Consumer<RBTree.TreeNode<Integer, S>> consumer) {
        var cur = tree.getMinimumNode();
        if (cur != null) {
            for (; cur != null; cur = tree.nextOf(cur)) {
                consumer.accept(cur);
            }
        }
    }

    /**
     * 元素遍历
     */
    public void forEach(Consumer<S> consumer) {
        forEachNode(node -> consumer.accept(node.value));
    }

    /**
     * 将集合中全部元素以哈希升序存储于列表
     */
    public List<S> toList() {
        var ret = new ArrayList<S>();
        forEach(ret::add);
        return ret;
    }

    /**
     * 将集合中全部元素以哈希升序存储于列表，键为哈希
     */
    public List<RBTree.TreeNode<Integer, S>> toNodeList() {
        var ret = new ArrayList<RBTree.TreeNode<Integer, S>>();
        forEachNode(ret::add);
        return ret;
    }

    /**
     * 非常缓慢的集合比较
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            throw new NullPointerException();
        if (!(obj instanceof HashSet))
            return false;
        var aSet = (HashSet<S>) obj;
        // 循环判断 O(m * n)
        var ret = new AtomicBoolean(true);
        forEach(el -> {
            if (ret.get()) {
                var flag = new AtomicBoolean(false);
                aSet.forEach(other -> {
                    if (!flag.get() && el.equals(other)) {
                        flag.set(true);
                    }
                });
                if (!flag.get())
                    ret.set(false);
            }
        });
        return ret.get();
    }
}
