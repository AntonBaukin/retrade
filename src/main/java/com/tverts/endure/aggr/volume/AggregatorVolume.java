package com.tverts.endure.aggr.volume;

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

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Does aggregation for {@link AggrTaskVolumeCreate} and
 * {@link AggrTaskVolumeDelete} tasks.
 *
 * @author anton.baukin@gmail.com
 */
public class AggregatorVolume extends AggregatorSingleBase
{
	/* public: constructor */

	public AggregatorVolume()
	{
		this(false);
	}

	public AggregatorVolume(boolean ordering)
	{
		setSupportedTasks(

		  AggrTaskVolumeCreate.class,
		  AggrTaskVolumeDelete.class
		);

		setAggrItemClass(AggrItemVolume.class);
		this.ordering = ordering;
	}


	/* public: AggregatorVolume interface */

	public final boolean isOrdering()
	{
		return this.ordering;
	}


	/* protected: AggregatorBase (aggregation) */

	protected void aggregateTask(AggrStruct struct)
	  throws Throwable
	{
		if(struct.task instanceof AggrTaskVolumeCreate)
			aggregateTaskCreate(struct);

		if(struct.task instanceof AggrTaskVolumeDelete)
			aggregateTaskDelete(struct);
	}


	/* protected: aggregate create task */

	protected void aggregateTaskCreate(AggrStruct struct)
	  throws Throwable
	{
		//~: create and save aggregated value item
		AggrItemVolume item = createItem(struct);
		saveItem(struct, item);

		//~: recalculate the aggregated value
		updateAggrValueCreate(struct, item);
	}

	protected AggrItemVolume
	               createItem(AggrStruct struct)
	{
		//?: {the source entity is undefined} do nothing
		if(struct.task.getSourceKey() == null)
			throw EX.state(logsig(struct), ": source is undefined!");


		AggrTaskVolumeCreate task = (AggrTaskVolumeCreate)struct.task;
		AggrItemVolume       item = new AggrItemVolume();

		//~: set item primary key
		setPrimaryKey(session(struct), item,
		  isTestInstance(aggrValue(struct)));

		//~: set aggregated value
		item.setAggrValue(aggrValue(struct));

		//~: set source key
		item.setSourceKey(task.getSourceKey());

		//~: set volume positive
		item.setVolumePositive(task.getVolumePositive());

		//~: set volume negative
		item.setVolumeNegative(task.getVolumeNegative());

		//?: {fixed historical value}
		if(task.isAggrFixed())
		{
			//~: {volume + must be undefined}
			if(item.getVolumePositive() != null)
				throw EX.arg(logsig(struct),
				  ": volume positive is defined for fixed item!");

			//~: {volume - must be undefined}
			if(item.getVolumeNegative() != null)
				throw EX.arg(logsig(struct),
				  ": volume negative is defined for fixed item!");

			//~: {aggregation must be defined}
			if((task.getAggrPositive() == null) && (task.getAggrNegative() == null))
				throw EX.arg(logsig(struct), ": aggregation positive or-and negative ",
				  "is undefined for fixed item!");

			//~: assign the history values
			item.setAggrPositive(task.getAggrPositive());
			item.setAggrNegative(task.getAggrNegative());
			item.setAggrFixed(true);
		}

		//~: set order index
		if(isOrdering())
		{
			setOrderIndex(struct, item);

			//?: {is historical}
			if(task.isAggrFixed())
				item.setHistoryIndex(item.getOrderIndex());
		}
		//?: {fixed historical requested}
		else if(task.isAggrFixed())
			throw EX.state(logsig(struct), ": requested aggregation with ",
			  "fixed historical item, but aggregator doesn't support ordering!");

		//?: {history item is not simultaneously fixed}
		if((item.getHistoryIndex() != null) && !item.isAggrFixed())
			throw EX.state(logsig(struct), ": only fixed item may be historical",
			  " in this implementation!");

		return item;
	}

	protected void saveItem(AggrStruct struct, AggrItemVolume item)
	{
		//~: evict all the aggregated items currently present
		evictAggrItems(struct);

		//!: do save the item
		session(struct).save(item);

		//!: flush the session
		session(struct).flush();

		//!: evict the item
		session(struct).evict(item);

		//~: link the items for the calculations
		struct.items(item);
	}

	protected void updateAggrValueCreate(AggrStruct struct, AggrItemVolume item)
	{
		//~: current value
		BigDecimal p = aggrValue(struct).getAggrPositive();
		BigDecimal n = aggrValue(struct).getAggrNegative();

		if(p == null) p = BigDecimal.ZERO;
		if(n == null) n = BigDecimal.ZERO;

		//~: find fixed item on the right
		AggrItemVolume r = findFixedItemRight(struct, item.getOrderIndex());


		//?: {there is no fixed history value on the right}
		if(r == null)
			//?: {item is a fixed history value} assign it and recalculate
			if(item.isAggrFixed())
			{
				//~: assign
				p = item.getAggrPositive();
				n = item.getAggrNegative();

				if(p == null) p = BigDecimal.ZERO;
				if(n == null) n = BigDecimal.ZERO;

				//~: recalculate plain items on the right
				BigDecimal[] s = summItemsRight(struct, item.getOrderIndex());

				if(s[0] != null)
					p = p.add(s[0]);
				if(s[1] != null)
					n = n.add(s[1]);
			}
			//~: this is a plain item
			else
			{
				if(item.getVolumePositive() != null)
					p = p.add(item.getVolumePositive());

				if(item.getVolumeNegative() != null)
					n = n.add(item.getVolumeNegative());
			}

		//~: assign the value components
		if(p == null) p = BigDecimal.ZERO;
		if(n == null) n = BigDecimal.ZERO;

		aggrValue(struct).setAggrPositive(p);
		aggrValue(struct).setAggrNegative(n);
		aggrValue(struct).setAggrValue(p.subtract(n));
	}


