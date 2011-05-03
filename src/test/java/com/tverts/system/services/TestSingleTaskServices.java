package com.tverts.system.services;

/* JUnit library */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/* com.tverts: multiprocessing */

import com.tverts.mp.WaitEmptyFlags;

/**
 * For classes {@link SingleTaskService} and
 * {@link CycledTaskService}.
 *
 * @author anton.baukin@gmail.com
 */
public class TestSingleTaskServices
{
	/* test entries */

	@org.junit.Test
	public void testServiceBasics()
	  throws InterruptedException
	{
		FlagTask          task    = new FlagTask();
		SingleTaskService service = new SingleTaskService();

		service.setExternalTask(task);
		service.setServiceName("testServiceBasics");

		assertNotNull(service.getServiceInfo());
		assertNotNull(service.getServiceStatus());

		assertTrue   (service.getServiceInfo().isActiveService());
		assertFalse  (service.getServiceStatus().isReady());
		assertFalse  (service.getServiceStatus().isActive());
		assertFalse  (service.getServiceStatus().isRunning());

		service.initService();

		assertTrue   (service.getServiceStatus().isReady());
		assertFalse  (service.getServiceStatus().isActive());
		assertFalse  (service.getServiceStatus().isRunning());

		assertFalse  (task.isFlagSet());

		service.freeService();

		assertFalse  (service.getServiceStatus().isReady());
		assertFalse  (service.getServiceStatus().isActive());
		assertFalse  (service.getServiceStatus().isRunning());
	}

	@org.junit.Test
	public void testStartStop()
	  throws InterruptedException
	{
		FlagTask          task    = new FlagTask();
		SingleTaskService service = new SingleTaskService();

		service.setExternalTask(task);
		service.setServiceName("testStartStop");
		service.initService();

		service.startService();
		service.waitService();

		assertTrue   (service.getServiceStatus().isReady());
		assertTrue   (service.getServiceStatus().isActive());

		service.stopService();
		service.waitService();

		assertTrue   (task.isFlagSet());
		assertTrue   (service.getServiceStatus().isReady());
		assertFalse  (service.getServiceStatus().isActive());
		assertFalse  (service.getServiceStatus().isRunning());

		service.freeService();
	}

	@org.junit.Test
	public void testStartPaused()
	  throws InterruptedException
	{
		FlagTask          task    = new FlagTask();
		SingleTaskService service = new SingleTaskService();

		service.setExternalTask(task);
		service.setServiceName("testStartPaused");
		service.initService();

		task.setWaitee();

		service.startService();
		service.waitService();

		assertTrue   (service.getServiceStatus().isReady());
		assertTrue   (service.getServiceStatus().isActive());
		assertTrue   (service.getServiceStatus().isRunning());

		task.goon();

		service.stopService();
		service.waitService();

		assertTrue   (task.isFlagSet());
		assertTrue   (service.getServiceStatus().isReady());
		assertFalse  (service.getServiceStatus().isActive());
		assertFalse  (service.getServiceStatus().isRunning());

		service.freeService();
	}

	@org.junit.Test
	public void testStartStopPaused()
	  throws InterruptedException
	{
		FlagTask          task    = new FlagTask();
		SingleTaskService service = new SingleTaskService();

		service.setExternalTask(task);
		service.setServiceName("testStartStopPaused");
		service.initService();

		task.setWaitee();

		service.startService();
		service.waitService();

		task.goonIwillWait();
		service.waitService(); //<-- possible hang-up is here

		service.stopService();
		service.waitService();

		assertTrue(task.isFlagSet());

		service.freeService();
	}

	@org.junit.Test
	public void testCycling()
	  throws InterruptedException
	{
		CycleTask         task    = new CycleTask();
		CycledTaskService service = new CycledTaskService();

		service.setExternalTask(task);
		service.setServiceName("testCycling");
		service.initService();

		task.setWaitee(); //<-- pause after each cycle

		service.startService();
		service.waitService();

		final int N = 3;

		for(int i = 1;(i <= N);i++)
		{
			assertTrue(service.getServiceStatus().isReady());
			assertTrue(service.getServiceStatus().isActive());
			assertTrue(service.getServiceStatus().isRunning());

			if(i != N)
				task.goonIwillWait(); //<-- index = i
			else //<-- stop at N
			{
				service.stopService();
				task.goon();
			}
		}

		service.waitService();

		assertEquals (N, task.getCounter());

		assertTrue   (service.getServiceStatus().isReady());
		assertFalse  (service.getServiceStatus().isActive());
		assertFalse  (service.getServiceStatus().isRunning());

		service.freeService();
	}

