package com.tverts.genesis;

/* standard Java classes */

import java.util.Random;
import java.util.Set;


/**
 * The context of generation call.
 *
 * @author anton.baukin@gmail.com
 */
public interface GenCtx
{
	/* public: GenCtx interface */

	/**
	 * Returns the Genesis unit that had created
	 * this context instance.
	 */
	public Genesis getOwner();

	public GenCtx  getOuter();

	/**
	 * The random numbers generator that may
	 * be applied by the generator.
	 */
	public Random  gen();

	/**
	 * Duplicates this context replacing
	 * the outer context with {@code this}
	 * and resetting the owner.
	 *
	 * Note that the new context shares
	 * the same parameters set and the
	 * generator {@link #gen()}.
	 */
	public GenCtx  stack(Genesis owner);

	/**
	 * Returns the names of all the parameters.
	 */
	public Set     params();

	/**
	 * Returns the context parameter.
	 */
	public Object  get(Object p);

	/**
	 * Rewrites the context parameter with
	 * the new value returning the old one.
	 */
	public Object  set(Object p, Object v);

	/**
	 * Returns the default logging destination
	 * that generator may use.
	 *
	 * Note that the result is always defined.
	 */
	public String  log();
}