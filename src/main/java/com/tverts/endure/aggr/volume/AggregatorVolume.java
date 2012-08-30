package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.List;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: aggregation */

import com.tverts.aggr.AggregatorSingleBase;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrItem;


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
		//?: {the source entity is undefined} do nothing
		if(struct.task.getSourceKey() == null)
			throw new IllegalArgumentException(
			  "Source is undefined! " + logsig(struct));

		//~: evict all the aggregated items currently present
		evictAggrItems(struct);

		//<: create and save aggregated value item

		AggrTaskVolumeCreate task = (AggrTaskVolumeCreate)struct.task;
		AggrItemVolume       item = new AggrItemVolume();

		//~: set item primary key
		setPrimaryKey(session(struct), item,
		  isTestInstance(aggrValue(struct)));

		//~: set aggregated value
		item.setAggrValue(aggrValue(struct));

		//~: set source ID
		item.setSourceID(task.getSourceKey());

		//~: set volume positive
		item.setVolumePositive(task.getVolumePositive());

		//~: set volume negative
		item.setVolumeNegative(task.getVolumeNegative());

		//~: set order index
		if(isOrdering())
		{
			setOrderIndex(struct, item);

			if(item.getOrderIndex() == null) throw new IllegalStateException(
			  logsig(struct) + ": order index is undefined!");
		}

		//!: do save the item
		session(struct).save(item);

		//!: flush the session
		session(struct).flush();

		//!: evict the item
		session(struct).evict(item);

		//~: link the items for the calculations
		struct.items(item);

		//>: create and save aggregated value item

		//<: update the historical values
		if(isOrdering())
		{

/*

update AggrItem set

  aggrPositive = aggrPositive + :aggrPositive,
  aggrNegative = aggrNegative + :aggrNegative

where (aggrValue = :aggrValue) and (historyIndex > :orderIndex)

*/
			Query query = aggrItemQ(struct,

"update AggrItem set\n" +
"\n" +
"  aggrPositive = aggrPositive + :aggrPositive,\n" +
"  aggrNegative = aggrNegative + :aggrNegative\n" +
"\n" +
"where (aggrValue = :aggrValue) and (historyIndex > :orderIndex)"

			).
			  setParameter("aggrValue",  aggrValue(struct)).
			  setParameter("orderIndex", item.getOrderIndex());

			//?: {has positive addition}
			if(item.getVolumePositive() != null)
				query.setBigDecimal("aggrPositive", item.getVolumePositive());
			else
				query.setBigDecimal("aggrPositive", BigDecimal.ZERO);

			//?: {has negative addition}
			if(item.getVolumeNegative() != null)
				query.setBigDecimal("aggrNegative", item.getVolumeNegative());
			else
				query.setBigDecimal("aggrNegative", BigDecimal.ZERO);

			//!: execute the update
			query.executeUpdate();
		}

		//>: update the historical values

		//<: recalculate the aggregated value

		BigDecimal p = aggrValue(struct).getAggrPositive();
		BigDecimal n = aggrValue(struct).getAggrNegative();

		if(p == null) p = BigDecimal.ZERO;
		if(n == null) n = BigDecimal.ZERO;

		if(item.getVolumePositive() != null)
			p = p.add(item.getVolumePositive());

		if(item.getVolumeNegative() != null)
			n = n.add(item.getVolumeNegative());

		aggrValue(struct).setAggrPositive(p);
		aggrValue(struct).setAggrNegative(n);
		aggrValue(struct).setAggrValue(p.subtract(n));

		//>: recalculate the aggregated value
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

		//<: totals (for aggregated value)
		BigDecimal sumPositive = BigDecimal.ZERO;
		BigDecimal sumNegative = BigDecimal.ZERO;

		for(AggrItem item : items)
		{
			AggrItemVolume vitem = (AggrItemVolume)item;

			//~: add to totals
			if(vitem.getVolumePositive() != null)
				sumPositive = sumPositive.add(vitem.getVolumePositive());

			if(vitem.getVolumeNegative() != null)
				sumNegative = sumNegative.add(vitem.getVolumeNegative());
		}

		//>: totals (for aggregated value)

		//!: delete that items
		for(AggrItem item : items)
			session(struct).delete(item);

		//!: flush the session
		session(struct).flush();

		//!: evict the items
		for(AggrItem item : items)
			session(struct).evict(item);

		//~: link the items for the calculations
		struct.items(items);

		//<: issue update on the historical values

		if(isOrdering())
		{

/*

update AggrItem set

  aggrPositive = aggrPositive - :aggrPositive,
  aggrNegative = aggrNegative - :aggrNegative

where (aggrValue = :aggrValue) and (historyIndex > :orderIndex)

*/

			Query query = aggrItemQ(struct,

"update AggrItem set\n" +
"\n" +
"  aggrPositive = aggrPositive - :aggrPositive,\n" +
"  aggrNegative = aggrNegative - :aggrNegative\n" +
"\n" +
"where (aggrValue = :aggrValue) and (historyIndex > :orderIndex)"

			).
			  setParameter("aggrValue", aggrValue(struct));


			// HINT: we update on each item separately as
			//   the items may be (not in the practice) messed up
			//   with the historical values.

			for(AggrItem item : items)
			{
				if(item.getOrderIndex() == null)
					throw new IllegalStateException(String.format(
					  "%s: item [%d] of Volume aggregated value [%d] " +
					  "has order index undefined!",

					  logsig(struct), item.getPrimaryKey(),
					  aggrValue(struct).getPrimaryKey()
					));

				AggrItemVolume vitem = (AggrItemVolume)item;

				//~: query: positive subtraction
				if(vitem.getAggrPositive() != null)
					query.setBigDecimal("aggrPositive", vitem.getAggrPositive());
				else
					query.setBigDecimal("aggrPositive", BigDecimal.ZERO);

				//~: query: negative subtraction
				if(vitem.getAggrNegative() != null)
					query.setBigDecimal("aggrNegative", vitem.getAggrNegative());
				else
					query.setBigDecimal("aggrNegative", BigDecimal.ZERO);

				//~: query: order index
				query.setLong("orderIndex", vitem.getOrderIndex());

				//!: execute that query
				query.executeUpdate();
			}
		}

		//>: issue update on the historical values

		//<: recalculate the aggregated value

		BigDecimal p = aggrValue(struct).getAggrPositive();
		BigDecimal n = aggrValue(struct).getAggrNegative();

		if(p == null) p = BigDecimal.ZERO;
		if(n == null) n = BigDecimal.ZERO;

		p = p.subtract(sumPositive);
		n = n.subtract(sumNegative);

		aggrValue(struct).setAggrPositive(p);
		aggrValue(struct).setAggrNegative(n);
		aggrValue(struct).setAggrValue(p.subtract(n));

		//>: recalculate the aggregated value
	}


	/* private: aggregator parameters */

	private boolean ordering;
}