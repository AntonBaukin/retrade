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
 * @author anton baukin (abaukin@mail.ru)
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
	 * method, shunt unit invocation is cancelled.
	 */
	boolean critical()    default true;

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