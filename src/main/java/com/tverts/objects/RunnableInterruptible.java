package com.tverts.objects;

/**
 * Defines a {@link Runnable} that may be
 * interrupted.
 *
 * This interface allows to install halting
 * interface directly into the task instance.
 * This interface does not provide direct link
 * to the thread running the task.
 *
 * @author anton.baukin@gmail.com
 */
public interface RunnableInterruptible extends Runnable
{
	/* public: Interruptor interface */

	public static interface Interruptor
	{
		/* public: Interruptor interface */

		public void interrupt()
		  throws IllegalStateException;
	}

	/* public: RunnableInterruptible interface */

	/**
	 * Orders to interrupt the thread executing the task.
	 * If the thread does not waiting on an object, this
	 * call has no effect. Be careful here, this call
	 * is not guaranteed to break the task execution!
	 *
	 * If interruptor is not set to the task,
	 * {@link IllegalStateException} is raised.
	 */
	public void interrupt()
	  throws IllegalStateException;

	public void setInterruptor(Interruptor x);
}