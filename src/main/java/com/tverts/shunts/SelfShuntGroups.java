package com.tverts.shunts;

/* standard Java classes */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the groups this shunt unit belongs to.
 * The default group named 'default' or '' (empty)
 * is for the shunt units without this annotation.
 *
 * The annotation has no default value.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
@Target(TYPE) @Retention(RUNTIME)
public @interface SelfShuntGroups
{
	String[] groups();
}