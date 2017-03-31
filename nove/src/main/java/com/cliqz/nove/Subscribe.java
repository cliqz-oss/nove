package com.cliqz.nove;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An marker annotation used to generate messages dispatchers
 *
 * @author Stefano Pacifici
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Subscribe {
}
