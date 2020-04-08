package net.kaaass.kflight.data.structure;

import java.util.ArrayList;
import java.util.List;

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
     * 将集合中全部元素存储于列表
     */
    public List<S> toList() {
        var ret = new ArrayList<S>();
        var cur = tree.getMinimumNode();
        if (cur != null) {
            for (; cur != null; cur = tree.nextOf(cur)) {
                ret.add(cur.value);
            }
        }
        return ret;
    }
}
