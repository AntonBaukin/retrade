package com.tverts.system.services;

/* tverts.com: objects */

import com.tverts.objects.RunnableInterruptible;
import com.tverts.objects.RunnableInterruptible.Interruptor;
import com.tverts.objects.RunnableWrapper;

/* tverts.com: support */

import static com.tverts.support.OU.interruptable;

/**
 * An active service that own only one working (task) thread.
 * It may be created to handle external task object, or may
 * be restricted in subclasses to create it's own tasks.
 *
 * When task exits the service becomes inactive. Note that the
 * external task object is wrapped into an
 * {@link RunnableWrapper}.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SingleTaskServiceBase
       extends        ActiveServiceBase
{
	/* protected: service task handling */

	/**
	 * Creates the {@link Runnable} task instance to
	 * activate the service.
	 */
	protected abstract Runnable
	                   createTask();

	protected Runnable allocateTask()
	{
		Runnable task = createTask();

		checkTask(task);
		return wrapTask(task);
	}

	protected Runnable wrapTask(Runnable task)
	{
		return new TaskWrapper(task);
	}

	protected void     checkTask(Runnable task)
	{
		if(task == null)
			throw new IllegalStateException(String.format(
			  "%s has no behaviuor task provided!", logsig()));
	}

	/* protected: ActiveServiceBase (active state impl.) */

	protected class   SingleTaskState
	          extends ActiveStateBase
	{
		/* public: constructor */

		public SingleTaskState(Runnable task)
		{
			this.task = task;
		}

		/* public: SingleTaskState interface */

		public Runnable      getTask()
		{
			return task;
		}

		/* public: ServiceState interface (state) */

		public boolean       isRunning()
		{
			return SingleTaskServiceBase.this.running;
		}

		/* public: ServiceState interface (transitions) */

		public ServiceState  startService()
		{
			if(thread != null) throw new IllegalStateException(
			  "Service thrad is already created!"
			);

			//~: create the thread
			this.thread = allocateThread(task);

			//~: install the interruptor
			installInterruptor(task, thread);

			//!: start the thread
			thread.start();

			return this;
		}

		public ServiceState  stopService()
		{
			if(!this.thread.isDaemon())
			{
				gracefulyShutdown(this.thread);
				this.thread = null; //<-- remove the reference
			}

			return createInitialState();
		}

		/* protected: the task */

		protected final Runnable task;
		protected Thread         thread;
	}

	protected ServiceState createActiveState(ServiceState state)
	{
		return new SingleTaskState(allocateTask());
	}

	/* protected: task control & wrapping */

	protected Object  beforeStopService()
	{
		this.breaked = true;
		return super.beforeStopService();
	}

	protected boolean openTaskWrapped(Runnable task)
	{
		return (this.running = super.openTaskWrapped(task));
	}

	protected void    closeTaskWrapped(Runnable task)
	{
		this.running = false;
		this.breaked = false;
		super.closeTaskWrapped(task);
	}

	/* protected: thread allocation */

	protected Thread allocateThread(Runnable task)
	{
		Thread thread = new Thread(task);

		thread.setName(defineThreadName(task));
		thread.setDaemon(isDaemonService());

		return thread;
	}

	protected String defineThreadName(Runnable task)
	{
		return String.format("service: %s",
		  getServiceInfo().getServiceSignature());
	}

	/* protected: interruptible tasks support */

	protected void installInterruptor(Runnable task, Thread thread)
	{
		RunnableInterruptible xtask = interruptable(task);

		//?: {interruptible task found}
		if(xtask != null)
		   xtask.setInterruptor(new InterruptLink(thread));
	}

	protected static class InterruptLink
	          implements   Interruptor
	{
		/* public: constructor */

		public InterruptLink(Thread thread)
		{
			this.thread = thread;
		}

		/* public: Interruptor interface */

		public void interrupt()
		  throws IllegalStateException
		{
			thread.interrupt();
		}

		/* protected: the thread */

		protected final Thread thread;
	}

	/* private: service state flags */

	protected volatile boolean running;
	protected volatile boolean breaked;
}