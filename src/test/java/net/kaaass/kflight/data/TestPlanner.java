package net.kaaass.kflight.data;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * 索引的单元测试
 */

public class TestPlanner {

    @Before
    public void loadData() throws IOException {
        FlightManager.clear();
        DataLoader.loadFlightFromJsonResource("/flights.json");
        System.out.println("succ: flight data loaded.");
    }

    /**
     * 由航班号生成转机计划
     */
    private Planner.FlightPlan planOf(String... flightNos) {
        var flights = Stream.of(flightNos)
                .map(FlightManager::findByFlightNo)
                .map(Optional::orElseThrow)
                .collect(Collectors.toList());
        float totalCost = flights.stream()
                .reduce(0F, (acc, flight) -> acc + flight.getTicketPrice(), Float::sum);
        long totalTime =
                Duration.between(flights.get(0).getDepartureTime(),
                        flights.get(flights.size() - 1).getLandingTime()).toSeconds();
        return new Planner.FlightPlan(totalCost, totalTime, flights);
    }

    /**
     * 测试单城市间隔转机
     */
    @Test
    public void testSearchGapOne() {
        var from = CityManager.findByName("温州").orElseThrow();
        var to = CityManager.findByName("长春").orElseThrow();
        var result = new ArrayList<Planner.FlightPlan>();
        var limit = Planner.searchGapOne(result, from, to, LocalDate.of(2020, 4, 9), 100);
        System.out.println("Search limit used: " + (100 - limit));
        assertEquals(3, result.size()); // FIXME: 有一定概率缺少一班？？？
        assertTrue(result.contains(planOf("CA0809", "CA1014")));
        assertTrue(result.contains(planOf("AQ1013", "AQ1417")));
        assertTrue(result.contains(planOf("CA0910", "CA1519")));
    }

    /**
     * 测试多间隔转机
     */
    @Test
    public void testSearchGapMulti() {
        var from = CityManager.findByName("温州").orElseThrow();
        var to = CityManager.findByName("长春").orElseThrow();
        var result = new ArrayList<Planner.FlightPlan>();
        var limit = Planner.searchGapMulti(result, from, to, LocalDate.of(2020, 4, 9), 100);
        System.out.println("Search limit used: " + (100 - limit));
    }

    /**
     * 测试转机计划规划
     */
    @Test
    public void testPlan() {
        var from = CityManager.findByName("温州").orElseThrow();
        var to = CityManager.findByName("长春").orElseThrow();
        var date = LocalDate.of(2020, 4, 9);
        var result = Planner.plan(from, to, date);
        System.out.println(result);
    }
}
