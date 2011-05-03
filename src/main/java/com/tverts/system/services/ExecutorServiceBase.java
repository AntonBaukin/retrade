package com.tverts.system.services;

/* tverts.com: objects */

import com.tverts.objects.RunnableInterruptible;

/* tverts.com: support */

import com.tverts.support.LU;

/**
 * This abstract bases the services that are for
 * execute a finite or 'endless' number of tasks.
 *
 * {@link TasksProvider} internal strategy allows
 * to isolate the service from the method of obtaining
 * {@link Runnable} tasks.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ExecutorServiceBase
       extends        CycledTaskServiceBase
{
	/* public: constructors */

	public    ExecutorServiceBase()
	{
		this.tasksProvider = createTasksProvider();
		if(this.tasksProvider == null)
			throw new IllegalStateException();
	}

	protected ExecutorServiceBase(TasksProvider tasksProvider)
	{
		if(tasksProvider == null)
			throw new IllegalStateException();
		this.tasksProvider = tasksProvider;
	}

	/* public static: task provider interface */

	/**
	 * Strategy to obtain {@link Runnable} tasks to execute.
	 */
	public static interface TasksProvider
	{
		/* public: TasksProvider interface */

		/**
		 * Blocking call for the next execution task.
		 * The task returned is scheduled for the execution.
		 *
		 * If {@link InterruptedException} is raised, the
		 * task omits this exception and exits immediately.
		 *
		 * The same behaviour is when this call returns
		 * {@code null}. This allows to execute the predefined
		 * number of tasks and then suspend the service:
		 * the service still would be active, but it's thread
		 * would be actually dead.
		 *
		 * Note that this call is done within one thread only.
		 * (The thread that runs {@link ExecutorTask}).
		 */
		public Runnable waitNextTask()
		  throws InterruptedException;

		/**
		 * Orders to cancel the waining for the next task.
		 * This call is made not by a thread waining.
		 *
		 * Note that waining thread is only the one invoking
		 * {@link #waitNextTask()} at all the times.
		 *
		 * If the thread is not waiting, no effect must be.
		 */
		public void     cancelTaskWaiting();
	}

	/* protected: tasks provider support */

	protected abstract TasksProvider
	                        createTasksProvider();

	protected TasksProvider getTasksProvider()
	{
		return tasksProvider;
	}

	/* protected: SingleTaskServiceBase interface */

	protected Runnable      createTask()
	{
		return new ExecutorTask();
	}

	/* protected: SingleTaskServiceBase (task control & wrapping) */

	protected Object        beforeStopService()
	{
		this.breaked = true;
		getTasksProvider().cancelTaskWaiting();
		return super.beforeStopService();
	}

	/* protected: executor task */

	/**
	 * This task is terminal, not a wrapper. It is intended
	 * to be called in a cycle wrapper. On each call this inner
	 * task of the service waits for the next task provided.
	 *
	 * It finishes when whether the provider returns {@code null},
	 * or {@link InterruptedException} occurs.
	 */
	protected class      ExecutorTask
	          implements BreakingTask, RunnableInterruptible
	{
		/* public: Runnable interface */

		public void    run()
		{
			try
			{
				Runnable task = getTasksProvider().waitNextTask();

				//?: {there are no more tasks} set the task break
				if(task == null)
				{
					this.breaked = true;
					return;
				}

				//!: execute the task
				try
				{
					execTask(task);
				}
				catch(Throwable e)
				{
					handleExecError(task, e);
				}
			}
			catch(InterruptedException e)
			{
				this.breaked = true;
			}
		}

		/* public: BreakingTask interface */

		public boolean isTaskBreaked()
		{
			return this.breaked;
		}

		/* public: RunnableInterruptible interface */

		public void    interrupt()
		  throws IllegalStateException
		{
			Interruptor x = this.interruptor;
			if(x != null) x.interrupt();
		}

		public void    setInterruptor(Interruptor x)
		{
			this.interruptor = x;
		}

		/* protected: task execution */

		/**
		 * This implementation runs the task in the thread of the
		 * service. It is possible to allocate a new thread (from
		 * a pool, or else) and to delegate the execution to it:
		 * in this case this service would not react on the errors
		 * ({@link #handleExecError(Runnable, Throwable)}) in
		 * the other thread.
		 */
		protected void execTask(Runnable task)
		  throws Throwable
		{
			task.run();
		}

		/**
		 * In this version the exception of the task is
		 * just logged, but not rethrown out. Also, the
		 * executor task is not breaked.
		 */
		protected void handleExecError(Runnable task, Throwable e)
		{
			LU.E(getLog(), e, logsig(),
			  " caught error when executing the task of the class '",
			  task.getClass().getName(), "'");
		}

		/* protected: the task state */

		protected volatile Interruptor interruptor;

		/**
		 * Is set when the tasks provider returns {@code null},
		 * or the waiting was interrupted. If is set, the task
		 * would not be invoked more.
		 */
		protected volatile boolean     breaked;
	}

	/* private: tasks provider */

	private final TasksProvider tasksProvider;
}