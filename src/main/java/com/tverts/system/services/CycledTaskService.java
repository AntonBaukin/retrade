package com.tverts.system.services;

/**
 * As it's parent {@link SingleTaskService}, this
 * service runs only one thread. But the same
 * task object is invoked infinitely in the cycle
 * until the service is stopped.
 *
 * If the task implements {@link BreakingTask}
 * interface it may define it's own life time.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   CycledTaskService
       extends SingleTaskService
{
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
			openTaskWrapped();

			//~: execute the task until the service is stopped
			while(!CycledTaskService.this.breaked) try
			{
				if(cycleBody())
					break;
			}
			catch(Throwable e)
			{
				handleCycleError(e);

				//?: {has error} exit the cycle
				if(getWrappedError() != null)
					break;
			}

			//!: exit the task (thread)
			try
			{
				closeTaskWrapped();
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

			return (breaker != null) && breaker.isTaskBreaked();
		}

		protected void    handleCycleError(Throwable e)
		{
			CycledTaskService.this.handleCycleError(this, e);
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
}