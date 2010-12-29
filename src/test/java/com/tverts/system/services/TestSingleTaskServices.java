package com.tverts.system.services;

/* JUnit library */

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * For classes {@link SingleTaskService} and
 * {@link CycledTaskService}.
 *
 * @author anton baukin (abaukin@mail.ru)
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

	//TODO test cycling service in TestSingleTaskServices

	/* helping tasks */

	protected static class FlagTask
	          implements   Runnable
	{
		/* public: Runnable interface */

		public void      run()
		{
			pause();
			this.flag = true;
		}

		/* public: FlagTask interface */

		public boolean   isFlagSet()
		{
			return flag;
		}

		public Object    getWaitee()
		{
			return waitee;
		}

		public FlagTask  setWaitee(Object waitee)
		{
			this.waitee = waitee;
			return this;
		}

		public FlagTask  setWaitee()
		{
			this.waitee = new Object();
			return this;
		}

		public long      getWaitTime()
		{
			return waitTime;
		}

		public FlagTask  setWaitTime(long waitTime)
		{
			this.waitTime = waitTime;
			return this;
		}

		public void      goon()
		{
			if(waitee == null)
				return;

			synchronized(waitee)
			{
				waitee.notifyAll();
			}
		}

		/* protected: test internals */

		protected void   pause()
		{
			if(waitee == null)
				return;

			try
			{
				synchronized(waitee)
				{
					if(waitTime > 0L)
						waitee.wait(waitTime);
					else
						waitee.wait();
				}
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}

		/* protected: task state */

		protected Object  waitee;
		protected long    waitTime;
		protected boolean flag;
	}

}
