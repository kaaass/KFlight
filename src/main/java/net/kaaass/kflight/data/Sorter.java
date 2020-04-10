package net.kaaass.kflight.data;

import net.kaaass.kflight.data.algorithm.sort.StableHybridSort;
import net.kaaass.kflight.data.entry.EntryCity;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.exception.BadRequestException;

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

    /**
     * 降序排序
     */
    public static <S> void sortDesc(List<S> list, Comparator<S> comparator) {
        var arr = list.toArray();
        StableHybridSort.sort((S[]) arr, 0, arr.length, comparator);
        for (int i = 0; i < arr.length; i++) {
            list.set(arr.length - i - 1, (S) arr[i]);
        }
    }

    /**
     * 对一系列航班进行排序
     *
     * @param method 排序方法，由一系列字母组成，小写为升序，大写为降序，从前至后为主要程度
     *               允许的取值如下：
     *               f - 起飞地点
     *               t - 降落地点
     *               d - 起飞时间
     *               l - 降落时间
     *               w - 航行时间
     *               p - 当前票价
     *               r - 剩余票数
     */
    public static void sortFlight(List<EntryFlight> flights, String method) throws BadRequestException {
        if (method == null)
            return;
        for (int i = method.length() - 1; i >= 0; i--) {
            var ch = method.charAt(i);
            Comparator<EntryFlight> cmpr;
            switch (Character.toLowerCase(ch)) {
                case 'f':
                    // 起飞地点
                    cmpr = Comparator.comparing(o -> o.getFrom().getName());
                    break;
                case 't':
                    // 降落地点
                    cmpr = Comparator.comparing(o -> o.getTo().getName());
                    break;
                case 'd':
                    // 起飞时间
                    cmpr = Comparator.comparing(EntryFlight::getDepartureTime);
                    break;
                case 'l':
                    // 降落时间
                    cmpr = Comparator.comparing(EntryFlight::getLandingTime);
                    break;
                case 'w':
                    // 航行时间
                    cmpr = Comparator.comparing(EntryFlight::getFlightTime);
                    break;
                case 'p':
                    // 当前票价
                    cmpr = Comparator.comparing(EntryFlight::getTicketPrice);
                    break;
                case 'r':
                    // 剩余票数
                    cmpr = Comparator.comparing(EntryFlight::getRestCabin);
                    break;
                default:
                    throw new BadRequestException("不支持此排序方法！");
            }
            // 排序
            if (Character.isUpperCase(ch))
                sortDesc(flights, cmpr); // 大写倒序
            else
                sort(flights, cmpr);
        }
    }
}
