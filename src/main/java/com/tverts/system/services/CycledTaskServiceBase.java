package com.tverts.system.services;

/**
 * As it's parent {@link SingleTaskServiceBase},
 * this service runs only one thread. But the same
 * task object is invoked infinitely in the cycle
 * until the service is stopped.
 *
 * If the task implements {@link BreakingTask}
 * interface it may define it's own life time.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class CycledTaskServiceBase
       extends        SingleTaskServiceBase
{
	/* public: service settings */

	public long getCyclePause()
	{
		return cyclePause;
	}

	public void setCyclePause(long cyclePause)
	{
		this.cyclePause = cyclePause;
	}

	/* protected: SingleTaskService (service task handling) */

	protected Runnable wrapTask(Runnable task)
	{
		return new CycledTaskWrapper(task);
	}

	/* protected: cycled task wrapper */

	protected class   CycledTaskWrapper
	          extends TaskWrapper
	{
		/* public: constructor */

		public CycledTaskWrapper(Runnable task)
		{
			super(task);
		}

		/* public: Runnable interface */

		public void run()
		{
			if(!openTaskWrapped())
			{
				closeTaskWrapped(false);
				return;
			}

			//~: execute the task until the service is stopped
			while(!CycledTaskServiceBase.this.breaked) try
			{
				if(cycleBody())
					break;
			}
			catch(Throwable e)
			{
				try
				{
					handleCycleError(e);
				}
				catch(Throwable ee)
				{
					setWrappedError(ee);
				}

				//?: {has error} exit the cycle
				if(getWrappedError() != null)
					break;
			}

			//!: exit the task (thread)
			try
			{
				closeTaskWrapped(true);
			}
			catch(Throwable e)
			{
				if(getWrappedError() == null)
					setWrappedError(e);
			}
		}

		/* protected: task execution */

		/**
		 * Invokes the task wrapped. Return {@code true}
		 * to break the cycle. The possible exceptions
		 * are handled outside.
		 */
		protected boolean cycleBody()
		  throws Throwable
		{
			BreakingTask breaker = unwrapBreakingTask(this);

			//?: {the task had breaked itself} exit the cycle
			if((breaker != null) && breaker.isTaskBreaked())
				return true;

			//!: execute the task
			getWrappedTask().run();

			//~: search the breaker again in case of mutating structure
			breaker = unwrapBreakingTask(this);
			if((breaker != null) && breaker.isTaskBreaked())
				return true;

			//do the pause, break when interrupted
			return !cycleTaskPause();
		}

		protected void    handleCycleError(Throwable e)
		{
			CycledTaskServiceBase.this.handleCycleError(this, e);
		}

		/**
		 * Pause made after each cycle of the task.
		 * Returns {@code false} to exit the task activity.
		 */
		protected boolean cycleTaskPause()
		{
			if(getCyclePause() > 0L) try
			{
				synchronized(pauseWaitee())
				{
					pauseWaitee().wait(getCyclePause());
				}
			}
			catch(InterruptedException e)
			{
				return false;
			}

			return true;
		}
	}

	/* protected: wrapped task execution */

	protected void handleCycleError (
	                 CycledTaskWrapper task,
	                 Throwable         error
	               )
	{
		task.setWrappedError(error);
	}

	/* protected: service settings */

	private long cyclePause;
}