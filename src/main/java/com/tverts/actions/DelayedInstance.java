package com.tverts.actions;

import com.tverts.endure.NumericIdentity;

/**
 * This interface defines a strategy of creating
 * instances as a callback within the actions.
 * Without this strategy action builders must
 * create the instance when placing an action
 * to the queue.
 *
 * Note that each instance of this strategy
 * MUST create only one resulting instance,
 * on the first request, and on the following
 * ones return it again.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface DelayedInstance
{
	/* public: DelayedInstance interface */

	public NumericIdentity createInstance(Action action);
}
