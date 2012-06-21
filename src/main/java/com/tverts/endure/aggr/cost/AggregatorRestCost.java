package com.tverts.endure.aggr.cost;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: aggregation */

import com.tverts.aggr.AggregatorSingleBase;

/* com.tverts: endure (core + aggregation) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.aggr.AggrItem;

/* com.tverts: support */

import com.tverts.support.LU;


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
		setOrderIndex(struct, item);

		//?: {the order index is undefined} illegal state
		if(item.getOrderIndex() == null)
			throw new IllegalStateException(logsig(struct) +
			  ": order index is undefined!");

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

		//~: recalculate the sell delta volumes
		if(item.isHistorical()) //<-- this is buy item
			recalcDeltaCenter(struct, item.getOrderIndex());
		else
			recalcDeltaRight(struct, item.getOrderIndex());

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

		//~: search for the smallest order index
		Long orderIndex = null;

		for(AggrItem item : items)
		{
			if(item.getOrderIndex() == null)
				throw new IllegalStateException();

			if((orderIndex == null) || (orderIndex > item.getOrderIndex()))
				orderIndex = item.getOrderIndex();
		}

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

		//~: recalculate the sell delta volumes
		for(AggrItem item : items)
			recalcDeltaRight(struct, item.getOrderIndex());

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
		//debug(struct, "==== recalc (", aggrValue(struct), ", ", orderIndex, ")");

		AggrItemRestCost p, c;
		BigDecimal       s, w;

		//~: take the buy item on the left as previous
		p = (AggrItemRestCost)findLeftItem(struct, orderIndex);
		//debug(struct, "p = ", p);

		//?: {found it} take it's aggregation attributes
		if(p != null)
		{
			s = (p.getRestCost() == null)?(null):(new BigDecimal(p.getRestCost()));
			w = p.getAggrVolume();
			//debug(struct, "(p != null), s = ", s, " w = ", w);

			//?: {those attributes are undefined} take (buy) item attributes
			if((s == null) || (w == null) || (w.signum() != 1))
			{
				w = p.getGoodVolume(); //<-- always positive here
				if((w == null) || (w.signum() != 1))
					throw new IllegalStateException();
				s = p.getVolumeCost().divide(w);
				//debug(struct, "(p != null) & (s == null), s = ", s, " w = ", w);
			}

			//~: find the current item as the next
			checkHistoryIndex(p);
			c = (AggrItemRestCost)findRightItem(struct, p.getHistoryIndex());
			//debug(struct, "(p != null), c = ", c);
		}
		//!: previous item not found, take the right as the current
		else
		{
			s = w = null;

			//~: find the current item as the global first
			c = (AggrItemRestCost)findFirstItem(struct);
			//debug(struct, "(p == null), c = ", c);
		}

		//?: {there are no buy items present}
		if(c == null)
		{
			//?: {there are no buy items at all} the value is undefined
			if(p == null)
			{
				setAggrValue(struct, null);
				//debug(struct, "(c == null) & (p == null), c = ", c);
			}
			else
			{
				//~: here s == previous item' good cost
				if(s == null) throw new IllegalArgumentException();
				setAggrValue(struct, s);
				//debug(struct, "(c == null) & (p != null), s = ", s);
			}

			return;
		}


		List<AggrItemRestCost> batch =
		  new ArrayList<AggrItemRestCost>(64);

		List rights = selectRightItems(struct, c.getHistoryIndex());
		//debug(struct, " right items: ", rights);

		//c: for all the buy items on the right
		for(Object id : rights)
		{
			//~: load the current item
			c = (AggrItemRestCost) session(struct).
			  load(AggrItemRestCost.class, (Long)id);

			//~: it must be for buy items
			checkHistoryIndex(c);

			//?: {accumulated value is still undefined} take it from the current
			if(s == null)
			{
				w = c.getGoodVolume(); //<-- always positive here
				if((w == null) || (w.signum() != 1))
					throw new IllegalStateException();
				s = c.getVolumeCost().divide(w);

				//~: clear item' aggregation attributes
				c.setRestCost(null); c.setAggrVolume(null);
				//debug(struct, "(s == null), c = ", c, " cleared! s = ", s, ", w = ", w);
			}
			else
			{
				//debug(struct, "(s != null), c = ", c, ", d = ", c.getDeltaVolume());

				//~: delta volume = sell volumes of sell item, it is negative!
				if(c.getDeltaVolume() != null)
				{
					//debug(struct, "w = ", w, ", w + d = ", w.add(c.getDeltaVolume()));
					w = w.add(c.getDeltaVolume());
				}

				//~: the volume become negative
				if(w.signum() != 1)
				{
					w = null;
					//debug(struct, "(w <= 0), w = null");
				}

				if(w == null)
				{
					s = null; //<-- clear the rest cost value
					//debug(struct, "(w == null), s = null");
				}
				else
				{
					//~: buy volumes are always positive
					if(c.getGoodVolume().signum() < 0)
						throw new IllegalStateException();
					if(c.getVolumeCost().signum() < 0)
						throw new IllegalStateException();

					BigDecimal w1 = w.add(c.getGoodVolume());
					//debug(struct, "v = ", c.getGoodVolume(), ", w + v = ", w1);

					s = s.multiply(w).add(c.getVolumeCost());
					s = s.divide(w1, 64, BigDecimal.ROUND_HALF_UP);
					w = w1;

					//~: make the precision < 64 (to store as string with '.')
					s = roundCostValue(s);
				}

				//!: update this (buy) aggregation item
				c.setRestCost((s == null)?(null):(s.toString()));
				c.setAggrVolume(w);
				//debug(struct, "c.cost = ", s, ", c.volume = ", w);

				//<: batch the session
				batch.add(c); if(batch.size() < 128) continue;
				//debug(struct, "batched!");

				//~: flush the session
				session(struct).flush();

				//~: evict all the items batched
				for(AggrItemRestCost i : batch)
					session(struct).evict(i);
				batch.clear();

				//>: batch the session
			}
		}

		//!: write the results
		setAggrValue(struct, s);

		//~: flush the session
		session(struct).flush();

		//~: evict all the items batched
		for(AggrItemRestCost i : batch)
			session(struct).evict(i);
	}

	protected AggrItem   findLeftItem(AggrStruct struct, long orderIndex)
	{
/*

from AggrItem where
  (aggrValue = :aggrValue) and (historyIndex < :orderIndex)
order by historyIndex desc

*/

		List list = aggrItemQ(struct,

"from AggrItem where\n" +
"  (aggrValue = :aggrValue) and (historyIndex < :orderIndex)\n" +
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
  (aggrValue = :aggrValue) and (historyIndex is not null)
order by historyIndex asc

*/
		List list = aggrItemQ(struct,

"from AggrItem where\n" +
"  (aggrValue = :aggrValue) and (historyIndex is not null)\n" +
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
  (aggrValue = :aggrValue) and (historyIndex > :orderIndex)
order by historyIndex asc

*/
		List list = aggrItemQ(struct,

"from AggrItem where\n" +
"  (aggrValue = :aggrValue) and (historyIndex > :orderIndex)\n" +
"order by historyIndex asc"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong     ("orderIndex", orderIndex).
		  setMaxResults(1).
		  list();

		return (list.isEmpty())?(null):((AggrItem)list.get(0));
	}

	/**
	 * Returns ids of all the items with defined history index
	 * greater or equal to the given one.
	 */
	protected List       selectRightItems(AggrStruct struct, long orderIndex)
	{
/*

select id from AggrItem where
  (aggrValue = :aggrValue) and (historyIndex >= :orderIndex)
order by historyIndex asc

*/
		return aggrItemQ(struct,

"select id from AggrItem where\n" +
"  (aggrValue = :aggrValue) and (historyIndex >= :orderIndex)\n" +
"order by historyIndex asc"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong     ("orderIndex", orderIndex).
		  list();
	}

	protected void       recalcDeltaCenter(AggrStruct struct, long orderIndex)
	{
		//debug(struct, "==== recalc delta center(", aggrValue(struct), ", ", orderIndex, ")");

		AggrItemRestCost r = (AggrItemRestCost)findRightItem(struct, orderIndex);
		if(r != null) checkHistoryIndex(r);

		AggrItemRestCost l = (AggrItemRestCost)findLeftItem(struct, orderIndex);
		if(l != null) checkHistoryIndex(l);

		AggrItemRestCost c = null;

		if(r != null)
			c = (AggrItemRestCost)findLeftItem(struct, r.getHistoryIndex());
		else if(l != null)
			c = (AggrItemRestCost)findRightItem(struct, l.getHistoryIndex());

		//debug(struct, "l = ", l, ", c = ", c, ", r = ", r);

		//~: this is mostly not possible when correct usage
		if((c == l) | (c == r)) c = null;

		//~: recalculate delta of the right item
		if(r != null)
		{
			if(c != null)
				r.setDeltaVolume(summSellVolumesBetween(struct,
				  c.getHistoryIndex(), r.getHistoryIndex()));
			else if(l != null)
				r.setDeltaVolume(summSellVolumesBetween(struct,
				  l.getHistoryIndex(), r.getHistoryIndex()));
			else
				r.setDeltaVolume(summSellVolumesTill(struct,
				  r.getHistoryIndex()));

			//debug(struct, "delta r = ", r.getDeltaVolume());
		}

		//~: recalculate delta of the center item
		if(c != null)
		{
			if(l != null)
				c.setDeltaVolume(summSellVolumesBetween(struct,
				  l.getHistoryIndex(), c.getHistoryIndex()));
			else
				c.setDeltaVolume(summSellVolumesTill(struct,
				  c.getHistoryIndex()));

			//debug(struct, "delta c = ", c.getDeltaVolume());
		}

		//~: flash the session
		session(struct).flush();
		if(l != null) session(struct).evict(l);
		if(c != null) session(struct).evict(c);
		if(r != null) session(struct).evict(r);
	}

	protected void       recalcDeltaRight(AggrStruct struct, long orderIndex)
	{
		//debug(struct, "==== recalc delta right(", aggrValue(struct), ", ", orderIndex, ")");

		AggrItemRestCost r = (AggrItemRestCost)findRightItem(struct, orderIndex);
		if(r != null) checkHistoryIndex(r);

		if(r == null)
		{
			//debug(struct, "r = null");
			return;
		}

		AggrItemRestCost l = (AggrItemRestCost)
		  findLeftItem(struct, r.getHistoryIndex());
		//debug(struct, "r = ", r, ", l = ", l);

		//~: recalculate the delta volume in range [p, c]

		if(l == null)
			r.setDeltaVolume(summSellVolumesTill(struct,
			  r.getHistoryIndex()));
		else
			r.setDeltaVolume(summSellVolumesBetween(struct,
			  l.getHistoryIndex(), r.getHistoryIndex()));
		//debug(struct, "delta = ", r.getDeltaVolume());

		//~: flash the session
		session(struct).flush();
		if(l != null) session(struct).evict(l);
		session(struct).evict(r);
	}

	/**
	 * Aggregates the volume of sell items between the two
	 * items defined by their indices. The border items
	 * are excluded from the selection.
	 */
	protected BigDecimal  summSellVolumesBetween
	  (AggrStruct struct, long leftIndex, long rightIndex)
	{

/*

select sum(goodVolume) from AggrItem where
  (aggrValue = :aggrValue) and (goodVolume < 0) and
  (orderIndex > :leftIndex) and (orderIndex < :rightIndex)

*/
		return (BigDecimal) aggrItemQ(struct,

"select sum(goodVolume) from AggrItem where\n" +
"  (aggrValue = :aggrValue) and (goodVolume < 0) and\n" +
"  (orderIndex > :leftIndex) and (orderIndex < :rightIndex)"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong     ("leftIndex",  leftIndex).
		  setLong     ("rightIndex", rightIndex).
		  uniqueResult();
	}

	protected BigDecimal  summSellVolumesTill
	  (AggrStruct struct, long rightIndex)
	{

/*

select sum(goodVolume) from AggrItem where
  (aggrValue = :aggrValue) and (goodVolume < 0) and
  (orderIndex < :rightIndex)

*/
		return (BigDecimal) aggrItemQ(struct,

"select sum(goodVolume) from AggrItem where\n" +
"  (aggrValue = :aggrValue) and (goodVolume < 0) and\n" +
"  (orderIndex < :rightIndex)"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong     ("rightIndex", rightIndex).
		  uniqueResult();
	}

	protected void        setAggrValue(AggrStruct struct, BigDecimal s)
	{
		if(s != null)
			s = s.setScale(5, BigDecimal.ROUND_HALF_UP);
		aggrValue(struct).setAggrValue(s);

		//~: the denominator is always 1
		aggrValue(struct).setAggrDenom(BigDecimal.ONE);
	}

	protected void        checkHistoryIndex(AggrItemRestCost item)
	{
		if((item.getHistoryIndex() == null) ||
		   !item.getHistoryIndex().equals(item.getOrderIndex())
		  )
			throw new IllegalStateException("AggrItemRestCost [" +
			  item.getPrimaryKey() + "] has wrong history index!");
	}

	protected BigDecimal  roundCostValue(BigDecimal v)
	{
		int p = v.precision();
		if(p <= 63) return v;

		int s = v.scale() - (p - 63);
		if(s < 0) throw new IllegalStateException("Decimal value [" +
		  v.toString() + "] is too large for Rest cost storage!");

		return v.setScale(s, BigDecimal.ROUND_HALF_UP);
	}

	private void          debug(AggrStruct s, Object... msgs)
	{
		//if(aggrValue(s).getPrimaryKey() != -211L) return;

		for(int i = 0;(i < msgs.length);i++)
		{
			if(msgs[i] == null) msgs[i] = "null";
			if(msgs[i] instanceof NumericIdentity)
				msgs[i] = ((NumericIdentity)msgs[i]).getPrimaryKey();
		}

		LU.D(this.getClass().getName(), msgs);
	}

//	private static int    debugInvokeShuntI;
//
//	@SuppressWarnings("unchecked")
//	private void          debugInvokeShunt(AggrStruct struct)
//	{
//		try
//		{
//			Class shunt = Thread.currentThread().getContextClassLoader().loadClass(
//			  "com.tverts.retrade.domain.invoice.shunts.ShuntInvoicesAggr");
//
//			shunt.getMethod("testRestCostsItemsOrder").
//			  invoke(shunt.newInstance());
//
//			debugInvokeShuntI++;
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(String.format(
//			  "Order breaked in call %d for invoice %d!",
//			  debugInvokeShuntI, struct.task.getSourceKey()
//			));
//		}
//	}
}