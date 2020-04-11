package net.kaaass.kflight.service;

import lombok.Data;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import net.kaaass.kflight.KflightApplication;
import net.kaaass.kflight.data.Index;
import net.kaaass.kflight.data.entry.EntryCity;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.event.FlightDelayedEvent;
import net.kaaass.kflight.eventhandle.SubscribeEvent;
import net.kaaass.kflight.exception.NotFoundException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 航班数据管理
 */
@Slf4j
public class FlightService {

    private final static String SEPARATOR = ";";

    private static final FlightService INSTANCE;

    private List<EntryFlight> data;

    static {
        INSTANCE = new FlightService();
        KflightApplication.EVENT_BUS.register(INSTANCE);
    }

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

        static String nameFromTo(EntryCity from, EntryCity to) {
            return from.getName() + SEPARATOR + to.getName();
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
                if (cmp == 0) {
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
    private Index<EntryFlight, CityTimeIndex, CityTimeIndex.Hash> indexFromToTime;

    private FlightService() {
        data = new ArrayList<>();
        // 索引初始化
        indexFlightNo = new Index<>(EntryFlight::getFlightNo, String::hashCode, Comparator.naturalOrder());
        indexAirlineName = new Index<>(EntryFlight::getAirlineName, String::hashCode, Comparator.naturalOrder());
        indexDepartureTime = new Index<>(EntryFlight::getDepartureTime, time -> time.toEpochSecond(ZoneOffset.UTC), Comparator.<Long>naturalOrder());
        indexLandingTime = new Index<>(EntryFlight::getLandingTime, time -> time.toEpochSecond(ZoneOffset.UTC), Comparator.<Long>naturalOrder());
        // 城市&起飞时间索引
        indexFromTime = new Index<>(flight -> new CityTimeIndex(flight.getFrom().getName(), flight.getDepartureTime()),
                CityTimeIndex::toHash, CityTimeIndex.Hash::compareTo);
        indexToTime = new Index<>(flight -> new CityTimeIndex(flight.getTo().getName(), flight.getDepartureTime()),
                CityTimeIndex::toHash, CityTimeIndex.Hash::compareTo);
        indexFromToTime = new Index<>(flight ->
                new CityTimeIndex(CityTimeIndex.nameFromTo(flight.getFrom(), flight.getTo()),
                        flight.getDepartureTime()),
                CityTimeIndex::toHash, CityTimeIndex.Hash::compareTo);
    }

    /**
     * 添加 entry
     */
    @Synchronized
    public static void addEntry(EntryFlight entryFlight) {
        // 字段计算
        entryFlight.setID(INSTANCE.data.size());
        // 添加对象
        INSTANCE.data.add(entryFlight);
        // 增加索引
        INSTANCE.addIndexFor(entryFlight);
    }

    /**
     * 删除 entry
     */
    @Synchronized
    public static void removeEntry(EntryFlight entryFlight) {
        INSTANCE.data.remove(entryFlight);
        INSTANCE.reId();
        INSTANCE.removeIndexFor(entryFlight);
    }

    /**
     * 根据航班号查找航班
     */
    public static Optional<EntryFlight> findByFlightNo(String flightNo) {
        return INSTANCE.indexFlightNo.findOne(flightNo);
    }

    /**
     * 根据起降地与日期寻找航班
     */
    public static List<EntryFlight> findAllByFromToAndDate(EntryCity from, EntryCity to, LocalDate date) {
        var start = new CityTimeIndex(CityTimeIndex.nameFromTo(from, to),
                date.atStartOfDay());
        var end = new CityTimeIndex(CityTimeIndex.nameFromTo(from, to),
                date.plusDays(1).atStartOfDay());
        return INSTANCE.indexFromToTime
                .findBetween(start, end)
                .parallelStream()
                .filter(flight -> flight.getFrom().equals(from) && flight.getTo().equals(to))
                .collect(Collectors.toList());
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
        return INSTANCE.indexFromTime
                .findBetween(start, end)
                .parallelStream()
                .filter(flight -> flight.getFrom().equals(city))
                .collect(Collectors.toList());
    }

    /**
     * 查找着陆地点确定、起飞时间在某日期的航班
     */
    public static List<EntryFlight> findByToAndDate(EntryCity city, LocalDate date) {
        var start = new CityTimeIndex(city.getName(), date.atStartOfDay());
        var end = new CityTimeIndex(city.getName(), date.plusDays(1).atStartOfDay());
        return INSTANCE.indexToTime
                .findBetween(start, end)
                .parallelStream()
                .filter(flight -> flight.getTo().equals(city))
                .collect(Collectors.toList());
    }

    /**
     * 获得所有航班信息
     */
    public static List<EntryFlight> getAll() {
        return INSTANCE.data;  // Bad, better use Collections::unmodifiedList
    }

    /**
     * 更新
     */
    public static void updateById(int id, EntryFlight newFlight) throws NotFoundException {
        var old = getById(id)
                .orElseThrow(() -> new NotFoundException("未找到此ID的航班信息！"));
        INSTANCE.removeIndexFor(old);
        // 重建索引
        INSTANCE.data.set(id, newFlight);
        INSTANCE.addIndexFor(newFlight);
    }

    /**
     * 由 ID 获取航班
     */
    public static Optional<EntryFlight> getById(int id) {
        var data = INSTANCE.data;
        if (id < 0 || id >= data.size())
            return Optional.empty();
        return Optional.of(INSTANCE.data.get(id));
    }

    /**
     * 重新编码航班记录 ID
     */
    private void reId() {
        for (int i = 0; i < data.size(); i++) {
            var cur = data.get(i);
            if (cur != null) {
                cur.setID(i);
            }
        }
    }

    /**
     * 增加索引并计算平均票价
     */
    private void addIndexFor(EntryFlight entryFlight) {
        // 字段计算
        entryFlight.setFlightTime(Duration.between(
                entryFlight.getDepartureTime(), entryFlight.getLandingTime()).toSeconds());
        // 增加索引
        INSTANCE.indexFlightNo.addIndexFor(entryFlight);
        INSTANCE.indexAirlineName.addIndexFor(entryFlight);
        INSTANCE.indexDepartureTime.addIndexFor(entryFlight);
        INSTANCE.indexLandingTime.addIndexFor(entryFlight);
        INSTANCE.indexFromTime.addIndexFor(entryFlight);
        INSTANCE.indexToTime.addIndexFor(entryFlight);
        INSTANCE.indexFromToTime.addIndexFor(entryFlight);
        // 计算城市平均票价
        var from = entryFlight.getFrom();
        var avg = from.getAvgCnt();
        var price = from.getAvgPrice();
        from.setAvgPrice(price * avg / (avg + 1) + entryFlight.getTicketPrice() / (avg + 1));
        from.setAvgCnt(avg + 1);
        var to = entryFlight.getTo();
        avg = to.getAvgCnt();
        price = to.getAvgPrice();
        to.setAvgPrice(price * avg / (avg + 1) + entryFlight.getTicketPrice() / (avg + 1));
        to.setAvgCnt(avg + 1);
    }

    /**
     * 查找同起降地未延期的最近航班
     */
    public static Optional<EntryFlight> searchClosetFlight(EntryFlight flight) {
        var from = flight.getFrom();
        var to = flight.getTo();

        var startHash = new CityTimeIndex.Hash(CityTimeIndex.nameFromTo(from, to).hashCode(),
                flight.getDepartureTime().toEpochSecond(ZoneOffset.UTC));
        var endHash = new CityTimeIndex.Hash(CityTimeIndex.nameFromTo(from, to).hashCode(),
                Long.MAX_VALUE);
        var fromTos = INSTANCE.indexFromToTime.findBetweenHash(startHash, endHash);

        return fromTos.stream()
                .filter(aFlight ->
                        aFlight.getState() == EntryFlight.State.BOOKING &&
                                aFlight.getFrom().equals(from) &&
                                aFlight.getTo().equals(to))
                .findFirst();
    }

    /**
     * 通过 ID 变更航班状态
     *
     * @return 若航延，返回推荐航班
     */
    public static EntryFlight changeState(int id,
                                          EntryFlight.State state,
                                          LocalDateTime delayTo) throws NotFoundException {
        var flight = getById(id)
                .orElseThrow(() -> new NotFoundException("未找到此ID的航班信息！"));
        flight.setState(state);
        if (state == EntryFlight.State.CANCELED || state == EntryFlight.State.DELAYED) {
            var recommend = searchClosetFlight(flight);
            if (delayTo != null)
                flight.setDepartureTime(delayTo);
            KflightApplication.EVENT_BUS.post(new FlightDelayedEvent(flight, recommend));
            return recommend.orElse(null);
        }
        return null;
    }

    @SubscribeEvent
    public void onFlightDelayed(FlightDelayedEvent event) {
        var delayed = event.getDelayed();
        var recommend = event.getRecommend();

        // 模拟发短信
        delayed.getTickets()
                .forEach(order ->
                        log.info("【短信发送】尊敬的 {}，非常抱歉的通知，您的乘坐的航班 {} 已经 {}。为您推荐就近的航班 {}。",
                                order.getPhone(),
                                delayed.getFlightNo(),
                                delayed.getState() == EntryFlight.State.DELAYED ? "延误" : "取消",
                                recommend.map(EntryFlight::getFlightNo)
                                        .orElse("[无就近航班]")));
    }

    /**
     * 删除索引
     */
    private void removeIndexFor(EntryFlight entryFlight) {
        INSTANCE.indexFlightNo.removeIndexFor(entryFlight);
        INSTANCE.indexAirlineName.removeIndexFor(entryFlight);
        INSTANCE.indexDepartureTime.removeIndexFor(entryFlight);
        INSTANCE.indexLandingTime.removeIndexFor(entryFlight);
        INSTANCE.indexFromTime.removeIndexFor(entryFlight);
        INSTANCE.indexToTime.removeIndexFor(entryFlight);
        INSTANCE.indexFromToTime.removeIndexFor(entryFlight);
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
        INSTANCE.indexFromToTime.clear();
    }

    public static FlightService getInstance() {
        return INSTANCE;
    }
}
