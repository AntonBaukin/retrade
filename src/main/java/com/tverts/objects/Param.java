package com.tverts.objects;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks parameter to expose as {@link ObjectParam}.
 * Place this annotation on get-, or set-method,
 * or on any method: then it's write-read purpose
 * would be guessed by the parameters list (methods
 * like  T setX(T)  are also supported).
 *
 *
 * @author anton.baukin@gmail.com
 */
@Target(METHOD) @Retention(RUNTIME)
public @interface Param
{
	/**
	 * The name of the parameter. Defaults
	 * to the property name.
	 */
	String  name()       default "";

	String  descr()      default "";

	/**
	 * Explicitly forbids writes.
	 */
	boolean readonly()   default false;

	/**
	 * Explicitly forbids reads.
	 */
	boolean writeonly()  default false;

	/**
	 * Tells that this parameter is obligatory
	 * for object to work properly.
	 */
	boolean required()   default false;
}