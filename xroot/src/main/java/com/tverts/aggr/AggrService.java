package com.tverts.aggr;

/* Java */

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system (services, tx) */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;
import com.tverts.system.services.events.SystemReady;
import com.tverts.system.tx.TxPoint;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrRequest;
import com.tverts.endure.aggr.GetAggrValue;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Facade point to post aggregation requests into the system.
 *
 * @author anton.baukin@gmail.com
 */
public class AggrService extends ServiceBase
{
	/* Aggregation Service Singleton */

	public static AggrService getInstance()
	{
		return INSTANCE;
	}

	public static final AggrService INSTANCE =
	  new AggrService();

	protected AggrService()
	{}


	/* Aggregation Service (configuration) */

	protected Aggregator aggregator = new AggregatorsRoot();

	public void setAggregator(Aggregator aggregator)
	{
		this.aggregator = EX.assertn(aggregator);
	}

	/**
	 * Set the number of milliseconds between the scans
	 * for the requests saved to the database.
	 */
	public void setScanPeriod(long sp)
	{
		EX.assertx(sp > 0L);
		this.scanPeriod = sp;
	}

	protected long scanPeriod = 10000L;


	/* Aggregation Service */

	public static void aggr(AggrRequest request)
	{
		//~: save the request
		INSTANCE.post(request);

		//~: notify about the new request
		INSTANCE.self(new AggrEvent().setAggrValue(
		  request.getAggrValue().getPrimaryKey()));
	}

	public void        service(Event e)
	{
		//?: {system is ready}
		if(e instanceof SystemReady)
		{
			schedule();
			return;
		}

		//?: {not our event}
		if(!(e instanceof AggrEvent))
			return;

		//?: {find any event}
		if(((AggrEvent)e).getAggrValue() == null)
			findAllAndSchedule();
		//~: execute concrete value
		else
			aggregateValue(((AggrEvent)e).getAggrValue());
	}


	/* public: aggregation request post routines */

	/**
	 * This call saves the aggregation request given to
	 * the database for further background execution by
	 * the aggregation service.
	 */
	protected void post(AggrRequest request)
	{
		EX.assertn(request.getAggrValue());
		EX.assertn(request.getAggrTask());

		//HINT: aggregation requests for test purposes
		//  still have positive primary key!

		//~: set the primary key
		HiberPoint.setPrimaryKey(TxPoint.txSession(), request);

		//!: do save the object
		TxPoint.txSession().save(request);
	}

	protected void schedule()
	{
		self(new AggrEvent().setEventDelay(scanPeriod));
	}

	/**
	 * Finds all requests in the database and creates
	 * service events for each of them
	 */
	protected void findAllAndSchedule()
	{
		//~: find values of all pending requests
		List<Long> reqs = bean(GetAggrValue.class).
		  getAggrRequests();

		LU.D(getLog(), logsig(), " found [", reqs.size(),
		  "] values with pending requestss");

		//c: create event for each value
		reqs.forEach(this::aggregateValue);
	}

	/**
	 * Tries to execute the given value now, or
	 * schedules the event for the nearest future.
	 */
	protected void aggregateValue(Long aggrValue)
	{
		//~: obtain a lock for this value
		final ValueLock lock = lock(aggrValue);

		//~: try obtain status (0 -> 1)
		if(lock.status.compareAndSet(0, 1))
		{
			//~: send event to itself
			self(new AggrEvent().setAggrValue(aggrValue));
			LU.D(getLog(), logsig(), " planning aggregation of [", aggrValue, "]");
			return;
		}

		//~: try obtain status (1 -> 2)
		if(lock.status.compareAndSet(1, 2)) try
		{
			doAggregateTx(aggrValue);
		}
		finally
		{
			//!: free the lock (2 -> 0)
			lock.status.compareAndSet(2, 0);
		}
	}

	protected void doAggregateTx(Long aggrValue)
	{
		//~: select all the requests for this value

		LU.D(getLog(), logsig(), " aggregating [", aggrValue, "]");
	}


	/* Lock Set */

	/**
	 * Aggregated Value Lock is created in the set and
	 * stays there while the service is active. It allows
	 * only one thread to take the value entity and the
	 * related components for the updating processing.
	 */
	protected static class ValueLock
	{
		public ValueLock(Long aggrValue)
		{
			this.aggrValue = EX.assertn(aggrValue);
		}

		public final Long aggrValue;

		/**
		 * Status values are:
		 *
		 *  0  the lock is not held (free);
		 *  1  event is already added to the queue;
		 *  2  aggregating on the given value.
		 */
		public final AtomicInteger status =
		  new AtomicInteger();
	}

	protected ValueLock lock(Long aggrValue)
	{
		return locks.computeIfAbsent(aggrValue,
		  av -> new ValueLock(av));
	}

	protected final Map<Long, ValueLock> locks =
	  new ConcurrentHashMap<>();
}