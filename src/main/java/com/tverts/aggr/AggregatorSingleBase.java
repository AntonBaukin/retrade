package com.tverts.aggr;

/* standard Java classes */

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: hibery */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrItem;
import com.tverts.endure.aggr.AggrTask;

/* com.tverts: endure (order) */

import com.tverts.endure.order.OrderIndex;
import com.tverts.endure.order.OrderPoint;
import com.tverts.endure.order.OrderRequest;


/**
 * The most of the aggregators do work with a single class
 * of aggregation items. This implementation contains the
 * common shared methods of them.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggregatorSingleBase
       extends        AggregatorBase
{
	/* protected: aggregated items handling */

	protected Class<? extends AggrItem>
	                getAggrItemClass()
	{
		if(aggrItemClass == null)
			throw new IllegalStateException(String.format(
			  "Aggregator %s has no aggregation item class defined!",
			  this.getClass().getSimpleName()
			));

		return aggrItemClass;
	}

	protected void  setAggrItemClass(Class<? extends AggrItem> aggrItemClass)
	{
		this.aggrItemClass = aggrItemClass;
	}

	/**
	 * Finds all the aggregated items in the session persistence
	 * context that are of this aggregated value. If they found,
	 * flashes the session and then evicts them.
	 */
	protected void  evictAggrItems(AggrStruct struct)
	{
		Set items = HiberSystem.getInstance().
		  findAttachedEntities(session(struct), getAggrItemClass());

		//~: remove items not of our interest
		for(Iterator i = items.iterator();(i.hasNext());)
		{
			AggrItem item = (AggrItem)i.next();

			//?: {aggregated value of that item differs}
			if(!aggrValue(struct).equals(item.getAggrValue()))
				i.remove();
		}

		//?: {found no items} quit
		if(items.isEmpty())
			return;

		//!: flush the session
		session(struct).flush();

		//~: evict all the items left
		for(Object item : items)
			session(struct).evict(item);
	}


	/* protected: queries  */

	protected Query aggrItemQ(AggrStruct struct, String hql)
	{
		return Q(struct, hql.
		  replace("AggrItem", getAggrItemClass().getName())
		);
	}

	@SuppressWarnings("unchecked")
	protected List<AggrItem>
	                loadBySource(AggrStruct struct, long sourceID)
	{
/*

from AggrItem where sourceID = :sourceID
order by orderIndex asc


*/
		return (List<AggrItem>) aggrItemQ(struct,

"from AggrItem where sourceID = :sourceID\n" +
"order by orderIndex asc"

		).
		  setLong("sourceID", sourceID).
		  list();
	}

	protected void  setOrderIndex
	  (AggrStruct struct, AggrTask task, OrderIndex instance)
	{

/*

from AggrItem where
  (aggrValue = :aggrValue) and (sourceID = :sourceID)
order by orderIndex desc

from AggrItem where
  (aggrValue = :aggrValue) and (sourceID = :sourceID)
order by orderIndex asc

*/
		OrderIndex reference = null;

		final String Q_MAX =

"from AggrItem where\n" +
"  (aggrValue = :aggrValue) and (sourceID = :sourceID)\n" +
"order by orderIndex desc";

		final String Q_MIN =

"from AggrItem where\n" +
"  (aggrValue = :aggrValue) and (sourceID = :sourceID)\n" +
"order by orderIndex asc";

		//?: {has reference source} try to load it
		if(task.getOrderRefID() != null)
		{
			// before-after true means to insert after the
			// source reference, after the component with the
			// maximum order index;

			// before-after false means to insert before the
			// source reference, before the component with the
			// minimum order index.

			List list = aggrItemQ(struct,(task.isBeforeAfter())?(Q_MAX):(Q_MIN)).
			  setParameter("aggrValue", aggrValue(struct)).
			  setLong     ("sourceID",  task.getOrderRefID()).
			  setMaxResults(1).
			  list();

			if(!list.isEmpty())
				reference = (OrderIndex)list.get(0);
		}

		//!: issue order request
		OrderPoint.order(new OrderRequest(instance, reference).
		  setBeforeAfter(task.isBeforeAfter())
		);
	}


	/* private: aggregation item class */

	private Class<? extends AggrItem> aggrItemClass;
}