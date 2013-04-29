package com.tverts.objects;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Property;


/**
 * Marks parameter to expose as {@link Property}.
 * To combine with {@link Param} annotation.
 *
 * @author anton.baukin@gmail.com
 */
@Target(METHOD) @Retention(RUNTIME)
public @interface Prop
{
	/**
	 * The name of the property. Defaults to
	 * the property name (not thw parameter!).
	 */
	String  name() default "";

	/**
	 * Optional parameter area name.
	 */
	String  area() default "";
}