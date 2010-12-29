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
		synchronized(getStateMutex())
		{
			if(getState() != null)
				return;

			checkInitService();
			setState(createInitialState());
		}
	}

	public void startService()
	{
		synchronized(getStateMutex())
		{
			checkStartService();
			beforeStartService();
			setState(getState().startService());
			afterStartService();
		}
	}

	public void stopService()
	{
		synchronized(getStateMutex())
		{
			checkStopService();
			beforeStopService();
			setState(getState().stopService());
			afterStopService();
		}
	}

	public void waitService()
	  throws InterruptedException
	{}

	public void freeService()
	{
		synchronized(getStateMutex())
		{
			if(getState() == null)
				return;

			checkFreeService();

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
		synchronized(getStateMutex())
		{
			return (getState() != null)?
			  (getState().getStateName(lang)):
			  (getStateNameEmpty(lang));
		}
	}

	public boolean isReady()
	{
		synchronized(getStateMutex())
		{
			return (getState() != null) && getState().isReady();
		}
	}

	public boolean isActive()
	{
		synchronized(getStateMutex())
		{
			return (getState() != null) && getState().isActive();
		}
	}

	public boolean isRunning()
	{
		synchronized(getStateMutex())
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

	protected void         beforeStartService()
	{}

	protected void         afterStartService()
	{}

	protected void         beforeStopService()
	{}

	protected void         afterStopService()
	{}

	/* protected: synchronization primitives */

	protected Object         getStateMutex()
	{
		return stateMutex;
	}

	protected WaitEmptyFlags getWaitFlags()
	{
		return waitFlags;
	}

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

			  sig(), operation));
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

	/* private: service state */

	private ServiceState   serviceState;

	/* private: service synchronization */

	private Object         stateMutex = new Object();
	private WaitEmptyFlags waitFlags  = new WaitEmptyFlags();
}