package com.tverts.endure.aggr.cost;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.List;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: aggregation */

import com.tverts.aggr.AggregatorSingleBase;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrItem;


/**
 * Does aggregation for {@link AggrTaskRestCostCreate} and
 * {@link AggrTaskRestCostDelete} tasks.
 *
 * @author anton.baukin@gmail.com
 */
public class AggregatorRestCost extends AggregatorSingleBase
{
	/* public: constructor */

	public AggregatorRestCost()
	{
		setSupportedTasks(

		  AggrTaskRestCostCreate.class,
		  AggrTaskRestCostDelete.class
		);

		setAggrItemClass(AggrItemRestCost.class);
	}


	/* protected: AggregatorBase (aggregation) */

	protected void aggregateTask(AggrStruct struct)
	  throws Throwable
	{
		if(struct.task instanceof AggrTaskRestCostCreate)
			aggregateTaskCreate(struct);

		if(struct.task instanceof AggrTaskRestCostDelete)
			aggregateTaskDelete(struct);
	}


	/* protected: aggregate create task */

	protected void aggregateTaskCreate(AggrStruct struct)
	  throws Throwable
	{
		//?: {the source entity is undefined} do nothing
		if(struct.task.getSourceKey() == null)
			throw new IllegalArgumentException(
			  "Source is undefined! " + logsig(struct));

		//~: evict all the aggregated items currently present
		evictAggrItems(struct);

		//<: create and save aggregated value item

		AggrTaskRestCostCreate task = (AggrTaskRestCostCreate)struct.task;
		AggrItemRestCost       item = new AggrItemRestCost();

		//~: set item primary key
		setPrimaryKey(session(struct), item,
		  isTestInstance(aggrValue(struct)));

		//~: set aggregated value
		item.setAggrValue(aggrValue(struct));

		//~: set source ID
		item.setSourceID(task.getSourceKey());

		//~: the good volume
		item.setGoodVolume(task.getGoodVolume());

		//~: the volume cost
		item.setVolumeCost(task.getVolumeCost());

		//~: set order index
		setOrderIndex(struct, task, item);

		//?: {the order index is undefined} illegal state
		if(item.getOrderIndex() == null)
			throw new IllegalStateException(
			  "Order index is undefined! " + logsig(struct));

		//?: {the volume is positive} set historical value
		if(item.getGoodVolume().signum() == 1)
			item.setHistorical(true);

		//!: do save the item
		session(struct).save(item);

		//!: flush the session
		session(struct).flush();

		//!: evict the item
		session(struct).evict(item);

		//>: create and save aggregated value item

		//!: aggregate the items changed
		recalcFromLeft(struct, item.getOrderIndex());
	}


	/* protected: aggregate delete task */

	protected void aggregateTaskDelete(AggrStruct struct)
	  throws Throwable
	{
		//?: {the source entity is undefined} do nothing
		if(struct.task.getSourceKey() == null)
			throw new IllegalArgumentException(
			  "Source is undefined! " + logsig(struct));

		//~: evict all the aggregated items currently present
		evictAggrItems(struct);

		//~: load the items of the source
		List<AggrItem> items = loadBySource(struct, struct.task.getSourceKey());

		//?: {there are none of them} nothing to do...
		if(items.isEmpty())
			return;

		//~: search the smallest order index

		Long orderIndex = null;

		for(AggrItem item : items)
			if(item.getOrderIndex() != null)
				orderIndex = (orderIndex == null)?(item.getOrderIndex()):
				  (orderIndex < item.getOrderIndex())?(orderIndex):
				    (item.getOrderIndex());

		//<: delete and evict those items

		//!: delete that items
		for(AggrItem item : items)
			session(struct).delete(item);

		//!: flush the session
		session(struct).flush();

		//!: evict the items
		for(AggrItem item : items)
			session(struct).evict(item);

		//>: delete and evict those items

		//!: aggregate the items changed
		if(orderIndex != null)
			recalcFromLeft(struct, orderIndex);
	}


	/* protected: recalculation sub-strategy */

