package com.tverts.system.services;

/* com.tverts: multiprocessing */

import com.tverts.mp.WaitEmptyFlags;

/* tverts.com: support */

import static com.tverts.support.LO.LANG_RU;

/**
 * Stateful services are the next level of
 * implementation from service basics.
 *
 * A stateful service is a state machine having
 * differ internal state objects for each
 * lifecycle state: null, prepared, active.
 *
 * Does not couple tightly with active services,
 * but provides some means for them.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public abstract class StatefulServiceBase
       extends        ServiceBase
{
	/* public: Service interface */

	public void initService()
	{
		synchronized(this.stateMutex)
		{
			if(getState() != null)
				return;

			checkInitService();
			setState(createInitialState());
			afterInitService();
		}
	}

	public void startService()
	{
		synchronized(this.stateMutex)
		{
			checkStartService();
			beforeStartService();

			try
			{
				setState(getState().startService());
				afterStartService();
			}
			catch(Throwable e)
			{
				onStartServiceError(e);
			}
		}
	}

	public void stopService()
	{
		synchronized(this.stateMutex)
		{
			checkStopService();
			Object x = beforeStopService();

			try
			{
				setState(getState().stopService());
				afterStopService(x);
			}
			catch(Throwable e)
			{
				onStopServiceError(x, e);
			}
		}
	}

	public void waitService()
	  throws InterruptedException
	{}

	public void freeService()
	{
		synchronized(this.stateMutex)
		{
			if(getState() == null)
				return;

			checkFreeService();

			beforeFreeService();
			try
			{
				getState().freeService();
			}
			finally
			{
				setState(null);
			}
		}
	}

	/* protected: ServiceBase interface (state) */

	public String  getStateName(String lang)
	{
		synchronized(this.stateMutex)
		{
			return (getState() != null)?
			  (getState().getStateName(lang)):
			  (getStateNameEmpty(lang));
		}
	}

	public boolean isReady()
	{
		synchronized(this.stateMutex)
		{
			return (getState() != null) && getState().isReady();
		}
	}

	public boolean isActive()
	{
		synchronized(this.stateMutex)
		{
			return (getState() != null) && getState().isActive();
		}
	}

	public boolean isRunning()
	{
		synchronized(this.stateMutex)
		{
			return (getState() != null) && getState().isRunning();
		}
	}

	/* protected: service state */

	protected interface ServiceState
	{
		/* public: ServiceState interface (state) */

		public String       getStateName(String lang);

		public boolean      isReady();

		public boolean      isActive();

		public boolean      isRunning();

		/* public: ServiceState interface (transitions) */

		public ServiceState startService();

		public ServiceState stopService();

		/**
		 * Implementation guarantees that this method
		 * would be invoked only once for any state
		 * instance, and at the end of it's life.
		 *
		 * After this call the service's state
		 * reference is cleared.
		 */
		public void         freeService();
	}

	protected ServiceState createInitialState()
	{
		return new ServiceStateBase();
	}

	protected ServiceState getState()
	{
		return serviceState;
	}

	protected void         setState(ServiceState state)
	{
		this.serviceState = state;
	}

	/* protected: task control */

	protected void         afterInitService()
	{}

	protected void         beforeStartService()
	{}

	/**
	 * Invoked on successful service start, on error
	 * {@link #onStartServiceError(Throwable)} is.
	 */
	protected void         afterStartService()
	{}

	protected void         onStartServiceError(Throwable e)
	{
		throw new RuntimeException(String.format(
		  "Unexpected error occured while starting %s!",
		  logsig()), e);
	}

	/**
	 * Invoked before stop the service occurs.
	 *
	 * The returned hint object is then passed to
	 * {@link #afterStopService(Object)} and
	 * {@link #onStopServiceError(Object, Throwable)}.
	 */
	protected Object       beforeStopService()
	{
		return null;
	}

	/**
	 * Invoked on successful service stop, on error
	 * {@link #onStopServiceError(Object, Throwable)} is.
	 */
	protected void         afterStopService(Object x)
	{}

	protected void         onStopServiceError(Object x, Throwable e)
	{
		throw new RuntimeException(String.format(
		  "Unexpected error occured while stopping %s!",
		  logsig()), e);
	}

	protected void         beforeFreeService()
	{}

	/* protected: state implementation */

	protected class      ServiceStateBase
	          implements ServiceState
	{
		/* public: ServiceState interface (state) */

		protected static final String STATE_NAME_READY_EN = "ready";
		protected static final String STATE_NAME_READY_RU = "готов";

		public String  getStateName(String lang)
		{
			return LANG_RU.equals(lang)?
			  STATE_NAME_READY_RU:STATE_NAME_READY_EN;
		}

		public boolean isReady()
		{
			return true;
		}

		public boolean isActive()
		{
			return false;
		}

		public boolean isRunning()
		{
			return false;
		}

		/* public: ServiceState interface (transitions) */

		public ServiceState startService()
		{
			return this;
		}

		public ServiceState stopService()
		{
			return this;
		}

		public void         waitService()
		{}

		public void         freeService()
		{}
	}

	/* protected: state transitions checks */

	protected void checkServiceReady(String operation)
	{
		if((getState() == null) || !getState().isReady())
			throw new IllegalStateException(String.format(
			  "%s: operation '%s' can't be issued as the " +
			  "service is not ready!",

			  logsig(), operation));
	}

	protected void checkInitService()
	{}

	protected void checkStartService()
	{
		checkServiceReady("start");
	}

	protected void checkStopService()
	{
		checkServiceReady("stop");
	}

	protected void checkFreeService()
	{
		checkServiceReady("free");
	}

	/* protected: service state */

	private volatile ServiceState   serviceState;

	/* protected: service synchronization */

	protected final Object          stateMutex = new Object();
	protected final WaitEmptyFlags  waitFlags  = new WaitEmptyFlags();
}