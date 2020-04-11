package net.kaaass.kflight.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.eventhandle.Event;
import net.kaaass.kflight.eventhandle.ListenerList;

import java.util.Optional;

/**
 * 退票事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FlightDelayedEvent extends Event {

    EntryFlight delayed;

    Optional<EntryFlight> recommend;
}
