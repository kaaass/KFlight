package net.kaaass.kflight.algorithm;

import net.kaaass.kflight.data.structure.HashSet;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合求交集
 */
public class SetIntersect {

    /**
     * 求集合交集，返回元素列表
     */
    public static <S> List<S> intersectList(HashSet<S> setA, HashSet<S> setB) {
        var a = setA.toNodeList();
        var b = setB.toNodeList();
        int lf = 0, rt = 0, lfEnd = 0, rtEnd = 0;
        var ret = new ArrayList<S>();
        while (lf < a.size() && rt < b.size()) {
            // lfEnd 移动
            int lfCur = a.get(lf).getKey();
            while (lfEnd < a.size() && a.get(lfEnd).getKey() <= lfCur)
                lfEnd++;
            // rtEnd 移动
            int rtCur = b.get(rt).getKey();
            while (rtEnd < b.size() && b.get(rtEnd).getKey() <= rtCur)
                rtEnd++;
            // 比较并移动
            if (lfCur < rtCur) {
                lf = lfEnd;
            } else if (lfCur > rtCur) {
                rt = rtEnd;
            } else {
                // 比较元素值
                for (var i = lf; i < lfEnd; i++) {
                    for (var j = rt; j < rtEnd; j++) {
                        if (a.get(i).getValue().equals(b.get(j).getValue())) {
                            ret.add(a.get(i).getValue());
                            break; // 有也是重复
                        }
                    }
                }
                lf = lfEnd;
                rt = rtEnd;
            }
        }
        return ret;
    }

    /**
     * 求集合交集
     */
    public static <S> HashSet<S> intersect(HashSet<S> setA, HashSet<S> setB) {
        var ret = new HashSet<S>();
        intersectList(setA, setB).forEach(ret::insert);
        return ret;
    }
}
