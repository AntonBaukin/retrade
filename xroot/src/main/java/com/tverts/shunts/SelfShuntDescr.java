package com.tverts.shunts;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Additional annotation to {@link SelfShuntUnit}.
 * Defines the description of the shunt unit.
 *
 * @author anton.baukin@gmail.com
 */
@Target(TYPE) @Retention(RUNTIME)
public @interface SelfShuntDescr
{
	/**
	 * English description text.
	 */
	String value();

	/**
	 * Localized description text.
	 */
	String lo() default "";
}