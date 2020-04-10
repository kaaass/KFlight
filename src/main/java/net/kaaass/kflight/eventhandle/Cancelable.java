package net.kaaass.kflight.eventhandle;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Modified from forge
 *
 * @author cpw
 */
@Retention(value = RUNTIME)
@Target(value = TYPE)
public @interface Cancelable {
}