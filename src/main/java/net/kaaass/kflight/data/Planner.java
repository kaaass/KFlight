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
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 航线规划类
 */
public class Planner {

    public final static int DEFAULT_SEARCH_LIMIT = 50;

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
        int limit = DEFAULT_SEARCH_LIMIT;
        var result = new ArrayList<FlightPlan>();
        // 直达查找
        FlightManager.findAllByFromToAndDate(from, to, date)
                .parallelStream()
                .map(Planner::planFor)
                .forEach(result::add);
        // 转机查找
        limit = searchGapOne(result, from, to, date, limit);
        searchGapMulti(result, from, to, date, limit);
        planSort(result);
        return result;
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
        var tos = FlightManager.findByToAndDate(to, date);
        // 提取中转城市集合
        var tosCitySet = tos.parallelStream()
                .map(EntryFlight::getFrom) // 到达航班的出发
                .collect(HashSet<EntryCity>::new, HashSet::insert, HashSet::addAll);
        var tosCity = tosCitySet.toList();
        // 对中转城市集合进行排序，计算和
        Sorter.sort(tosCity, (o1, o2) -> Float.compare(o1.getAvgPrice(), o2.getAvgPrice()));
        var sum = tosCity.stream()
                .reduce(0F, (acc, city) -> acc + 1e6f / city.getAvgPrice(), Float::sum);
        // Random walk 选择
        var rand = new Random();
        var ret = new ArrayList<FlightPlan>();
        while (searchLimit > 0 && !tosCity.isEmpty()) {  // 防止精度问题
            searchLimit--;
            var t = rand.nextFloat() * sum;
            EntryCity mid = null;
            for (var city : tosCity) {
                t -= 1e6f / city.getAvgPrice();
                if (t < 1) {  // 进行选择
                    mid = city;
                    sum -= city.getAvgPrice();
                    break;
                }
            }
            if (mid == null)
                mid = tosCity.get(0);
            // 删除城市防止重复
            tosCity.remove(mid);
            sum -= 1e6f / mid.getAvgPrice();
            // 对该城市计算 One gap
            ret.clear();
            searchLimit = searchGapOne(ret, from, mid, date, searchLimit);
            // 取所有 mid -> to 的航班
            var finalMid = mid;
            var midTos = tos.stream()
                    .filter(flight -> flight.getFrom().equals(finalMid))
                    .filter(Planner::couldSellTicket)
                    .collect(Collectors.toList());
            // 筛选，并加入最终结果
            var limit = new AtomicInteger(searchLimit);
            planConcatFlight(ret, midTos, limit).forEach(result::add);
            searchLimit = limit.get();
        }
        // 多层级递归搜索
        var limit = new AtomicInteger(searchLimit);
        tosCitySet.forEach(mid -> {
            if (limit.get() > 0) {
                ret.clear();
                limit.set(searchGapMulti(ret, from, mid, date, limit.get()));
                if (!ret.isEmpty()) {
                    // 取所有 mid -> to 的航班
                    var midTos = tos.stream()
                            .filter(flight -> flight.getFrom().equals(mid))
                            .filter(Planner::couldSellTicket)
                            .collect(Collectors.toList());
                    // 筛选，并加入最终结果
                    planConcatFlight(ret, midTos, limit).forEach(result::add);
                }
            }
        });
        return limit.get();
    }

    /**
     * 将若干转机计划与航班拼接
     */
    private static Stream<FlightPlan> planConcatFlight(List<FlightPlan> plans, List<EntryFlight> midTos, AtomicInteger limit) {
        return plans.parallelStream().flatMap(plan -> limit.get() <= 0 ? Stream.empty() : // 确保搜索未超界限
                midTos.parallelStream()
                        .filter(nxtFlight -> {  // 使用 flatMap 嵌套模拟笛卡尔积
                            // 参数：plan、toFlight
                            // 目的：筛选出符合转机要求的航班
                            if (limit.get() <= 0)
                                return false;
                            limit.decrementAndGet();
                            // 航班间隔必须大于等于 40 分钟
                            var dur = Duration.between(
                                    plan.flights.get(plan.flights.size() - 1).getLandingTime(),
                                    nxtFlight.getDepartureTime());
                            if (dur.toMinutes() < 40)
                                return false;
                            // 其余情况则符合要求
                            return true;
                        })
                        .map(nxtFlight -> {  // 包装为航班计划
                            float totalCost = plan.totalCost
                                    + nxtFlight.getTicketPrice();
                            long totalTime =
                                    Duration.between(plan.flights.get(0).getDepartureTime(),
                                            nxtFlight.getLandingTime()).toSeconds();
                            var flights = new ArrayList<>(plan.flights);
                            flights.add(nxtFlight);
                            return new FlightPlan(totalCost, totalTime, flights);
                        })
        );
    }

    /**
     * 对转机计划进行排序
     */
    static void planSort(List<FlightPlan> plan) {
        var cost = plan.parallelStream()
                .reduce(0F, (acc, p) -> acc + p.totalCost, Float::sum);
        var time = plan.parallelStream()
                .reduce(0F, (acc, p) -> acc + p.totalTime, Float::sum);
        Sorter.sort(plan, (o1, o2) -> {
            int cmp = Integer.compare(o1.flights.size(), o2.flights.size());
            // 优先取转机次数少的
            if (cmp == 0) {
                var val1 = o1.totalCost / cost + o1.totalTime / time;
                var val2 = o1.totalCost / cost + o1.totalTime / time;
                cmp = Float.compare(val1, val2);
            }
            return cmp;
        });
    }

    /**
     * 为单航班创建转机计划
     */
    private static FlightPlan planFor(EntryFlight flight) {
        return new FlightPlan(flight.getTicketPrice(), flight.getFlightTime(),
                new ArrayList<>() {{
                    add(flight);
                }});
    }

    /**
     * 谓词：航班是否可销售并且有余票
     */
    private static boolean couldSellTicket(EntryFlight flight) {
        return flight.getState() == EntryFlight.State.BOOKING && flight.getRestCabin() > 0;
    }
}
