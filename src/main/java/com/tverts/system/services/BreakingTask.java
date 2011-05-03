package com.tverts.system.services;

/**
 * Denotes a task that may say the external
 * execution routines (mostly, cycling) that
 * this task has nothing to do more.
 *
 * @author anton.baukin@gmail.com
 */
public interface BreakingTask extends Runnable
{
	/* public: BreakingTask interface */

	public boolean isTaskBreaked();
}