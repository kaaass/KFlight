package net.kaaass.kflight.data.entry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.kflight.util.EntryCityDeserializer;
import net.kaaass.kflight.util.EntryCitySerializer;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EntryFlight implements IEntry {

    /**
     * 航班状态
     */
    public enum State {

        /**
         * 接受机票预定
         */
        BOOKING,

        /**
         * 关闭售票，准备起飞
         * <p>
         * 可以转移至DONE、DELAYED、CANCELED
         */
        PREPARE,

        /**
         * 正常起飞
         * <p>
         * 允许将航班移出系统
         */
        DONE,

        /**
         * 航班延误
         */
        DELAYED,

        /**
         * 航班取消
         */
        CANCELED
    }

    /**
     * 航班状态
     */
    State state = State.BOOKING;

    /**
     * 航班号
     */
    final String flightNo;

    /**
     * 航空公司名称
     */
    final String airlineName;

    /**
     * 起飞时间
     */
    final LocalDateTime departureTime;

    /**
     * 降落时间
     */
    final LocalDateTime landingTime;

    /**
     * 飞行时间
     */
    long flightTime;

    /**
     * 起飞城市
     */
    @JsonSerialize(using = EntryCitySerializer.class)
    @JsonDeserialize(using = EntryCityDeserializer.class)
    final EntryCity from;

    /**
     * 目的城市
     */
    @JsonSerialize(using = EntryCitySerializer.class)
    @JsonDeserialize(using = EntryCityDeserializer.class)
    final EntryCity to;

    /**
     * 经停城市，可能为空
     */
    @JsonSerialize(using = EntryCitySerializer.class)
    @JsonDeserialize(using = EntryCityDeserializer.class)
    EntryCity middle;

    /**
     * 经停等待时间，以秒计算
     */
    long middleTime;

    /**
     * 客舱总数（总可售票数）
     */
    int totalCabin;

    /**
     * 剩余票数
     */
    int restCabin;

    /**
     * 当前票价
     */
    float ticketPrice;

    /**
     * 当前有效票
     */
    final List<EntryTicketOrder> tickets;
}
