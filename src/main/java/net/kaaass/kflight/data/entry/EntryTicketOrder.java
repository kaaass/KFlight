package net.kaaass.kflight.data.entry;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.springframework.lang.Nullable;

/**
 * 订票信息
 */
@Data
@RequiredArgsConstructor
@ToString(exclude = {"flight"})
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
     * 票号
     */
    @Setter
    @Nullable
    Integer ID = null;

    /**
     * 出票情况
     */
    @Setter
    private State state = State.QUEUED;

    /**
     * 对应航班
     */
    @NonNull
    @JsonBackReference
    private EntryFlight flight;

    /**
     * 订票人手机号
     */
    @NonNull
    private String phone;
}
