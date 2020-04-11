package net.kaaass.kflight.eventhandle;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Base Event class that all other events are derived from
 * <p>
 * Modified from forge
 *
 * @author cpw
 */
public class Event {
    @Retention(value = RUNTIME)
    @Target(value = TYPE)
    public @interface HasResult {
    }

    public enum Result {
        DENY,
        DEFAULT,
        ALLOW
    }

    private boolean isCanceled = false;
    private Result result = Result.DEFAULT;
    private static ListenerList listeners = new ListenerList();
    private EventPriority phase = null;

    public Event() {
        setup();
    }

    /**
     * Determine if this function is cancelable at all.
     *
     * @return If access to setCanceled should be allowed
     * <p>
     * Note:
     * Events with the Cancelable annotation will have this method automatically added to return true.
     */
    public boolean isCancelable() {
        for (var annotation : this.getClass().getAnnotations()) {
            if (Cancelable.class.isAssignableFrom(annotation.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if this event is canceled and should stop executing.
     *
     * @return The current canceled state
     */
    public boolean isCanceled() {
        return isCanceled;
    }

    /**
     * Sets the state of this event, not all events are cancelable, and any attempt to
     * cancel a event that can't be will result in a IllegalArgumentException.
     * <p>
     * The functionality of setting the canceled state is defined on a per-event bases.
     *
     * @param cancel The new canceled value
     */
    public void setCanceled(boolean cancel) {
        if (!isCancelable()) {
            throw new IllegalArgumentException("Attempted to cancel a uncancelable event");
        }
        isCanceled = cancel;
    }

    /**
     * Determines if this event expects a significant result value.
     * <p>
     * Note:
     * Events with the HasResult annotation will have this method automatically added to return true.
     */
    public boolean hasResult() {
        return false;
    }

    /**
     * Returns the value set as the result of this event
     */
    public Result getResult() {
        return result;
    }

    /**
     * Sets the result value for this event, not all events can have a result set, and any attempt to
     * set a result for a event that isn't expecting it will result in a IllegalArgumentException.
     * <p>
     * The functionality of setting the result is defined on a per-event bases.
     *
     * @param value The new result
     */
    public void setResult(Result value) {
        result = value;
    }

    /**
     * Called by the base constructor, this is used by ASM generated
     * event classes to setup various functionality such as the listenerlist.
     */
    protected void setup() {
    }

    /**
     * Returns a ListenerList object that contains all listeners
     * that are registered to this event.
     *
     * @return Listener List
     */
    public ListenerList getListenerList() {
        return listeners;
    }

    public EventPriority getPhase() {
        return this.phase;
    }

    public void setPhase(EventPriority value) {
        // Preconditions.checkArgument(value != null, "setPhase argument must not be null");
        int prev = phase == null ? -1 : phase.ordinal();
        // Preconditions.checkArgument(prev < value.ordinal(), "Attempted to set event phase to %s when already %s", value, phase);
        phase = value;
    }
}