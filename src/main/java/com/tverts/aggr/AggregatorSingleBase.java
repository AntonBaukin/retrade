package com.tverts.aggr;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: endure (core + aggregation + order) */

import com.tverts.endure.Unity;
import com.tverts.endure.core.GetUnity;
import com.tverts.endure.aggr.AggrItem;
import com.tverts.endure.order.OrderIndex;
import com.tverts.endure.order.OrderPoint;
import com.tverts.endure.order.OrderRequest;

/* com.tverts: support */

import com.tverts.support.EX;
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
		if(aggrItemClass == null) throw EX.state(
		  "Aggregator ", this.getClass().getSimpleName(),
		  " has no aggregation item class defined!"
		);

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
	protected List<AggrItem> loadBySource(AggrStruct struct, long sourceKey)
	{
/*

from AggrItem where
  (aggrValue = :aggrValue) and (sourceKey = :sourceKey)
order by orderIndex asc


*/
		return (List<AggrItem>) aggrItemQ(struct,

"from AggrItem where\n" +
"  (aggrValue = :aggrValue) and (sourceKey = :sourceKey)\n" +
"order by orderIndex asc"

		).
		  setParameter("aggrValue", aggrValue(struct)).
		  setLong     ("sourceKey",  sourceKey).
		  list();
	}

	protected void           setOrderIndex
	  (AggrStruct struct, OrderIndex instance)
	{
		OrderIndex reference = findOrderIndexAggrItemReference(struct);

		setOrderIndex(struct, instance,
		  findOrderIndexAggrItemReference(struct)
		);
	}

	protected void           setOrderIndex
	  (AggrStruct struct, OrderIndex instance, OrderIndex referenceBefore)
	{
		// HINT:  find reference function returns aggr item
		//  to insert before. If such instance exists before-after
		//  flag must be set false.
		//
		//  If the reference item does not exist, this source
		//  instance is currently the last in the order, and we
		//  must insert the item as the last: the flag is true.

		//!: issue order request
		OrderPoint.order(new OrderRequest(instance, referenceBefore).
		  setBeforeAfter(referenceBefore == null)
		);

		if(instance.getOrderIndex() == null)
			throw EX.state(logsig(struct), ": order index is undefined!");
	}

	/**
	 * This implementation finds the aggregation item of the
	 * closest source instance (already having items) in the
	 * order after this source instance. (Set before-after
	 * false to insert before the reference, or as the first.)
	 */
	protected OrderIndex     findOrderIndexAggrItemReference(AggrStruct struct)
	{
		Class sourceClass = findSourceClass(struct);

		if(sourceClass == null) throw EX.state(
		  logsig(struct), ": couldn't find aggregation item source class!"
		);

		//<: load the present order index of the source


// select so.orderIndex from Source so where (so.primaryKey = :sourceKey)

		String Q =
"  select so.orderIndex from Source so where (so.primaryKey = :sourceKey)";

		if(struct.task.getOrderPath() != null)
			Q = Q.replace("so.orderIndex", "so." + struct.task.getOrderPath());

		Number sourceIndex = (Number) Q(struct, Q, "Source", sourceClass).
		  setLong("sourceKey", struct.task.getSourceKey()).
		  uniqueResult();

		//?: {order index does not exist}
		if(sourceIndex == null)
			throw EX.state(sourceClass.getSimpleName(), " instance [",
			  struct.task.getSourceKey(), "] has no order index defined!"
			);

		//>: load the present order index of the source


		// HINT: before-after false means to insert before the
		// source reference, before the component with the
		// minimum order index after the reference.


/*

 select ai from AggrItem ai, Source so where
   (ai.aggrValue = :aggrValue) and
   (so.primaryKey = ai.sourceKey) and
   (so.orderIndex > :sourceIndex)
 order by ai.orderIndex asc

*/
		Q =
"select ai from AggrItem ai, Source so where\n" +
"  (ai.aggrValue = :aggrValue) and\n" +
"  (so.primaryKey = ai.sourceKey) and\n" +
"  (so.orderIndex > :sourceIndex)\n" +
"order by ai.orderIndex asc";

		if(struct.task.getOrderPath() != null)
			Q = Q.replace("so.orderIndex", "so." + struct.task.getOrderPath());

		//~: execute the query
		List list = aggrItemQ(struct, Q, "Source", sourceClass).
		  setParameter("aggrValue",   aggrValue(struct)).
		  setLong     ("sourceIndex", sourceIndex.longValue()).
		  setMaxResults(1).
		  list();

		return (list.isEmpty())?(null):((OrderIndex)list.get(0));
	}

	protected Class          findSourceClass(AggrStruct struct)
	{
		if(struct.task.getSourceClass() != null)
			return struct.task.getSourceClass();

		if(struct.task.getSourceKey() == null)
			throw EX.state();

		Unity u = bean(GetUnity.class).getUnity(
		  struct.task.getSourceKey());

		return (u == null)?(null):(u.getUnityType().getTypeClass());
	}

	protected String         debugSelectIndices(AggrStruct struct)
	{

/*

select primaryKey, orderIndex, sourceKey from AggrItem where
  aggrValue = :aggrValue order by orderIndex

*/
		List list =  aggrItemQ(struct,

"select primaryKey, orderIndex, sourceKey from AggrItem where\n" +
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

	protected String         debugSelectSources(AggrStruct struct)
	{

/*

select so.id, so.orderIndex from AggrItem ai, Source so where
  (ai.aggrValue = :aggrValue) and (so.primaryKey = ai.sourceKey)
order by so.orderIndex

 */

		List list =  aggrItemQ(struct,

"select so.id, so.orderIndex from AggrItem ai, Source so where\n" +
"  (ai.aggrValue = :aggrValue) and (so.primaryKey = ai.sourceKey)\n" +
"order by so.orderIndex",

		  "Source", struct.task.getSourceClass()

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