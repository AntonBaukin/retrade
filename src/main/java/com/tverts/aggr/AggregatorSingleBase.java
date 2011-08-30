package com.tverts.aggr;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collection;
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

/* com.tverts: support */

import com.tverts.support.SU;


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
	protected void  evictAggrItems(AggrStruct struct, Collection except)
	{
		Set items = HiberSystem.getInstance().
		  findAttachedEntities(session(struct), getAggrItemClass());

		if((except != null) && except.isEmpty())
			except = null;

		//~: remove items not of our interest
		for(Iterator i = items.iterator();(i.hasNext());)
		{
			AggrItem item = (AggrItem)i.next();

			//?: {aggregated value of that item differs}
			if(!aggrValue(struct).equals(item.getAggrValue()))
			{
				i.remove();
				continue;
			}

			//?: {the item is in except list}
			if((except != null) && except.contains(item))
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

	protected void  evictAggrItems(AggrStruct struct, Object... except)
	{
		evictAggrItems(struct, Arrays.asList(except));
	}


	/* protected: queries + order index handling */

	protected Query          aggrItemQ
	  (AggrStruct struct, String hql, Object... replaces)
	{
		hql = SU.replace(hql, " AggrItem ",
		  SU.cat(" ", getAggrItemClass().getSimpleName(), " "));

		return Q(struct, hql, replaces);
	}

	@SuppressWarnings("unchecked")
	protected List<AggrItem> loadBySource(AggrStruct struct, long sourceID)
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

	protected void           setOrderIndex
	  (AggrStruct struct, AggrTask task, OrderIndex instance)
	{
		OrderIndex reference   = findOrderIndexAggrItemReference(struct, task);
		boolean    beforeAfter = task.isBeforeAfter();

		//HINT: here we invert before-after flag when the reference
		//   is not found. This is correct, if you check it on an axis...

		if(reference == null)
			beforeAfter = !beforeAfter;

		//!: issue order request
		OrderPoint.order(new OrderRequest(instance, reference).
		  setBeforeAfter(beforeAfter)
		);
	}

	protected OrderIndex     findOrderIndexAggrItemReference
	  (AggrStruct struct, AggrTask task)
	{
		//?: {has reference source} do nothing
		if((task.getOrderKey() == null) || (task.getSourceClass() == null))
			return null;

		//<: load the present order index of the source

/*

select orderIndex from Source where (primaryKey = :orderKey)

*/
		Number sourceIndex = (Number) Q (struct,

"select orderIndex from Source where (primaryKey = :orderKey)",

		  "Source", task.getSourceClass()

		).
		  setLong("orderKey", task.getOrderKey()).
		  uniqueResult();

		//?: {order index does not exist}
		if(sourceIndex == null)
			return null;

		//>: load the present order index of the source

/*

select ai from AggrItem ai, Source so where
  (ai.aggrValue = :aggrValue) and (so.primaryKey = ai.sourceID) and
  (so.orderIndex <= :sourceIndex)
order by ai.orderIndex desc

select ai from AggrItem ai, Source so where
  (ai.aggrValue = :aggrValue) and (so.primaryKey = ai.sourceID) and
  (so.orderIndex >= :sourceIndex)
order by ai.orderIndex asc

*/

		// HINT: before-after true means to insert after the
		// source reference, after the component with the
		// maximum order index before-equal the reference.

		final String Q_MAX =

"select ai from AggrItem ai, Source so where\n" +
"  (ai.aggrValue = :aggrValue) and (so.primaryKey = ai.sourceID) and\n" +
"  (so.orderIndex <= :sourceIndex)\n" +
"order by ai.orderIndex desc";


		// HINT: before-after false means to insert before the
		// source reference, before the component with the
		// minimum order index after-equal the reference.

		final String Q_MIN =

"select ai from AggrItem ai, Source so where\n" +
"  (ai.aggrValue = :aggrValue) and (so.primaryKey = ai.sourceID) and\n" +
"  (so.orderIndex >= :sourceIndex)\n" +
"order by ai.orderIndex asc";


		//~: execute the query
		List list = aggrItemQ(struct,

		  (task.isBeforeAfter())?(Q_MAX):(Q_MIN),
		  "Source", task.getSourceClass()

		).
		  setParameter("aggrValue",   aggrValue(struct)).
		  setLong     ("sourceIndex", sourceIndex.longValue()).
		  setMaxResults(1).
		  list();

		return (list.isEmpty())?(null):((OrderIndex)list.get(0));
	}

	protected String         debugSelectIndices(AggrStruct struct)
	{

/*

select primaryKey, orderIndex, sourceID from AggrItem where
  aggrValue = :aggrValue order by orderIndex

*/
		List list =  aggrItemQ(struct,

"select primaryKey, orderIndex, sourceID from AggrItem where\n" +
"  aggrValue = :aggrValue order by orderIndex"

		).
		  setParameter("aggrValue",   aggrValue(struct)).
		  setMaxResults(100).
		  list();

		StringBuilder s = new StringBuilder(512);

		for(Object row : list)
		{
			String pk = ((Object[])row)[0].toString().replace("-", "");
			String so = ((Object[])row)[2].toString().replace("-", "");

			String oi = ((Object[])row)[1].toString();
			if(oi.indexOf('-') == -1) oi = "+" + oi;

			s.append((s.length() != 0)?("  "):("")).
			  append(pk).append('@').append(so).append(oi);
		}

		return s.toString();
	}

	protected String         debugSelectSources(AggrStruct struct, AggrTask task)
	{

/*

select so.id, so.orderIndex from AggrItem ai, Source so where
  (ai.aggrValue = :aggrValue) and (so.primaryKey = ai.sourceID)
order by so.orderIndex

 */

		List list =  aggrItemQ(struct,

"select so.id, so.orderIndex from AggrItem ai, Source so where\n" +
"  (ai.aggrValue = :aggrValue) and (so.primaryKey = ai.sourceID)\n" +
"order by so.orderIndex",

		  "Source", task.getSourceClass()

		).
		  setParameter("aggrValue",   aggrValue(struct)).
		  setMaxResults(100).
		  list();

		StringBuilder s = new StringBuilder(512);

		for(Object row : list)
		{
			String pk = ((Object[])row)[0].toString().replace("-", "");
			String oi = ((Object[])row)[1].toString();
			if(oi.indexOf('-') == -1) oi = "+" + oi;

			s.append((s.length() != 0)?("  "):("")).
			  append(pk).append(oi);
		}

		return s.toString();
	}


	/* private: aggregation item class */

	private Class<? extends AggrItem> aggrItemClass;
}