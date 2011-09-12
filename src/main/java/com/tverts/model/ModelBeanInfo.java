package com.tverts.model;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Main annotation describing model beans.
 *
 * @author anton.baukin@gmail.com
 */
@Target(TYPE) @Retention(RUNTIME)
public @interface ModelBeanInfo
{
	/**
	 * Tells that this bean must be stored in the model
	 * until explicitly removed.
	 */
	boolean crucial() default false;

	/**
	 * Defines that each model store may have only
	 * one instance of this model bean. Note that
	 * uniqueness is defined only by the bean key
	 * stored {@link #key()}.
	 */
	boolean unique()  default false;

	/**
	 * The key (name) of the model bean.
	 *
	 * For unique model beans defines the actual name
	 * to be assigned. If not set, is taken from the
	 * simple name of the implementation class. (Having
	 * first letter set to lower case if not all the
	 * key is in upper.)
	 *
	 * For non-unique beans tells the prefix of the
	 * serial keys generated. (The random tail of
	 * the key is separated by '_' character.)
	 */
	String  key();
}