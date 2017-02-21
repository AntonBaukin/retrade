package com.tverts.aggr;

/* Java */

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system (services, tx) */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;
import com.tverts.system.services.events.SystemReady;
import com.tverts.system.tx.TxBean;
import com.tverts.system.tx.TxPoint;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrRequest;
import com.tverts.endure.aggr.AggrValue;
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
	 * Defaults to 10 seconds.
	 */
	public void setScanPeriod(long sp)
	{
		EX.assertx(sp > 0L);
		this.scanPeriod = sp;
	}

	protected long scanPeriod = 10000L;

	/**
	 * Small delay no postpone aggregation request.
	 * It's required to decouple transaction issuing
	 * the aggregation request. Defaults to 1 second.
	 */
	public void setNotifyDelay(long nd)
	{
		EX.assertx(nd > 0L);
		this.notifyDelay = nd;
	}

	protected long notifyDelay = 1000L;


	/* Aggregation Service */

	public static void aggr(AggrRequest request)
	{
		INSTANCE.accept(request);
	}

	/**
	 * Returns the number of pending aggregation requests.
	 * Inserts request for the aggregation.
	 */
	public static int  await()
	{
		int n = bean(GetAggrValue.class).countAggrRequests();

		//?: {has requests} scan
		if(n != 0)
			INSTANCE.scheduleScan(true);

		return n;
	}

	public void        service(Event e)
	{
		//?: {system is ready}
		if(e instanceof SystemReady)
		{
			LU.I(getLog(), "starting periodic scan for the requests");
			startTime = System.currentTimeMillis();

			//!: schedule the first scan
			findAllAndSchedule();
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

	protected volatile long startTime;


	/* public: aggregation request post routines */

	/**
	 * Saves the request and makes notification.
	 * Does this in separate transaction.
	 */
	protected void     accept(AggrRequest request)
	{
		EX.assertn(request);
		EX.assertn(request.getAggrValue());

		bean(TxBean.class).setNew().execute(() ->
		{
			//~: save the request
			save(request);

			//~: notify about the new request
			notifyValue(request.getAggrValue().getPrimaryKey());
		});
	}

	/**
	 * Saves the aggregation request given to the database
	 * for further background execution by the service.
	 */
	protected void     save(AggrRequest request)
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

	/**
	 * Finds all requests in the database and creates
	 * service events for each of them
	 */
	protected void     findAllAndSchedule()
	{
		//~: schedule own invocation
		scheduleScan(false);

		//~: find values of all pending requests
		List<Long> vals = bean(GetAggrValue.class).
		  getAggrRequests();

		if(!vals.isEmpty()) //?: {need to log}
			LU.D(getLog(), "found [", vals.size(), "] pending values");
		//?: {was reported the overall timing}
		else if(aggregated)
		{
			aggregated = false;
			LU.I(getLog(), "completed aggregation in ", LU.td(startTime));
		}

		//c: create event for each value
		vals.forEach(this::notifyValue);
	}

	protected void     scheduleScan(boolean now)
	{
		AggrEvent e = new AggrEvent();
		if(!now) e.setEventDelay(scanPeriod);

		bean(TxBean.class).setNew().execute(() -> self(e));
	}

	/**
	 * Tries to execute the given value now, or
	 * schedules the event for the nearest future.
	 */
	protected boolean  aggregateValue(Long aggrValue)
	{
		//~: obtain a lock for this value
		ValueLock lock = lock(aggrValue);

		//?: {the lock is held}
		if(!lock.busy.compareAndSet(false, true))
			return false;

		try
		{
			aggregated = true;

			//!: invoke aggregation in separated tx
			bean(TxBean.class).setNew().execute(
			  () -> doAggregateTx(aggrValue));
		}
		finally
		{
			//!: release the lock
			lock.busy.set(false);
		}

		return true;
	}

	/**
	 * Used for the logging.
	 */
	protected volatile boolean aggregated;

	protected boolean  notifyValue(Long aggrValue)
	{
		ValueLock lock = lock(aggrValue);

		//?: {the lock is currently busy}
		if(lock.busy.get())
			return false;

		//~: swap notification time
		final long ts = System.currentTimeMillis();
		final long nt = lock.ntime.getAndSet(ts);

		//?: {there was recent notification}
		if(nt + scanPeriod - 100 > ts)
		{
			lock.ntime.compareAndSet(ts, nt);
			return false;
		}

		//~: send notification
		self(new AggrEvent().setAggrValue(aggrValue).
		  setEventDelay(notifyDelay));

		return true;
	}

	/**
	 * Does actual aggregation. Collects all the requests
	 * for the value into a single job and runs it.
	 * Invoked with value-exclusive lock.
	 */
	protected void     doAggregateTx(Long aggrValue)
	{
		GetAggrValue get = bean(GetAggrValue.class);

		//~: get the value
		AggrValue av = get.getAggrValue(aggrValue);

		if(av == null) //?: {value does not exist}
		{
			LU.W(getLog(), "value is not found [", aggrValue, "]!");
			return;
		}

		//~: select all the requests for this value
		List<AggrRequest> reqs = get.getAggrRequests(aggrValue);

		//?: {found nothing}
		if(reqs.isEmpty())
		{
			LU.D(getLog(), "found nothing for [", aggrValue, "]");
			return;
		}

		//~: invoke the requests
		doAggregate(av, reqs);

		//~: remove the requests
		for(AggrRequest r : reqs)
			TxPoint.txSession().remove(r);
	}

	protected void     doAggregate(AggrValue av, List<AggrRequest> reqs)
	{
		LU.D(getLog(), "aggregating [", reqs.size(), "] requests for [",
		  av.getPrimaryKey(), "] of ", av.getAggrType());

		//TODO aggregate requests in a bunch

		//c: for each individual request
		for(AggrRequest r : reqs)
			aggregator.aggregate(AggrJob.create(r));
	}

	protected String   getLog()
	{
		return this.getClass().getName();
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
		 * Flag indicating that the value is locked
		 * for the executed aggregation.
		 */
		public final AtomicBoolean busy =
		  new AtomicBoolean();

		/**
		 * Absolute time of the previous notification.
		 * Pending values that are not busy are repeatedly
		 * notified vith the scan interval.
		 */
		public final AtomicLong ntime =
		  new AtomicLong();
	}

	protected ValueLock lock(Long aggrValue)
	{
		return locks.computeIfAbsent(aggrValue, ValueLock::new);
	}

	protected final Map<Long, ValueLock> locks =
	  new ConcurrentHashMap<>();
}