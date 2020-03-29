package net.kaaass.kflight.data.entry;

import lombok.Data;
import lombok.Setter;

/**
 * 订票信息
 */
@Data
public class EntryTicketOrder {

    /**
     * 出票状态
     */
    public enum State {
        /**
         * 等待出票
         */
        QUEUED,

        /**
         * 出票成功
         */
        DONE
    }

    /**
     * 出票情况
     */
    @Setter
    private State state;

    /**
     * 对应航班
     */
    private EntryFlight flight;

    /**
     * 订票人手机号
     */
    private String phone;
}
