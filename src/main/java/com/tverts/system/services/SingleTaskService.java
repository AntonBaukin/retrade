package com.tverts.system.services;

/* tverts.com: objects */

import com.tverts.objects.RunnableWrapper;

/**
 * An active service that own only one working (task) thread.
 * It may be created to handle external task object, or may
 * be restricted in subclasses to create it's own tasks.
 *
 * When task exits the service becomes inactive. Note that the
 * external task object is wrapped into an
 * {@link RunnableWrapper}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SingleTaskService
       extends ActiveServiceBase
{
	/* public: SingleTaskService interface */

	public Runnable getExternalTask()
	{
		return externalTask;
	}

	public void     setExternalTask(Runnable task)
	{
		this.externalTask = task;
	}

	/* protected: service task handling */

	/**
	 * Creates the {@link Runnable} task instance to
	 * activate the service. By default returns the
	 * {@link #getExternalTask()} instance, if any.
	 */
	protected Runnable createTask()
	{
		return getExternalTask();
	}

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
			  "%s has no behaviuor task provided!", sig()));
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
			return SingleTaskService.this.running;
		}

		/* public: ServiceState interface (transitions) */

		public ServiceState  startService()
		{
			allocateThread(task).start();
			return this;
		}

		public ServiceState  stopService()
		{
			return createInitialState();
		}

		/* protected: the task */

		protected final Runnable task;
	}

	protected ServiceState createActiveState(ServiceState state)
	{
		return new SingleTaskState(allocateTask());
	}

	/* protected: task control & wrapping */

	protected void beforeStopService()
	{
		super.beforeStopService();
		this.breaked = true;
	}

	protected void openTaskWrapped(Runnable task)
	{
		this.running = true;

		super.openTaskWrapped(task);
	}

	protected void closeTaskWrapped(Runnable task)
	{
		this.running = false;
		this.breaked = false;

		super.closeTaskWrapped(task);
	}

	/* protected: thread allocation */

	protected Thread allocateThread(Runnable task)
	{
		Thread thread = new Thread(task);

		thread.setName(String.format(
		  "service:%s", getServiceName()));

		if(isDaemonService())
			thread.setDaemon(true);

		return thread;
	}

	/* private: the external task reference */

	private Runnable externalTask;

	/* private: service state flags */

	protected volatile boolean running;
	protected volatile boolean breaked;
}