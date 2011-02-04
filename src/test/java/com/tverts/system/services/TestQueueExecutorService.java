package com.tverts.system.services;

/* standard Java classes */

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;

/* JUnit library */

import com.tverts.system.services.QueueExecutorServiceBase.DequeProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link QueueExecutorServiceBase} class.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class TestQueueExecutorService
{
	/* test entries */

	/**
	 * This creates service without tasks. This service
	 * must start and then wait infinitely.
	 */
	@org.junit.Test
	public void testNoTasks()
	  throws InterruptedException
	{
		QueueExecutorService service = service();

		//~: start it
		start(service);

		//~: pause 2 sec
		pause(2000);

		//!: it must be still running (waiting for the tasks)
		assertTrue(service.isRunning());

		//~: stop -> free it
		stop_free(service);
	}

	@org.junit.Test
	public void testFourSteps()
	  throws InterruptedException
	{
		QueueExecutorService service = service();

		//~: start it
		start(service);

		//A: first step
		exec_wait(service, task("A"));
		assertEquals("A", text.toString());
		pause(500);

		//B: second step
		exec_wait(service, task("B"));
		assertEquals("A B", text.toString());

		//C: third step
		exec_wait(service, task("C"));
		assertEquals("A B C", text.toString());
		pause(500);

		//D: fourth step
		exec_wait(service, task("D"));
		assertEquals("A B C D", text.toString());

		//~: stop -> free it
		stop_free(service);
	}

	@org.junit.Test
	public void testStressive()
	  throws InterruptedException
	{
		QueueExecutorService service = service();

		//~: start it
		start(service);

		long j, i = 1L, a = 0L;

		while(true)
		{
			j =  i;                    //<-- j = i before step
			i += ((i%2) == 0)?(2):(3); //<-- step i
			a =  a*2 + j;              //<-- order-dependent accumulation

			boolean finished = (i >= 2000L);

			if(!finished)
			{
				service.execute(task(j, false));
				if((j%5 == 0)) pause(10);
				continue;
			}

			//!: finished -> wait the last task
			synchronized(waitee)
			{
				service.execute(task(j, true));
				waitee.wait();
			}

			break;
		}

		//!: test the accumulated value
		assertEquals(this.accum, a);

		//~: stop -> free it
		stop_free(service);
	}

	/* private: queue provider support */

	private ExecutorServiceBase.TasksProvider
	                 provider(Runnable... tasks)
	{
		return new DequeProvider(
		  new LinkedBlockingDeque<Runnable>(Arrays.asList(tasks)));
	}

	private Runnable task(String text)
	{
		return new TextTask(text);
	}

	private Runnable task(long num, boolean wakeup)
	{
		return new NumTask(num, wakeup);
	}

	private void     exec_wait(QueueExecutorService service, Runnable task)
	  throws InterruptedException
	{
		synchronized(waitee)
		{
			service.execute(task);
			waitee.wait();
		}
	}

	private QueueExecutorService
	                 service(Runnable... tasks)
	{
		return service(provider(tasks));
	}

	private QueueExecutorService
	                 service(ExecutorServiceBase.TasksProvider provider)
	{
		QueueExecutorService service = new QueueExecutorService(provider);

		service.setServiceName("queueExecutorService");
		service.setServiceTitleEn("Queue Executor Service");
		service.initService();

		assertTrue(service.isReady());
		assertTrue(service.isActiveService());

		return service;
	}

	/* private: test tasks */

	private StringBuilder text =
	  new StringBuilder();

	private class TextTask implements Runnable
	{
		public TextTask(String text)
		{
			this.text = text;
		}

		public void run()
		{
			if(TestQueueExecutorService.this.text.length() != 0)
				TestQueueExecutorService.this.text.append(' ');
			TestQueueExecutorService.this.text.append(text);

			synchronized(waitee)
			{
				waitee.notify();
			}
		}

		private String text;
	}

	private long accum = 0L;

	private class NumTask implements Runnable
	{
		private NumTask(long num, boolean wakeup)
		{
			this.num = num;
			this.wakeup = wakeup;
		}

		public void run()
		{
			accum = accum*2 + num;

			if(wakeup) synchronized(waitee)
			{
				waitee.notify();
			}
		}

		private long    num;
		private boolean wakeup;
	}

	/* private: testing support */

	private final Object waitee = new Object();

	private void pause(long ms)
	  throws InterruptedException
	{
		synchronized(waitee)
		{
			waitee.wait(ms);
		}
	}

	private void start(QueueExecutorService service)
	  throws InterruptedException
	{
		service.startService();
		service.waitService();
		assertTrue(service.isActive());
	}

	private void stop_free(QueueExecutorService service)
	  throws InterruptedException
	{
		service.stopService();
		service.waitService();

		assertFalse(service.isRunning());
		assertFalse(service.isActive());

		service.freeService();
		assertFalse(service.isReady());
	}
}