	/* protected: aggregate delete task */

	protected void aggregateTaskDelete(AggrStruct struct)
	  throws Throwable
	{
		//~: find items to delete
		List<AggrItem> items = findItemsToDelete(struct);

		//?: {there are none of them} nothing to do...
		if(items.isEmpty()) return;

		//~: delete them first!
		deleteItems(struct, items);

		//~: recalculate the aggregated value
		updateAggrValueDelete(struct, items);
	}

	protected List<AggrItem>
	               findItemsToDelete(AggrStruct struct)
	{
		//?: {the source entity is undefined} do nothing
		if(struct.task.getSourceKey() == null)
			throw EX.state(logsig(struct), ": source is undefined!");

		//~: evict all the aggregated items currently present
		evictAggrItems(struct);

		//~: load the items of the source
		return loadBySource(struct, struct.task.getSourceKey());
	}

	protected void deleteItems(AggrStruct struct, List<AggrItem> items)
	{
		//!: delete that items first
		for(AggrItem item : items)
			session(struct).delete(item);

		//!: flush the session
		session(struct).flush();

		//!: evict the items
		for(AggrItem item : items)
			session(struct).evict(item);

		//~: link the items for the calculations
		struct.items(items);
	}

	protected void updateAggrValueDelete
	  (AggrStruct struct, List<AggrItem> items)
	{
		//~: current value
		BigDecimal p = aggrValue(struct).getAggrPositive();
		BigDecimal n = aggrValue(struct).getAggrNegative();


		//~: the the most right fixed history value
		AggrItemVolume r = findFixedItemLeft(struct, Long.MAX_VALUE);

		//?: {it exists} take it
		if(r != null)
		{
			p = r.getAggrPositive();
			n = r.getAggrNegative();
		}

		if(p == null) p = BigDecimal.ZERO;
		if(n == null) n = BigDecimal.ZERO;

		//~: effect items on the right of it
		for(AggrItem item : items)
		{
			AggrItemVolume i = (AggrItemVolume)item;

			if(i.getVolumePositive() != null)
				p = p.subtract(i.getVolumePositive());

			if(i.getVolumeNegative() != null)
				n = n.subtract(i.getVolumeNegative());
		}

		if(p == null) p = BigDecimal.ZERO;
		if(n == null) n = BigDecimal.ZERO;

		//~: the component became sub zero
		if((BigDecimal.ZERO.compareTo(p) > 0) || (BigDecimal.ZERO.compareTo(n) > 0))
			throw EX.state( logsig(struct),
			  " delete operation for source [",
			  struct.task.getSourceKey(), "] class ",
			  struct.task.getSourceClass().getSimpleName(),
			  "] made value component to become below zero!"
			);

		aggrValue(struct).setAggrPositive(p);
		aggrValue(struct).setAggrNegative(n);
		aggrValue(struct).setAggrValue(p.subtract(n));
	}


	/* protected: aggregation helpers */

	protected AggrItemVolume findFixedItemLeft(AggrStruct struct, Long orderIndex)
	{

/*

 from AggrItem where (aggrValue = :aggrValue) and
   (historyIndex < :orderIndex)
 order by historyIndex desc

 */
		AggrItemVolume i = (AggrItemVolume) aggrItemQ(struct,

"from AggrItem where (aggrValue = :aggrValue) and\n" +
"  (historyIndex < :orderIndex)\n" +
"order by historyIndex desc"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong("orderIndex", orderIndex).
		  setMaxResults(1).
		  uniqueResult();

		if((i != null) && !i.isAggrFixed()) throw EX.state(logsig(struct),
		  ": history aggregated item [", i.getPrimaryKey(), "] is not a fixed!");
		return i;
	}

	protected AggrItemVolume findFixedItemRight(AggrStruct struct, Long orderIndex)
	{

/*

 from AggrItem where (aggrValue = :aggrValue) and
   (historyIndex > :orderIndex)
 order by historyIndex asc

 */
		AggrItemVolume i = (AggrItemVolume) aggrItemQ(struct,

"from AggrItem where (aggrValue = :aggrValue) and\n" +
"  (historyIndex > :orderIndex)\n" +
"order by historyIndex asc"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong("orderIndex", orderIndex).
		  setMaxResults(1).
		  uniqueResult();

		if((i != null) && !i.isAggrFixed()) throw EX.state(logsig(struct),
		  ": history aggregated item [", i.getPrimaryKey(), "] is not a fixed!");
		return i;
	}

	protected BigDecimal[]   summItemsRight(AggrStruct struct, Long orderIndex)
	{
/*

 select sum(volumePositive), sum(volumeNegative)
 from AggrItem where (aggrValue = :aggrValue) and
   (orderIndex > :orderIndex) and (historyIndex is null)

 */
		Object[] res = (Object[]) aggrItemQ(struct,

"select sum(volumePositive), sum(volumeNegative)\n" +
"from AggrItem where (aggrValue = :aggrValue) and\n" +
"  (orderIndex > :orderIndex) and (historyIndex is null)"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong("orderIndex", orderIndex).
		  uniqueResult();

		return new BigDecimal[] {
		  (BigDecimal) res[0], (BigDecimal) res[1]
		};
	}


	/* private: aggregator parameters */

	private boolean ordering;
}