	@org.junit.Test
	public void testPreBreakedCycling()
	  throws InterruptedException
	{
		BreakingCycleTask task    = new BreakingCycleTask();
		CycledTaskService service = new CycledTaskService();

		service.setExternalTask(task);
		service.setServiceName("testPreBreakedCycling");
		service.initService();

		task.setCounter(0);
		task.setBreakPoint(0);

		service.startService();
		service.waitService();

		service.stopService();
		service.waitService();

		assertEquals(0, task.getCounter());

		assertTrue  (service.getServiceStatus().isReady());
		assertFalse (service.getServiceStatus().isActive());
		assertFalse (service.getServiceStatus().isRunning());
	}

	@org.junit.Test
	public void testBreakedCycling()
	  throws InterruptedException
	{
		BreakingCycleTask task    = new BreakingCycleTask();
		CycledTaskService service = new CycledTaskService();

		service.setExternalTask(task);
		service.setServiceName("testBreakedCycling");
		service.initService();

		final int N = 10;

		task.setBreakPoint(N);

		service.startService();
		service.waitService();


		for(int i = 1;(i <= N);i++)
			if(i != N)
				task.goonIwillWait(); //<-- index = i
			else
				//~: breaked automatically
				task.goon();

		testWait(500);

		service.stopService();
		service.waitService();

		assertEquals(N, task.getCounter());

		assertTrue  (service.getServiceStatus().isReady());
		assertFalse (service.getServiceStatus().isActive());
		assertFalse (service.getServiceStatus().isRunning());
	}

	/* helping tasks */

	protected static class FlagTask
	          implements   Runnable
	{
		/* public: Runnable interface */

		public void      run()
		{
			pause();
			flag = true;
		}

		/* public: FlagTask interface */

		public boolean   isFlagSet()
		{
			return flag;
		}

		public FlagTask  setWaitee()
		{
			this.waitee = new WaitEmptyFlags();
			return this;
		}

		public void      goon()
		{
			if(waitee == null)
				return;

			waitee.release("pause");
		}

		public void      goonIwillWait()
		  throws InterruptedException
		{
			if(waitee == null) return;

			waitee.relocc("pause", "master", true);
		}

		/* protected: test internals */

		protected void   pause()
		{
			if(waitee == null) return;

			try
			{
				waitee.relocc("master", "pause", true);
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}

		/* protected: task state */

		protected WaitEmptyFlags waitee;
		protected boolean        flag;
	}

	protected static class CycleTask
	          extends      FlagTask
	{

		/* public: Runnable interface */

		public void run()
		{
			pause();
			testWait(500);
			flag     = true;
			counter += step;
		}

		/* public: CycleTask interface */

		public int       getCounter()
		{
			return counter;
		}

		public CycleTask setCounter(int counter)
		{
			this.counter = counter;
			return this;
		}

		public int       getStep()
		{
			return step;
		}

		public CycleTask setStep(int step)
		{
			this.step = step;
			return this;
		}

		/* protected: task state */

		protected int counter = 1;
		protected int step    = 1;
	}

	protected static class BreakingCycleTask
	          extends      CycleTask
	          implements   BreakingTask
	{
		/* public: Runnable interface */

		public void run()
		{
			pause();
			flag     = true;
			counter += step;
		}

		/* public: BreakingTask interface */

		public boolean           isTaskBreaked()
		{
			return (breakPoint != null) && (breakPoint == counter);
		}

		/* public: BreakingCycleTask interface */

		public Integer           getBreakPoint()
		{
			return breakPoint;
		}

		public BreakingCycleTask setBreakPoint(Integer breakPoint)
		{
			this.breakPoint = breakPoint;
			return this;
		}

	/* protected: break point */

		protected Integer breakPoint;
	}

	/* misc routines */

	private static void testWait(long ms)
	{
		synchronized(TestSingleTaskServices.class)
		{
			try
			{
				TestSingleTaskServices.class.wait(ms);
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
