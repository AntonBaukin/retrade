package com.tverts.api.core;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * When integrating, a foreign key to an entity
 * has the value (Long) from ReTrade system,
 * and it is not known to an external system.
 *
 * External system do refer the entity by
 * it's own key, the x-keys pair.
 *
 * Integrating application links the pair.
 *
 * Mark with it a get-method of the ReTrade Long
 * primary key. The x-keys pair property of then
 * must start with 'X'. For example, getMeasure()
 * and getXMeasure().
 *
 * The x-key property must be string.
 */
@Target(METHOD) @Retention(RUNTIME)
public @interface XKeyPair
{
	/**
	 * Defines API type of the entity referred.
	 * Used by integrating applications to
	 * links the x-keys pair.
	 */
	Class type();
}