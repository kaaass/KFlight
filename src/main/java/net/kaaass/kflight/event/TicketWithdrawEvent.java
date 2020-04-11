package net.kaaass.kflight.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.eventhandle.Event;

/**
 * 退票事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TicketWithdrawEvent extends Event {

    /**
     * 退票航班
     */
    EntryFlight flight;

    /**
     * 退票电话
     */
    String phone;
}
