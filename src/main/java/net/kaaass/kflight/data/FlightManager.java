package net.kaaass.kflight.data;

import lombok.Getter;
import lombok.Synchronized;
import net.kaaass.kflight.data.entry.EntryCity;
import net.kaaass.kflight.data.entry.EntryFlight;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 航班数据管理
 */
public class FlightManager {

    private final static String SEPARATOR = ";";

    private static final FlightManager INSTANCE = new FlightManager();

    private List<EntryFlight> data;

    /*
    索引
     */

    /**
     * 航班号索引
     */
    @Getter
    private Index<EntryFlight, String, Integer> indexFlightNo;

    /**
     * 航空公司索引
     */
    @Getter
    private Index<EntryFlight, String, Integer> indexAirlineName;

    /**
     * 起飞时间索引
     */
    @Getter
    private Index<EntryFlight, LocalDateTime, Long> indexDepartureTime;

    /**
     * 降落时间索引
     */
    @Getter
    private Index<EntryFlight, LocalDateTime, Long> indexLandingTime;

    /**
     * 起飞地索引
     */
    @Getter
    private Index<EntryFlight, String, Integer> indexFrom;

    /**
     * 降落地索引
     */
    @Getter
    private Index<EntryFlight, String, Integer> indexTo;

    /**
     * 起降地索引
     */
    @Getter
    private Index<EntryFlight, String, Integer> indexFromTo;

    private FlightManager() {
        data = new ArrayList<>();
        // 索引初始化
        indexFlightNo = new Index<>(EntryFlight::getFlightNo, String::hashCode, Comparator.naturalOrder());
        indexAirlineName = new Index<>(EntryFlight::getAirlineName, String::hashCode, Comparator.naturalOrder());
        indexDepartureTime = new Index<>(EntryFlight::getDepartureTime, time -> time.toEpochSecond(ZoneOffset.UTC), Comparator.<Long>naturalOrder());
        indexLandingTime = new Index<>(EntryFlight::getLandingTime, time -> time.toEpochSecond(ZoneOffset.UTC), Comparator.<Long>naturalOrder());
        indexFrom = new Index<>(flight -> flight.getFrom().getName(), String::hashCode, Comparator.naturalOrder());
        indexTo = new Index<>(flight -> flight.getTo().getName(), String::hashCode, Comparator.naturalOrder());
        indexFromTo = new Index<>(flight -> flight.getFrom().getName() + SEPARATOR
                + flight.getTo().getName(), String::hashCode, Comparator.naturalOrder());
    }

    /**
     * 添加 entry
     */
    @Synchronized
    public static void addEntry(EntryFlight entryFlight) {
        INSTANCE.data.add(entryFlight);
        INSTANCE.indexFlightNo.addIndexFor(entryFlight);
        INSTANCE.indexAirlineName.addIndexFor(entryFlight);
        INSTANCE.indexDepartureTime.addIndexFor(entryFlight);
        INSTANCE.indexLandingTime.addIndexFor(entryFlight);
        INSTANCE.indexFrom.addIndexFor(entryFlight);
        INSTANCE.indexTo.addIndexFor(entryFlight);
        INSTANCE.indexFromTo.addIndexFor(entryFlight);
    }

    /**
     * 删除 entry
     */
    @Synchronized
    public static void removeEntry(EntryFlight entryFlight) {
        INSTANCE.data.remove(entryFlight);
        INSTANCE.indexFlightNo.removeIndexFor(entryFlight);
        INSTANCE.indexAirlineName.removeIndexFor(entryFlight);
        INSTANCE.indexDepartureTime.removeIndexFor(entryFlight);
        INSTANCE.indexLandingTime.removeIndexFor(entryFlight);
        INSTANCE.indexFrom.removeIndexFor(entryFlight);
        INSTANCE.indexTo.removeIndexFor(entryFlight);
        INSTANCE.indexFromTo.removeIndexFor(entryFlight);
    }

    /**
     * 根据航班号查找航班
     */
    public static Optional<EntryFlight> findByFlightNo(String flightNo) {
        return INSTANCE.indexFlightNo.findOne(flightNo);
    }

    /**
     * 根据起降地寻找航班
     */
    public static List<EntryFlight> findAllByFromTo(EntryCity from, EntryCity to) {
        return INSTANCE.indexFromTo.findAll(from.getName() + SEPARATOR + to.getName());
    }

    /**
     * 查找起飞时间在范围内的航班
     */
    public static List<EntryFlight> findBetween(LocalDateTime start, LocalDateTime end) {
        return INSTANCE.indexDepartureTime.findBetween(start, end);
    }

    public static FlightManager getInstance() {
        return INSTANCE;
    }
}
