package com.tverts.shunts;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a method of {@link SelfShunt} to run when
 * the test instance is invoked.
 *
 * @author anton.baukin@gmail.com
 */
@Target(METHOD) @Retention(RUNTIME)
public @interface SelfShuntMethod
{
	/**
	 * Defines the name of the shunt method.
	 * By default is equals to the name of the method.
	 */
	String  name()        default "";

	/**
	 * Tells that this method is critical.
	 *
	 * If an exception is thrown out of a critical
	 * method, shunt units invocation is cancelled,
	 * ant whole the shunting is cancelled.
	 *
	 * By default the methods are not critical.
	 * Try to set it {@code true} only when the
	 * whole system, or the application's logic
	 * are depend on the components tested here.
	 */
	boolean critical()    default false;

	/**
	 * Tells to run this method in the subclasses.
	 * By default is set to {@code false}.
	 */
	boolean inherit()     default false;

	/**
	 * Defines the order in which the methods are
	 * invoked. The scope of the order is within
	 * the declaring class.
	 *
	 * Inherited and not overwritten methods of the
	 * superclass are invoked (in the order of
	 * inheritance) before the methods of the
	 * subclasses.
	 *
	 * Note that the method without this parameter
	 * set explicitly, defined within the same class
	 * are always executed after the ones having it set.
	 */
	int     order()       default Integer.MAX_VALUE;

	/**
	 * Optional English text denoting the purpose
	 * of the method.
	 */
	String  descrEn()     default "";

	/**
	 * Localized value of the description.
	 */
	String  descrLo()     default "";
}