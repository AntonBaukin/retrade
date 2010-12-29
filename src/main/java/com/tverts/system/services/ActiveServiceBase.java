package com.tverts.system.services;

/* com.tverts: support classes */

import com.tverts.support.RunnableWrapper;
import static com.tverts.support.LO.LANG_RU;

/**
 * Basic layer for an active services.
 * The present implementation has some
 * affinity with single task services
 * when defining the start/stop wait
 * support.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public abstract class ActiveServiceBase
       extends        StatefulServiceBase
{
	/* public: Service interface */

	public void    waitService()
	  throws InterruptedException
	{
		getWaitFlags().waitEmptyFlags();
	}

	/* public: ServiceInfo interface */

	public boolean isActiveService()
	{
		return true;
	}

	/* public: properties access (bean) */

	public boolean isDaemonService()
	{
		return daemonService;
	}

	public void    setDaemonService(boolean daemon)
	{
		this.daemonService = daemon;
	}

	/* protected: active state implementation */

	protected class   ActiveStateBase
	          extends ServiceStateBase
	{
		/* public: ServiceState interface (state) */

		protected static final String STATE_NAME_ACTIVE_EN  = "active";
		protected static final String STATE_NAME_ACTIVE_RU  = "активен";

		protected static final String STATE_NAME_RUNNING_EN = "running";
		protected static final String STATE_NAME_RUNNING_RU = "выполняется";

		public String  getStateName(String lang)
		{
			if(isRunning())
				return LANG_RU.equals(lang)?
				  STATE_NAME_RUNNING_RU:STATE_NAME_RUNNING_EN;

			if(isActive())
				return LANG_RU.equals(lang)?
				  STATE_NAME_ACTIVE_RU:STATE_NAME_ACTIVE_EN;

			return super.getStateName(lang);
		}

		public boolean isActive()
		{
			return true;
		}
	}

	protected ServiceState createInitialState()
	{
		return new ReadyStateBase();
	}

	protected ServiceState createActiveState(ServiceState state)
	{
		return new ActiveStateBase();
	}

	/* protected: ready state implementation */

	protected class   ReadyStateBase
	          extends ServiceStateBase
	{
		/* public: ServiceState interface (transitions) */

		public ServiceState startService()
		{
			return createActiveState(this).startService();
		}
	}

	/* protected: task control & wrapping */

	protected static final String WAIT_FLAG_START = "start";

	protected void beforeStartService()
	{
		getWaitFlags().occupy(WAIT_FLAG_START);
	}

	protected static final String WAIT_FLAG_STOP  = "stop";

	protected void beforeStopService()
	{
		getWaitFlags().occupy(WAIT_FLAG_STOP);
	}

	/**
	 * Invoked by the services' private threads
	 * before executing the task given.
	 *
	 * Note that this method must be invoked in
	 * the overwriting implementations!
	 */
	protected void openTaskWrapped(Runnable task)
	{
		getWaitFlags().release(WAIT_FLAG_START);
	}

	/**
	 * Invoked by the services' private threads
	 * after executing the task given.
	 *
	 * Note that this method must be invoked in
	 * the overwriting implementations!
	 */
	protected void closeTaskWrapped(Runnable task)
	{
		getWaitFlags().release(WAIT_FLAG_STOP);
	}

	protected class      TaskWrapper
	          implements RunnableWrapper
	{
		/* public: constructor */

		public TaskWrapper(Runnable task)
		{
			this.task = task;
		}

		/* public: Runnable interface */

		public void run()
		{
			openTaskWrapped();

			try
			{
				getWrappedTask().run();
			}
			catch(Throwable e)
			{
				setWrappedError(e);
			}
			finally
			{
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
		}

		/* public: RunnableWrapper interface */

		public Runnable  getWrappedTask()
		{
			return task;
		}

		public Throwable getWrappedError()
		{
			return error;
		}

		/* public: TaskWrapper interface */

		public void      setWrappedError(Throwable error)
		{
			this.error = error;
		}

		/* protected: wrapped task execution */

		protected void         openTaskWrapped()
		{
			ActiveServiceBase.this.openTaskWrapped(this);
		}

		protected void         closeTaskWrapped()
		{
			ActiveServiceBase.this.closeTaskWrapped(this);
		}

		protected BreakingTask unwrapBreakingTask(Runnable task)
		{
			while((task != null) && !(task instanceof BreakingTask))
				task = !(task instanceof TaskWrapper)?(null):
				  ((TaskWrapper)task).getWrappedTask();

			return (BreakingTask)task;
		}

		/* private: the task */

		private Runnable  task;
		private Throwable error;
	}

	/* protected: service  settings */

	private boolean daemonService;
}