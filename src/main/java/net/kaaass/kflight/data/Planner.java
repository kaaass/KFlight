package net.kaaass.kflight.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kaaass.kflight.data.algorithm.SetIntersect;
import net.kaaass.kflight.data.entry.EntryCity;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.data.structure.HashSet;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 航线规划类
 */
public class Planner {

    /**
     * 飞行计划
     */
    @Data
    @AllArgsConstructor
    public final static class FlightPlan {
        float totalCost;
        long totalTime;
        List<EntryFlight> flights;
    }

    /**
     * 制定机票中转方案
     */
    public static List<FlightPlan> plan(EntryCity from, EntryCity to, LocalDate date) {
        // TODO
        return Collections.emptyList();
    }

    /**
     * 寻找两个转机 1 次的方案
     */
    static int searchGapOne(List<FlightPlan> result, EntryCity from, EntryCity to, LocalDate date, int searchLimit) {
        var froms = FlightManager.findByFromAndDate(from, date);
        var tos = FlightManager.findByToAndDate(to, date);
        // 提取中转城市集合
        var fromsCity = froms.parallelStream()
                .map(flight -> flight.getTo().getName()) // 出发航班的目的
                .collect(HashSet<String>::new, HashSet::insert, HashSet::addAll);
        var tosCity = tos.parallelStream()
                .map(flight -> flight.getFrom().getName()) // 到达航班的出发
                .collect(HashSet<String>::new, HashSet::insert, HashSet::addAll);
        // 求中转城市
        var midCity = SetIntersect.intersect(fromsCity, tosCity);
        // 找出所有中转航班
        var fromFlightsStream = froms.parallelStream()
                .filter(flight -> midCity.inSet(flight.getTo().getName()))
                .filter(Planner::couldSellTicket);  // 保证可购买
        var toFlights = tos.parallelStream()
                .filter(flight -> midCity.inSet(flight.getFrom().getName()))
                .filter(Planner::couldSellTicket)
                .collect(Collectors.toList());  // 保证可购买
        // 对中转航班进行筛选，并拼接结果
        var limit = new AtomicInteger(searchLimit);
        fromFlightsStream
                .flatMap(prevFlight -> limit.get() <= 0 ? Stream.empty() : // 确保搜索未超界限
                        toFlights
                                .parallelStream()
                                .filter(nxtFlight -> {                     // 使用 flatMap 嵌套模拟笛卡尔积
                                    // 参数：prevFlight、toFlight
                                    // 目的：筛选出符合转机要求的航班
                                    if (limit.get() <= 0)
                                        return false;
                                    limit.decrementAndGet();
                                    // 航班中转地检查
                                    if (!prevFlight.getTo().equals(nxtFlight.getFrom()))
                                        return false;
                                    // 航班间隔必须大于等于 40 分钟
                                    var dur = Duration.between(prevFlight.getLandingTime(),
                                            nxtFlight.getDepartureTime());
                                    if (dur.toMinutes() < 40)
                                        return false;
                                    // 其余情况则符合要求
                                    return true;
                                })
                                .map(nxtFlight -> {                        // 包装为航班计划
                                    float totalCost = prevFlight.getTicketPrice()
                                            + nxtFlight.getTicketPrice();
                                    long totalTime =
                                            Duration.between(prevFlight.getDepartureTime(),
                                                    nxtFlight.getLandingTime()).toSeconds();
                                    var flights = new ArrayList<EntryFlight>();
                                    flights.add(prevFlight);
                                    flights.add(nxtFlight);
                                    return new FlightPlan(totalCost, totalTime, flights);
                                }))
                .forEach(result::add);                                     // 将剩余结果添加至列表
        return limit.get();
    }

    /**
     * 寻找两个转机多次的方案
     */
    static int searchGapMulti(List<FlightPlan> result, EntryCity from, EntryCity to, LocalDate date, int searchLimit) {
        // TODO
        return searchLimit;
    }

    /**
     * 谓词：航班是否可销售并且有余票
     */
    private static boolean couldSellTicket(EntryFlight flight) {
        return flight.getState() == EntryFlight.State.BOOKING && flight.getRestCabin() > 0;
    }
}
