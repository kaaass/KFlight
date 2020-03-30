package net.kaaass.kflight.data.structure;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Comparator;

/**
 * 红黑树的简单实现
 *
 * @param <K> 查询下标类型
 * @param <V> 存放内容类型
 */
public class RBTree<K, V> {

    /**
     * 查询下标比较器，建立偏序关系
     */
    private Comparator<? super K> comparator;

    /**
     * 树根
     */
    private TreeNode<K, V> root = null;

    /**
     * 红黑树元素数
     */
    private int size = 0;

    public RBTree(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    /**
     * 获得红黑树元素数
     */
    public int size() {
        return size;
    }

    /**
     * 红黑树节点颜色
     */
    enum NodeColor {
        BLACK, RED
    }

    /**
     * 红黑树节点
     */
    @Getter
    @ToString(exclude = {"parent"})
    @EqualsAndHashCode
    @AllArgsConstructor
    public static final class TreeNode<K, V> {
        K key;
        V value;
        TreeNode<K, V> parent = null;
        NodeColor color = NodeColor.BLACK;
        TreeNode<K, V> left = null;
        TreeNode<K, V> right = null;

        public TreeNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * 公共空子节点
     */
    private final TreeNode<K, V> NULL = new TreeNode<>(null, null);

    /**
     * 判断是否为空节点
     */
    private boolean isNull(TreeNode<K, V> node) {
        return node == null || node == NULL;
    }

    private boolean notNull(TreeNode<K, V> node) {
        return !isNull(node);
    }

    /**
     * 寻找某节点在红黑树内的下一个节点
     *
     * @return 找不到返回 null
     */
    public TreeNode<K, V> nextOf(TreeNode<K, V> curNode) {
        if (isNull(curNode))
            return null;
        // 逻辑
        if (notNull(curNode.right)) {
            // 右子节点的最左
            var cur = curNode.right;
            while (notNull(cur.left))
                cur = cur.left;
            return cur;
        }
        // 若无右子节点，寻找非右子节点父结点（即“拐弯”）
        var prev = curNode.parent;
        var cur = curNode;
        while (notNull(prev) && cur == prev.right) {
            cur = prev;
            prev = prev.parent;
        }
        if (prev == NULL)
            return null;
        return prev;
    }

    /**
     * 寻找某节点在红黑树内的上一个节点
     *
     * @return 找不到返回 null
     */
    public TreeNode<K, V> prevOf(TreeNode<K, V> curNode) {
        if (isNull(curNode))
            return null;
        // 逻辑
        if (notNull(curNode.left)) {
            // 左子节点的最右
            var cur = curNode.left;
            while (notNull(cur.right))
                cur = cur.right;
            return cur;
        }
        // 若无左子节点，寻找非左子节点父结点（即“拐弯”）
        var prev = curNode.parent;
        var cur = curNode;
        while (notNull(prev) && cur == prev.left) {
            cur = prev;
            prev = prev.parent;
        }
        if (prev == NULL)
            return null;
        return prev;
    }

    /**
     * 获取下标最小节点
     */
    public TreeNode<K, V> getMinimumNode() {
        return getMinimumNode(root);
    }

    public TreeNode<K, V> getMinimumNode(TreeNode<K, V> cur) {
        if (notNull(cur)) {
            // 取最左元素
            while (notNull(cur.left))
                cur = cur.left;
        }
        return cur;
    }

    /**
     * 获取下标最大节点
     */
    public TreeNode<K, V> getMaximumNode() {
        var cur = root;
        if (notNull(cur)) {
            // 取最右元素
            while (notNull(cur.right))
                cur = cur.right;
        }
        return cur;
    }

    /*
    旋转操作：
          (y)                        (x)
         /  \   <---------------    /  \
       (x)  γ   leftRotate(x)      α  (y)
      /  \        rightRotate(y)     /  \
     α   β      --------------->    β   γ
     */

    /**
     * 子树左旋
     * <p>
     * 代码参考自《算法导论》
     */
    private void leftRotate(TreeNode<K, V> x) {
        if (notNull(x)) {
            var y = x.right;
            x.right = y.left;
            if (notNull(y.left))
                y.left.parent = x;
            y.parent = x.parent;
            if (isNull(x.parent))
                this.root = y;
            else if (x == x.parent.left)
                x.parent.left = y;
            else
                x.parent.right = y;
            y.left = x;
            x.parent = y;
        }
    }

    /**
     * 子树右旋
     * <p>
     * 代码参考自《算法导论》
     */
    private void rightRotate(TreeNode<K, V> y) {
        if (notNull(y)) {
            var x = y.left;
            y.left = x.right;
            if (notNull(x.right))
                x.right.parent = y;
            x.parent = y.parent;
            if (isNull(y.parent))
                root = x;
            else if (y == y.parent.right)
                y.parent.right = x;
            else
                y.parent.left = x;
            x.right = y;
            y.parent = x;
        }
    }

    /**
     * 插入操作
     * <p>
     * 代码参考自《算法导论》
     */
    public void insert(K key, V value) {
        if (key == null)
            return;
        var cmp = comparator;
        var x = root;
        TreeNode<K, V> y = null;
        var z = new TreeNode<>(key, value);
        z.left = z.right = NULL;

        while (notNull(x)) {
            y = x;
            if (cmp.compare(z.key, x.key) < 0)
                x = x.left;
            else
                x = x.right;
        }
        z.parent = y;
        if (isNull(y)) {
            // 根节点
            root = z;
            size = 1;
            return;
        } else if (cmp.compare(z.key, y.key) < 0)
            y.left = z;
        else
            y.right = z;
        z.color = NodeColor.RED;
        insertFixup(z);
        size++;
    }

    /**
     * 插入操作后维持树性质
     * <p>
     * 代码参考自《算法导论》
     */
    private void insertFixup(TreeNode<K, V> z) {
        while (notNull(z) && z != root && z.parent.color == NodeColor.RED) {
            if (z.parent == z.parent.parent.left) {
                var y = z.parent.parent.right;
                if (y.color == NodeColor.RED) {
                    // Case 1
                    z.parent.color = NodeColor.BLACK;
                    y.color = NodeColor.BLACK;
                    z.parent.parent.color = NodeColor.RED;
                    z = z.parent.parent;
                } else {
                    // Case 2
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = NodeColor.BLACK;
                    z.parent.parent.color = NodeColor.RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                var y = z.parent.parent.left;
                if (y.color == NodeColor.RED) {
                    // Case 1
                    z.parent.color = NodeColor.BLACK;
                    y.color = NodeColor.BLACK;
                    z.parent.parent.color = NodeColor.RED;
                    z = z.parent.parent;
                } else {
                    // Case 2
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = NodeColor.BLACK;
                    z.parent.parent.color = NodeColor.RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = NodeColor.BLACK;
    }

    /**
     * 用 v 节点替换 u 节点
     */
    private void transplant(TreeNode<K, V> u, TreeNode<K, V> v) {
        if (isNull(u.parent))
            root = v;
        else if (u == u.parent.left)
            u.parent.left = v;
        else
            u.parent.right = v;
        v.parent = u.parent;
    }

    /**
     * 删除操作
     * <p>
     * 代码参考自《算法导论》
     */
    public void delete(TreeNode<K, V> z) {
        if (isNull(z))
            return;
        TreeNode<K, V> x;
        var y = z;
        var yOriginColor = y.color;
        // 删除操作和正常二叉查找树删除类似
        if (isNull(z.left)) {
            x = z.right;
            transplant(z, z.right);
        } else if (isNull(z.right)) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = getMinimumNode(z.right);
            yOriginColor = y.color;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        size--;
        // 黑色节点的删除会破坏性质
        if (yOriginColor == NodeColor.BLACK)
            deleteFixup(x);
    }

    /**
     * 删除操作后维持树性质
     * <p>
     * 代码参考自《算法导论》
     */
    private void deleteFixup(TreeNode<K, V> x) {
        if (isNull(x))
            return;
        while (x != root && x.color == NodeColor.BLACK) {
            if (x == x.parent.left) {
                var w = x.parent.right; // 兄弟节点

                if (w.color == NodeColor.RED) {
                    // Case 1
                    w.color = NodeColor.BLACK;
                    x.parent.color = NodeColor.RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (w.left.color == NodeColor.BLACK &&
                        w.right.color == NodeColor.BLACK) {
                    // Case 2
                    w.color = NodeColor.RED;
                    x = x.parent;
                } else {
                    if (w.right.color == NodeColor.BLACK) {
                        // Case 3
                        w.left.color = NodeColor.BLACK;
                        w.color = NodeColor.RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    // Case 4
                    w.color = x.parent.color;
                    x.parent.color = NodeColor.BLACK;
                    w.right.color = NodeColor.BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                var w = x.parent.left;  // 兄弟节点

                if (w.color == NodeColor.RED) {
                    // Case 1
                    w.color = NodeColor.BLACK;
                    x.parent.color = NodeColor.RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (w.left.color == NodeColor.BLACK &&
                        w.right.color == NodeColor.BLACK) {
                    // Case 2
                    w.color = NodeColor.RED;
                    x = x.parent;
                } else {
                    if (w.left.color == NodeColor.BLACK) {
                        // Case 3
                        w.right.color = NodeColor.BLACK;
                        w.color = NodeColor.RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    // Case 4
                    w.color = x.parent.color;
                    x.parent.color = NodeColor.BLACK;
                    w.left.color = NodeColor.BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = NodeColor.BLACK;
    }

    /**
     * 删除某一键值对
     */
    public boolean deletePair(K key, V val) {
        var cur = findLowerBound(key);
        if (isNull(cur))
            return false;
        // 遍历寻找节点
        while (notNull(cur) && cur.key.equals(key)) {
            if (cur.value.equals(val)) {
                // 找到并删除节点
                delete(cur);
                return true;
            }
            cur = nextOf(cur);
        }
        return false;
    }

    /**
     * 通过 key 找到一个结点，若重复，则找到其中一个
     *
     * @return 若找不到则返回 null
     */
    public TreeNode<K, V> findOne(K key) {
        var cur = root;
        while (notNull(cur)) {
            int cmp = comparator.compare(key, cur.key);
            if (cmp < 0)
                cur = cur.left;
            else if (cmp > 0)
                cur = cur.right;
            else
                return cur;
        }
        return null;
    }

    /**
     * 找到小于 key 的最大节点
     *
     * @return 若找不到则返回 null
     */
    public TreeNode<K, V> findPrev(K key) {
        var cur = root;
        while (notNull(cur)) {
            int cmp = comparator.compare(key, cur.key);
            if (cmp > 0) {
                // 当前小于 key，向右查找
                if (notNull(cur.right))
                    cur = cur.right;
                else
                    return cur;
            } else {
                // 当前大于 key，找上一节点
                if (notNull(cur.left)) {
                    cur = cur.left;
                } else {
                    var prev = cur.parent;
                    var tCur = cur;
                    while (notNull(prev) && tCur == prev.left) {
                        tCur = prev;
                        prev = prev.parent;
                    }
                    return prev;
                }
            }
        }
        return null;
    }

    /**
     * 找到大于 key 的最小节点
     *
     * @return 若找不到则返回 null
     */
    public TreeNode<K, V> findNext(K key) {
        var cur = root;
        while (cur != null) {
            int cmp = comparator.compare(key, cur.key);
            if (cmp < 0) {
                if (notNull(cur.left))
                    cur = cur.left;
                else
                    return cur;
            } else {
                if (notNull(cur.right)) {
                    cur = cur.right;
                } else {
                    var prev = cur.parent;
                    var tCur = cur;
                    while (notNull(prev) && tCur == prev.right) {
                        tCur = prev;
                        prev = prev.parent;
                    }
                    return prev;
                }
            }
        }
        return null;
    }

    /**
     * 找到不小于 key 的最小节点
     *
     * @return 若找不到则返回 null
     */
    public TreeNode<K, V> findLowerBound(K key) {
        var prev = findPrev(key);
        if (isNull(prev))
            return getMinimumNode();
        return nextOf(prev);
    }

    /**
     * 找到不大于 key 的最大节点
     *
     * @return 若找不到则返回 null
     */
    public TreeNode<K, V> findUpperBound(K key) {
        var next = findNext(key);
        if (isNull(next))
            return getMaximumNode();
        return prevOf(next);
    }

    /**
     * 比较两节点在树中的位置
     */
    public int compareNode(TreeNode<K, V> a, TreeNode<K, V> b) {
        if (a == b)
            return 0;
        if (!a.key.equals(b.key))
            return comparator.compare(a.key, b.key);
        // Case: key 相同时
        var key = a.key;
        var cur = a;
        // 可以使用LCA优化，不过重复数据量有限，一般不需要
        while (notNull(a) && cur.key.equals(key)) {
            if (cur == b)
                return -1;
            cur = nextOf(cur);
        }
        return 1;
    }

    /**
     * 树中是否存在 key
     */
    public boolean has(K key) {
        return notNull(findOne(key));
    }

    /**
     * 树中是否存在键值对
     */
    public boolean has(K key, V val) {
        var cur = findLowerBound(key);
        if (isNull(cur))
            return false;
        // 遍历寻找节点
        while (notNull(cur) && cur.key.equals(key)) {
            if (cur.value.equals(val))
                return true;
            cur = nextOf(cur);
        }
        return false;
    }
}
