package com.tverts.actions;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericIdentity;

/* com.tverts: predicates */

import com.tverts.support.logic.Predicate;


/**
 * This interface defines a strategy of creating
 * instances as a callback within the actions.
 * Without this strategy action builders must
 * create the instance when placing an action
 * to the queue.
 *
 * Note that each instance of this strategy
 * MUST create ONLY ONE resulting instance,
 * on the first request, and on the following
 * requests return it again.
 *
 * This interface extends {@link Predicate}
 * interface to tell whether the instance was
 * actually created. It also must answer
 * whether to create the instance before the
 * call to {@link #createInstance(Action)}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface DelayedInstance extends Predicate
{
	/* public: DelayedInstance interface */

	public NumericIdentity createInstance(Action action);

	/**
	 * This call must return the instance created, or
	 * the instance existing, or {@code null} when
	 * the instance must not be created.
	 */
	public NumericIdentity getInstance();
}
