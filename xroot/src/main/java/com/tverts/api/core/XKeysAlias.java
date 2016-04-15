package com.tverts.api.core;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * When integrating, this annotation is set
 * on the classes that share the same x-key
 * pairs as the master (value) class set.
 */
@Target(TYPE) @Retention(RUNTIME)
public @interface XKeysAlias
{
	/**
	 * Defines the master class that originates
	 * the x-key pairs.
	 */
	Class value();
}