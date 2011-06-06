package com.tverts.system.services;

/* standard Java classes */

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This service is for executing arbitrary
 * {@link Runnable} tasks stored in the blocking queue.
 *
 * As an abstract it does not define how the tasks are
 * added to the queue.
 *
 * A noticeable feature of this service is that it's
 * tasks provider controls own behaviour via the same
 * queue. It places special tasks to the queue to unblock
 * the waiting master thread.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class QueueExecutorServiceBase
       extends        ExecutorServiceBase
{
	/* public: constructors */

	public    QueueExecutorServiceBase()
	{}

	protected QueueExecutorServiceBase(TasksProvider tasksProvider)
	{
		super(tasksProvider);
	}


	/* protected: ExecutorServiceBase interface */

	protected TasksProvider createTasksProvider()
	{
		return new DequeProvider();
	}

	protected void          enqueueTask(Runnable task)
	{
		((DequeProvider)getTasksProvider()).
		  appendTask(task);
	}

	/* protected: StatefulServiceBase (state control) */

	/**
	 * Adds the initial tasks to the queue.
	 */
	protected void   afterInitService()
	{
		super.afterInitService();

		//~: add initial tasks to the queue
		appendInitialTasks();

		//~: add the task of the service complete notification
		appendCompleteNotificationTask();
	}

	protected void   appendInitialTasks()
	{}

	protected void   appendCompleteNotificationTask()
	{
		enqueueTask(new NotifyServiceCompleted());
	}


	/* public static: Queue Task Provider */

	/**
	 * Superclass for all the commands of the provider.
	 * Other threads may put the commands to the queue
	 * to order master thread to do something.
	 */
	public static class QueueCommand implements Runnable
	{
		/* public: Runnable interface */

		public void run()
		{}
	}

	/**
	 * This command orders to cancel waiting the next tasks
	 * and to return {@code null} result that means to
	 * stop queue executor.
	 */
	public static class BreakCommand extends QueueCommand
	{}

	/**
	 * Add this command to the queue after the initial tasks
	 * to notify waiting on the Service Synch that this
	 * service is completed.
	 */
	protected class NotifyServiceCompleted extends QueueCommand
	{
		/* public: Runnable interface */

		public void run()
		{
			notifyServiceCompleted();
		}
	}

	public static class DequeProvider implements TasksProvider
	{
		/* public: constructors */

		public DequeProvider(BlockingDeque<Runnable> deque)
		{
			if(deque == null)
				throw new IllegalArgumentException();
			this.deque = deque;
		}

		public DequeProvider()
		{
			this(new LinkedBlockingDeque<Runnable>());
		}

		/* public: DequeProvider interface */

		/**
		 * Appends the task to the end of the queue.
		 * It is also allowed to append the commands.
		 *
		 * Throws {@link IllegalStateException} if the
		 * queue is full.
		 */
		public void appendTask(Runnable task)
		  throws IllegalStateException
		{
			if(task == null)
				throw new IllegalStateException();

			deque.addLast(task);
		}

		/**
		 * Inserts the command as the first task of the queue.
		 * Note that true tasks may only be appended.
		 *
		 * Throws {@link IllegalStateException} if the
		 * queue is full.
		 */
		public void peerCommand(QueueCommand command)
		  throws IllegalStateException
		{
			if(command == null)
				throw new IllegalStateException();

			deque.addFirst(command);
		}

		/* public: TasksProvider interface */

		public Runnable    waitNextTask()
		  throws InterruptedException
		{
			Runnable task;

			//~: commands processing cycle
			do
			{
				//~: take the next task   <-- blocking!
				task = selectNext();

				//?: {there are no tasks more} quit
				if(task == null) return null;

				//?: {this task is a command} process it
				if(task instanceof QueueCommand)
					task = processCommand((QueueCommand)task);

				//?: {ordered to break the cycle}
				if(task instanceof BreakCommand)
					return null;
			}
			while(task == null);

			return task;
		}

		public void        cancelTaskWaiting()
		{
			deque.addFirst(new BreakCommand());
		}

		/* protected: deque processing */

		protected Runnable selectNext()
		  throws InterruptedException
		{
			return deque.takeFirst();
		}

		/**
		 * This plain implementation of commands processing
		 * returns as-is any break command, and runs other
		 * commands, then returns {@code null}.
		 *
		 * Result {@code null} means to select the next
		 * task from the queue (in the main waiting cycle).
		 */
		protected Runnable processCommand(QueueCommand cmd)
		{
			//?: {a break command} return it
			if(cmd instanceof BreakCommand)
				return cmd;

			//!: invoke the command
			cmd.run();

			//~: take the next command
			return null;
		}

		/* private: the deque */

		protected final BlockingDeque<Runnable> deque;
	}
}