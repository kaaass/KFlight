package net.kaaass.kflight.data.entry;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import net.kaaass.kflight.util.EntryCityDeserializer;
import net.kaaass.kflight.util.EntryCitySerializer;
import net.kaaass.kflight.util.LocalDateTimeDeserializer;
import net.kaaass.kflight.util.LocalDateTimeSerializer;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
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

    @Setter
    @Nullable
    Integer ID = null;

    /**
     * 航班状态
     */
    State state = State.BOOKING;

    /**
     * 航班号
     */
    String flightNo;

    /**
     * 航空公司名称
     */
    String airlineName;

    /**
     * 起飞时间
     */
    @Setter
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime departureTime;

    /**
     * 降落时间
     */
    @Setter
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime landingTime;

    /**
     * 飞行时间
     */
    @Setter
    long flightTime = 0;

    /**
     * 起飞城市
     */
    @JsonSerialize(using = EntryCitySerializer.class)
    @JsonDeserialize(using = EntryCityDeserializer.class)
    EntryCity from;

    /**
     * 目的城市
     */
    @JsonSerialize(using = EntryCitySerializer.class)
    @JsonDeserialize(using = EntryCityDeserializer.class)
    EntryCity to;

    /**
     * 经停城市，可能为空
     */
    @JsonSerialize(using = EntryCitySerializer.class)
    @JsonDeserialize(using = EntryCityDeserializer.class)
    EntryCity middle;

    /**
     * 经停等待时间，以秒计算
     */
    @Setter
    long middleTime;

    /**
     * 客舱总数（总可售票数）
     */
    @Setter
    int totalCabin;

    /**
     * 剩余票数
     */
    @Setter
    int restCabin;

    /**
     * 当前票价
     */
    @Setter
    float ticketPrice;

    /**
     * 当前有效票
     */
    @JsonManagedReference
    List<EntryTicketOrder> tickets = new ArrayList<>();
}
