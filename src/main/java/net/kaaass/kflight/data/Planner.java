package net.kaaass.kflight.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kaaass.kflight.data.algorithm.SetIntersect;
import net.kaaass.kflight.data.entry.EntryCity;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.data.structure.HashSet;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

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
        // TODO
        return searchLimit;
    }

    /**
     * 寻找两个转机多次的方案
     */
    static int searchGapMulti(List<FlightPlan> result, EntryCity from, EntryCity to, LocalDate date, int searchLimit) {
        var froms = FlightManager.findByFromAndDate(from, date);
        var tos = FlightManager.findByToAndDate(to, date);
        // 提取中转城市集合
        var fromsCity = froms.parallelStream()
                .map(flight -> flight.getTo().getName()) // 出发航班的目的
                .collect(HashSet::new, HashSet::insert, HashSet::addAll);
        var tosCity = tos.parallelStream()
                .map(flight -> flight.getFrom().getName()) // 到达航班的出发
                .collect(HashSet::new, HashSet::insert, HashSet::addAll);
        var midCity = SetIntersect.intersectList(fromsCity, tosCity);

        // TODO
        return searchLimit;
    }
}
