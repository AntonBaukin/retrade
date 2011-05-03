package com.tverts.support.logic;

/**
 * Predicate takes context object as the input
 * and returns boolean result of it's evaluation.
 *
 * The meaning of the context objects fully
 * depends on the area whete the predicate is used.
 *
 * @author anton.baukin@gmail.com
 */
public interface Predicate
{
	/* public: Predicate interface */

	public boolean evalPredicate(Object ctx);
}