package com.tverts.system.services;

/**
 * Implementation of a {@link SingleTaskServiceBase}
 * that allows to set an external task to run it
 * as a service.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SingleTaskService
       extends SingleTaskServiceBase
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
		Runnable task = getExternalTask();

		if(task == null) throw new IllegalStateException(
		  "Single Task Service has no external task set!");

		return task;
	}

	/* private: the external task reference */

	private Runnable externalTask;
}