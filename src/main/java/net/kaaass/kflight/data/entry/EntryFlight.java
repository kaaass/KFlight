package net.kaaass.kflight.data.entry;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Setter
public class EntryFlight {

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
    private State state = State.BOOKING;

    /**
     * 航班号
     */
    private String flightNo;

    /**
     * 航空公司名称
     */
    private String airlineName;

    /**
     * 起飞时间
     */
    private LocalDateTime departureTime;

    /**
     * 降落时间
     */
    private LocalDateTime landingTime;

    /**
     * 飞行时间
     */
    private long flightTime;

    /**
     * 起飞城市
     */
    private EntryCity from;

    /**
     * 目的城市
     */
    private EntryCity to;

    /**
     * 经停城市，可能为空
     */
    private EntryCity middle;

    /**
     * 经停等待时间，以秒计算
     */
    private long middleTime;

    /**
     * 客舱总数（总可售票数）
     */
    private int totalCabin;

    /**
     * 剩余票数
     */
    private int restCabin;

    /**
     * 当前票价
     */
    private float ticketPrice;
}
