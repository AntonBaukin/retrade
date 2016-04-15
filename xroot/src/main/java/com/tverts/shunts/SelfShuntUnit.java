package com.tverts.shunts;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a {@link SelfShunt} class. Addition to the
 * basic implementation class {@link SelfShuntBase}.
 * Used in {@link SelfShuntBaseAnnotated}.
 *
 * @author anton.baukin@gmail.com
 */
@Target(TYPE) @Retention(RUNTIME)
public @interface SelfShuntUnit
{
	/**
	 * Optional property, the name of the
	 * self-shunting unit. Defaults to
	 * the simple name of the class.
	 */
	String  value()  default "";

	/**
	 * Tells that all the shunt methods of the unit
	 * are invoked on the same shunt instance. By
	 * default it is false, and each method works
	 * with it's own private instance.
	 */
	boolean single() default false;
}