	/**
	 * Recalculates the aggregated value starting from
	 * the first historical component on the left of
	 * the given order index.
	 */
	protected void       recalcFromLeft(AggrStruct struct, long orderIndex)
	{
		AggrItemRestCost p, c;
		BigDecimal       z, w;


		//~: take the buy item on the left as previous
		p = (AggrItemRestCost)findLeftItem(struct, orderIndex);

		//?: {found it} take it's aggregation attributes
		if(p != null)
		{
			z = p.getAggrCost();
			w = p.getAggrVolume();

			//?: {those attributes are undefined} take (buy) item attributes
			if((z == null) || (w == null) || (w.signum() != 1))
			{
				z = p.getVolumeCost();
				w = p.getGoodVolume(); //<-- always positive here
			}

			//~: find the current item as the next
			c = (AggrItemRestCost)findRightItem(struct, p.getOrderIndex());
		}
		//!: previous item not found, take the right as the current
		else
		{
			z = w = null;

			//~: find the current item as the global first
			c = (AggrItemRestCost)findFirstItem(struct);
		}

		int B = 0; //<-- session buffer size

		//<: repeat for all buy aggregation items

		while(c != null)
		{
			//?: {accumulated values are still undefined} take them from the current
			if((z == null) || (w == null) || (w.signum() != 1))
			{
				z = c.getVolumeCost();
				w = c.getGoodVolume(); //<-- always positive here

				//~: clear item' aggregation attributes
				c.setAggrCost(null);
				c.setAggrVolume(null);
			}
			else
			{
				BigDecimal s;

				//aggregate sell volumes between the previous and the current items
				s = aggrVolumeBetween(struct, p.getOrderIndex(), c.getOrderIndex());

				//?: {has sell items} subtract their volumes
				if(s != null)
					w = w.add(s); //<-- 's' is negative

				//?: {the volume became negative} clear aggregated attributes
				if(w.signum() != 1)
					z = w = null;
				//!: add current cost and volume
				else
				{
					z = z.add(c.getVolumeCost());
					w = w.add(c.getGoodVolume());
				}

				//~: assign the aggregates
				c.setAggrCost(z);
				c.setAggrVolume(w);
			}

			//!: move to the next item
			p = c;
			c = (AggrItemRestCost)findRightItem(struct, c.getOrderIndex());

			//<: batch the session
			B++; if(B < 48) continue; B = 0;

			//~: flush the session
			session(struct).flush();

			//~: evict all the items except the new current
			evictAggrItems(struct, c);

			//>: batch the session
		}

		//>: repeat for all buy aggregation items

		//<: update the aggregated value

		//?: {the aggregation attributes are not defined}
		if((z == null) || (z.signum() != 1) || (w == null) || (w.signum() != 1))
		{
			z = null;
			w = BigDecimal.ONE;
		}

		//~: assign the aggregated values
		aggrValue(struct).setAggrValue(z);
		aggrValue(struct).setAggrDenom(w);
	}

	protected AggrItem   findLeftItem(AggrStruct struct, long orderIndex)
	{
/*

from AggrItem where
  (aggrValue = :aggrValue) and
  (historyIndex < :orderIndex) and (goodVolume > 0)
order by historyIndex desc

*/

		List list = aggrItemQ(struct,

"from AggrItem where\n" +
"  (aggrValue = :aggrValue) and\n" +
"  (historyIndex < :orderIndex) and (goodVolume > 0)\n" +
"order by historyIndex desc"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong     ("orderIndex", orderIndex).
		  setMaxResults(1).
		  list();

		return (list.isEmpty())?(null):((AggrItem)list.get(0));
	}

	protected AggrItem   findFirstItem(AggrStruct struct)
	{
/*

from AggrItem where
  (aggrValue = :aggrValue) and
  (historyIndex is not null) and (goodVolume > 0)
order by historyIndex asc

*/
		List list = aggrItemQ(struct,

"from AggrItem where\n" +
"  (aggrValue = :aggrValue) and\n" +
"  (historyIndex is not null) and (goodVolume > 0)\n" +
"order by historyIndex asc"

		).
		  setParameter("aggrValue", aggrValue(struct)).
		  setMaxResults(1).
		  list();

		return (list.isEmpty())?(null):((AggrItem)list.get(0));
	}

	protected AggrItem   findRightItem(AggrStruct struct, long orderIndex)
	{
/*

from AggrItem where
  (aggrValue = :aggrValue) and
  (historyIndex > :orderIndex) and (goodVolume > 0)
order by historyIndex asc

*/
		List list = aggrItemQ(struct,

"from AggrItem where\n" +
"  (aggrValue = :aggrValue) and\n" +
"  (historyIndex > :orderIndex) and (goodVolume > 0)\n" +
"order by historyIndex asc"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong     ("orderIndex", orderIndex).
		  setMaxResults(1).
		  list();

		return (list.isEmpty())?(null):((AggrItem)list.get(0));
	}

	/**
	 * Aggregates the volume of the items between the two
	 * items defined by their indices. The border items
	 * are excluded from the selection.
	 */
	protected BigDecimal aggrVolumeBetween
	  (AggrStruct struct, long leftIndex, long rightIndex)
	{

/*

select sum(goodVolume) from AggrItem where
  (aggrValue = :aggrValue) and
  (orderIndex > :leftIndex) and (orderIndex < :rightIndex)

*/
		return (BigDecimal) aggrItemQ(struct,

"select sum(goodVolume) from AggrItem where\n" +
"  (aggrValue = :aggrValue) and\n" +
"  (orderIndex > :leftIndex) and (orderIndex < :rightIndex)"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong     ("leftIndex",  leftIndex).
		  setLong     ("rightIndex", rightIndex).
		  uniqueResult();
	}
}