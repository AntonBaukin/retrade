package com.tverts.api.core;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Tells to ignore this property during the in-depth comparing.
 * (Use this annotation on get-methods only.)
 */
@Target(METHOD) @Retention(RUNTIME)
public @interface Ignore
{}