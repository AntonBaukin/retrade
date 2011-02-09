package com.tverts.system.services;

/* standard Java classes */

import java.util.concurrent.Executor;

/**
 * Exposes {@link Executor} interface as a
 * System Service. Handles unlimited queue
 * of {@link Runnable} tasks.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      QueueExecutorService
       extends    QueueExecutorServiceBase
       implements Executor
{
	/* public: constructors */

	public    QueueExecutorService()
	{}

	protected QueueExecutorService(TasksProvider tasksProvider)
	{
		super(tasksProvider);
	}

	/* public: Executor interface */

	/**
	 * Adds the task to the end of the queue.
	 * Execution service is blocked on the queue
	 * until elements arrives.
	 */
	public void execute(Runnable task)
	{
		((DequeProvider)getTasksProvider()).
		  appendTask(task);
	}
}