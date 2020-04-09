package net.kaaass.kflight.data;

import lombok.Data;
import lombok.Getter;
import lombok.Synchronized;
import net.kaaass.kflight.data.entry.EntryCity;
import net.kaaass.kflight.data.entry.EntryFlight;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
     * 城市时间索引
     */
    @Data
    public static final class CityTimeIndex {
        final String cityName;
        final LocalDateTime time;

        Hash toHash() {
            return new Hash(cityName.hashCode(), time.toEpochSecond(ZoneOffset.UTC));
        }

        /**
         * 索引哈希
         */
        @Data
        public static final class Hash implements Comparable<Hash> {
            final int nameHash;
            final Long dateTime;

            @Override
            public int compareTo(Hash o) {
                if (o == null)
                    throw new NullPointerException();
                int cmp = Integer.compare(nameHash, o.nameHash);
                if (cmp != 0) {
                    cmp = Long.compare(dateTime, o.dateTime);
                }
                return cmp;
            }
        }
    }

    /**
     * 起飞地索引
     */
    @Getter
    private Index<EntryFlight, CityTimeIndex, CityTimeIndex.Hash> indexFromTime;

    /**
     * 降落地索引
     */
    @Getter
    private Index<EntryFlight, CityTimeIndex, CityTimeIndex.Hash> indexToTime;

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
        // 城市&起飞时间索引
        indexFromTime = new Index<>(flight -> new CityTimeIndex(flight.getFrom().getName(), flight.getDepartureTime()),
                CityTimeIndex::toHash, Comparator.naturalOrder());
        indexToTime = new Index<>(flight -> new CityTimeIndex(flight.getTo().getName(), flight.getDepartureTime()),
                CityTimeIndex::toHash, Comparator.naturalOrder());
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
        INSTANCE.indexFromTime.addIndexFor(entryFlight);
        INSTANCE.indexToTime.addIndexFor(entryFlight);
        INSTANCE.indexFromTo.addIndexFor(entryFlight);
        // 计算城市平均票价
        var from = entryFlight.getFrom();
        var avg = from.getAvgCnt();
        var price = from.getAvgPrice();
        from.setAvgPrice(price * avg / (avg + 1) + entryFlight.getTicketPrice() / avg);
        var to = entryFlight.getTo();
        avg = to.getAvgCnt();
        price = to.getAvgPrice();
        to.setAvgPrice(price * avg / (avg + 1) + entryFlight.getTicketPrice() / avg);
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
        INSTANCE.indexFromTime.removeIndexFor(entryFlight);
        INSTANCE.indexToTime.removeIndexFor(entryFlight);
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

    /**
     * 查找起飞地点确定、起飞时间在某日期的航班
     */
    public static List<EntryFlight> findByFromAndDate(EntryCity city, LocalDate date) {
        var start = new CityTimeIndex(city.getName(), date.atStartOfDay());
        var end = new CityTimeIndex(city.getName(), date.plusDays(1).atStartOfDay());
        return INSTANCE.indexFromTime.findBetween(start, end);
    }

    /**
     * 查找着陆地点确定、起飞时间在某日期的航班
     */
    public static List<EntryFlight> findByToAndDate(EntryCity city, LocalDate date) {
        var start = new CityTimeIndex(city.getName(), date.atStartOfDay());
        var end = new CityTimeIndex(city.getName(), date.plusDays(1).atStartOfDay());
        return INSTANCE.indexToTime.findBetween(start, end);
    }

    /**
     * 清空航班数据
     */
    public static void clear() {
        INSTANCE.data.clear();
        INSTANCE.indexFlightNo.clear();
        INSTANCE.indexAirlineName.clear();
        INSTANCE.indexDepartureTime.clear();
        INSTANCE.indexLandingTime.clear();
        INSTANCE.indexFromTime.clear();
        INSTANCE.indexToTime.clear();
        INSTANCE.indexFromTo.clear();
    }

    public static FlightManager getInstance() {
        return INSTANCE;
    }
}
