package com.tverts.objects;

/**
 * Interface of a wrapper of a {@link Runnable} task
 * into another task. Allows to access the original one.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public interface RunnableWrapper extends Runnable
{
	/* public: RunnableWrapper interface */

	/**
	 * Returns the task wrapped by this task.
	 */
	public Runnable  getWrappedTask();

	/**
	 * Returns the error (if) occured in the task wrapped
	 * during the one's execution.
	 */
	public Throwable getWrappedError();
}