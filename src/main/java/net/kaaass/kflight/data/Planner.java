package net.kaaass.kflight.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kaaass.kflight.data.entry.EntryCity;
import net.kaaass.kflight.data.entry.EntryFlight;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        // TODO
        return searchLimit;
    }
}
