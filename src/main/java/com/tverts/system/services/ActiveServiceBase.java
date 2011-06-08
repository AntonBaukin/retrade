package com.tverts.system.services;

/* tverts.com: services (mp) */

import com.tverts.system.mp.ServiceSynch;


/* tverts.com: objects */

import com.tverts.objects.RunnableWrapper;

/* com.tverts: support */

import com.tverts.support.LU;
import static com.tverts.support.LO.LANG_RU;


/**
 * Basic layer for an active services.
 * The present implementation has some
 * affinity with single task services
 * when defining the start/stop wait
 * support.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActiveServiceBase
       extends        StatefulServiceBase
{
	/* public: Service interface */

	public void         waitService()
	  throws InterruptedException
	{
		this.waitFlags.waitEmptyFlags();
	}

	/* public: ServiceInfo interface */

	public boolean      isActiveService()
	{
		return this.active;
	}


	/* public: ActiveServiceBase bean interface */

	public void         setActive(boolean active)
	{
		this.active = active;
	}

	public ServiceSynch getServiceSynch()
	{
		return serviceSynch;
	}

	public void         setServiceSynch(ServiceSynch serviceSynch)
	{
		this.serviceSynch = serviceSynch;
	}

	public boolean      isDaemonService()
	{
		return daemonService;
	}

	public void         setDaemonService(boolean daemon)
	{
		this.daemonService = daemon;
	}

	/**
	 * The pause (in milliseconds) the service makes
	 * just after starting it's activity. This pause
	 * allows to reduce the system overload during
	 * the start: different services really operate
	 * after a small (maybe, random) delay.
	 */
	public long         getOpenPause()
	{
		return openPause;
	}

	public void         setOpenPause(long openPause)
	{
		this.openPause = openPause;
	}

	/**
	 * If the service is not a Daemon, on the system stop
	 * there would be a graceful shutdown thread created
	 * that interrupts the thread given after this delay
	 * (milliseconds). By default is 24 sec.
	 */
	public long         getShutdownDelay()
	{
		return shutdownDelay;
	}

	public void         setShutdownDelay(long shutdownDelay)
	{
		this.shutdownDelay = shutdownDelay;
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

	protected void    beforeStartService()
	{
		this.waitFlags.occupy(WAIT_FLAG_START);
	}

	protected void    onStartServiceError(Throwable e)
	{
		this.waitFlags.release(WAIT_FLAG_START);
		super.onStartServiceError(e);
	}

	protected static final String WAIT_FLAG_STOP  = "stop";

	/**
	 * This version of stop brackets method returns
	 * {@link Boolean#FALSE} when the service was not
	 * active, here it does nothing more.
	 *
	 * If the service was active, accupies the wait flags
	 * with {@link #WAIT_FLAG_STOP} and returns
	 * {@link Boolean#TRUE}.
	 */
	protected Object  beforeStopService()
	{
		if(!getState().isActive())
			return Boolean.FALSE;

		this.waitFlags.occupy(WAIT_FLAG_STOP);
		return Boolean.TRUE;
	}

	protected void    onStopServiceError(Object x, Throwable e)
	{
		//?: {was active state} release the wait flag
		if(Boolean.TRUE.equals(x))
			this.waitFlags.release(WAIT_FLAG_STOP);

		super.onStopServiceError(x, e);
	}

	/**
	 * Invoked by the services' private threads
	 * before executing the task given.
	 *
	 * Note that this method must be invoked in
	 * the overwriting implementations!
	 *
	 * Answers {@code false} to exit the task thread
	 * immediately. In this case opposite call to
	 * {@link #closeTaskWrapped(Runnable)} is not made.
	 */
	protected boolean openTaskWrapped(Runnable task)
	{
		//~: set the start flag
		this.waitFlags.release(WAIT_FLAG_START);

		//~: wait in the synch point
		try
		{
			waitServiceMayStart();
		}
		catch(InterruptedException e)
		{
			//!: was interrupted may not start
			return false;
		}

		return true;
	}

	/**
	 * Invoked from {@link #openTaskWrapped(Runnable)}
	 * after {@link #WAIT_FLAG_START} flag is set.
	 */
	protected void    waitServiceMayStart()
	  throws InterruptedException
	{
		ServiceSynch synch = getServiceSynch();
		if(synch == null) return;

		if(LU.isI(getLog())) LU.I(getLog(), logsig(),
		  " waits on the synch point ", synch
		);

		synch.waitServiceMayStart(this);

		if(LU.isI(getLog())) LU.I(getLog(), logsig(),
		  " wait on synch point completed!"
		);
	}

	/**
	 * Task of an active service may invoke this method
	 * to indicate that the service is completed.
	 *
	 * The point of work when this call is done depends
	 * on the design and issues of a service. Abstract
	 * implementations do not invoke it.
	 */
	protected void    notifyServiceCompleted()
	{
		ServiceSynch synch = getServiceSynch();
		if(synch == null) return;

		if(LU.isI(getLog())) LU.I(getLog(), logsig(),
		  " notify is completed on synch point ", synch
		);

		synch.serviceCompleted(this);
	}

	/**
	 * Invoked by the services' private threads
	 * after executing the task given.
	 *
	 * Note that this method must be invoked in
	 * the overwriting implementations!
	 */
	protected void    closeTaskWrapped(Runnable task)
	{
		this.waitFlags.release(WAIT_FLAG_STOP);
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
			if(!openTaskWrapped())
			{
				closeTaskWrapped(false);
				return;
			}

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
					closeTaskWrapped(true);
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

		/**
		 * Performs the tasks on the thread entry to the task.
		 *
		 * Answers {@code false} to exit the task thread immediately.
		 * In this case opposite call to {@link #closeTaskWrapped(boolean)}
		 * would also be done.
		 */
		protected boolean      openTaskWrapped()
		{
			return ActiveServiceBase.this.
			  openTaskWrapped(this) && openTaskPause();
		}

		/**
		 * Pause made when starting the task. Returns
		 * {@code false} to exit the task activity.
		 */
		protected boolean      openTaskPause()
		{
			if(getOpenPause() > 0L) try
			{
				synchronized(pauseWaitee())
				{
					pauseWaitee().wait(getOpenPause());
				}
			}
			catch(InterruptedException e)
			{
				return false;
			}

			return true;
		}

		/**
		 * This method is invoked also is the case when
		 * {@link #openTaskWrapped()} had returned {@code false}.
		 */
		protected void         closeTaskWrapped(boolean opened)
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

		protected Object       pauseWaitee()
		{
			return (pauseWaitee != null)?(pauseWaitee):
			  (pauseWaitee = new Object());
		}

		/* private: the task */

		private Runnable  task;
		private Throwable error;
		private Object    pauseWaitee;
	}


	/* protected: graceful shutdown */

	protected void gracefulyShutdown(Thread thread)
	{
		new GracefulShutdown(thread, getShutdownDelay()).
		  start();
	}

	protected class GracefulShutdown extends Thread
	{
		/* public: constructor */

		public GracefulShutdown(Thread thread, long delay)
		{
			if(thread == null) throw new IllegalArgumentException();
			if(delay < 0L)     throw new IllegalArgumentException();

			this.thread = thread;
			this.delay  = delay;

			setDaemon(true);
			setName(String.format(
			  "graceful-shutdown: %s", thread.getName()
			));
		}

		/* public: GracefulShutdown interface */

		public Thread getThread()
		{
			return thread;
		}

		public long   getDelay()
		{
			return delay;
		}

		/* public: Thread interface */

		public void run()
		{
			//~: delay the shutdown
			delay();

			//!: invoke the shutdown
			shutdown();
		}

		/* protected: shutdown internals */

		protected void delay()
		{
			final Object waitee = new Object();

			if(delay != 0L) try
			{
				synchronized(waitee)
				{
					waitee.wait(delay);
				}
			}
			catch(Throwable e)
			{
				//~: ignore this error

				LU.E(getLog(), e, getName(),
				  ": unexpected error while waiting the delay!");
			}
		}

		protected void shutdown()
		{
			if(thread.isAlive() && !thread.isInterrupted()) try
			{
				LU.W(getLog(), "!!!: ", getName(),
				  ": interrupting thread of service [", logsig(), "]");

				thread.interrupt();
			}
			catch(Throwable e)
			{
				//~: ignore this error

				LU.E(getLog(), e, getName(),
				  ": unexpected error while waiting the delay!");
			}
		}

		/* protected: shutdown goal */

		protected final Thread thread;
		protected final long   delay;
	}


	/* private: service synchronization */

	private volatile ServiceSynch serviceSynch;


	/* private: service settings */

	private long    openPause;
	private long    shutdownDelay = 24000L; //<-- 24 sec
	private boolean active;
	private boolean daemonService;
}