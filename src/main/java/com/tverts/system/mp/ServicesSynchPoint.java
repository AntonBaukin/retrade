package com.tverts.system.mp;

/* standard Java classes */

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

/* com.tverts: services */

import com.tverts.system.Service;
import com.tverts.system.ServicesPoint;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Global services synchronization point.
 *
 * @author anton.baukin@gmail.com
 */
public class      ServicesSynchPoint
       implements ServiceSynchBus
{
	/* public: Singleton */

	public static ServicesSynchPoint getInstance()
	{
		return INSTANCE;
	}

	private static final ServicesSynchPoint INSTANCE =
	  new ServicesSynchPoint();

	protected ServicesSynchPoint()
	{
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

		this.readLock  = lock.readLock();
		this.writeLock = lock.writeLock();
	}

	/* public: ServiceSynchBus interface */

	public void serviceCompleted(Service service)
	{
		if(service == null)
			throw new IllegalArgumentException();

		if(LU.isD(getLog())) LU.D(getLog(),
		  "Services Synch Point (Bus) notified that '",
		  ServicesPoint.logsig(service), "' is completed."
		);

		//x: obtain read lock
		readLock.lock();

		try
		{
			//~: invoke all the listeners
			for(ServiceSynchBusListener listener : listeners)
				listener.onServiceCompleted(service);
		}
		finally
		{
			//x: free read lock
			readLock.unlock();
		}
	}

	public void connectListener(ServiceSynchBusListener ssbl)
	{
		if(ssbl == null)
			throw new IllegalArgumentException();

		//x: obtain write lock
		writeLock.lock();

		try
		{
			//~: add the listener
			listeners.add(ssbl);
		}
		finally
		{
			//x: free write lock
			writeLock.unlock();
		}
	}

	public void removeListener(ServiceSynchBusListener ssbl)
	{
		if(ssbl == null) return;

		//x: obtain write lock
		writeLock.lock();

		try
		{
			//~: add the listener
			listeners.remove(ssbl);
		}
		finally
		{
			//x: free write lock
			writeLock.unlock();
		}
	}


	/* protected: listeners */

	/**
	 * The array of bus listeners.
	 */
	protected final ArrayList<ServiceSynchBusListener> listeners =
	  new ArrayList<ServiceSynchBusListener>(16);


	/* protected: logging */

	protected String getLog()
	{
		return ServicesPoint.LOG_SERVICE_MAIN;
	}


	/* protected: listeners */

	/**
	 * The read (notification) lock on the listeners array.
	 */
	protected final Lock readLock;

	/**
	 * The update lock on the listeners array.
	 */
	protected final Lock writeLock;
}