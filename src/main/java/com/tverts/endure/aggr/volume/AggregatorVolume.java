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

import com.tverts.support.CMP;
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

	protected void updateAggrValueCreate(AggrStruct struct, AggrItemVolume z)
	{
		//~: current value
		BigDecimal     p = x(aggrValue(struct).getAggrPositive());
		BigDecimal     n = x(aggrValue(struct).getAggrNegative());

		//~: find fixed item on the right
		AggrItemVolume r = findFixedItemRight(struct, z.getOrderIndex());

		//?: {there is no fixed history value on the right}
		if(r == null)
		{
			//?: {item is a fixed history value}
			if(z.isAggrFixed())
			{
				//<: calculate this item deltas

				//~: summarize plain items on the right
				BigDecimal[]   s = summItems(struct, z.getOrderIndex(), Long.MAX_VALUE);

				//~: find left fixed item
				AggrItemVolume l = findFixedItemLeft(struct, z.getOrderIndex());
				BigDecimal     lp, ln, slp, sln;

				//~: left initial value
				if(l == null)
					lp = ln = BigDecimal.ZERO;
				else
				{
					lp = x(l.getAggrPositive());
					ln = x(l.getAggrNegative());
				}

				//~: left summary
				slp = x(p.subtract(lp).subtract(s[0]));
				sln = x(n.subtract(ln).subtract(s[1]));

				//~: z - is the item to insert
				BigDecimal zp = x(z.getAggrPositive());
				BigDecimal zn = x(z.getAggrNegative());

				//~: the deltas
				BigDecimal dp = zp.add(ln).add(sln);
				BigDecimal dn = zn.add(lp).add(slp);

				//~: normalize delta
				BigDecimal d  = dp.subtract(dn);

				if(CMP.greZero(d)) {dp = d; dn = BigDecimal.ZERO;}
				else               {dn = d.negate(); dp = BigDecimal.ZERO;}

				//~: assign it to the item
				z.setVolumePositive(dp);
				z.setVolumeNegative(dn);

				//>: calculate this item deltas

				//~: assign aggregated value
				p = zp.add(s[0]);
				n = zn.add(s[1]);
			}
			//~: this is a plain item
			else
			{
				p = p.add(x(z.getVolumePositive()));
				n = n.add(x(z.getVolumeNegative()));
			}
		}
		//~: update the volume deltas of the fixed items
		else
		{
			//?: {item is a fixed history value}
			if(z.isAggrFixed())
			{
				//~: summarize plain items on the right (till r)
				BigDecimal[]   s = summItems(struct,
				  z.getOrderIndex(), r.getOrderIndex());

				//~: right final value
				BigDecimal rp  = x(r.getAggrPositive());
				BigDecimal rn  = x(r.getAggrNegative());
				BigDecimal drp = x(r.getVolumePositive());
				BigDecimal drn = x(r.getVolumeNegative());

				//~: z - is the item to insert
				BigDecimal zp  = x(z.getAggrPositive());
				BigDecimal zn  = x(z.getAggrNegative());

				//~: the deltas of z
				BigDecimal dzp = zp.add(rn).add(drp).add(s[0]);
				BigDecimal dzn = zn.add(rp).add(drn).add(s[1]);

				BigDecimal d   = dzp.subtract(dzn);

				if(CMP.greZero(d)) {dzp = d; dzn = BigDecimal.ZERO;}
				else               {dzn = d.negate(); dzp = BigDecimal.ZERO;}

				z.setVolumePositive(dzp);
				z.setVolumeNegative(dzn);

				//~: the deltas of right
				drp = rp.add(zn).add(s[1]);
				drn = rn.add(zp).add(s[0]);
				d   = drp.subtract(drn);

				if(CMP.greZero(d)) {drp = d; drn = BigDecimal.ZERO;}
				else               {drn = d.negate(); drp = BigDecimal.ZERO;}

				r.setVolumePositive(drp);
				r.setVolumeNegative(drn);
			}
			//~: this is a plain item
			else
			{
				//~: update the deltas
				BigDecimal dp = x(r.getVolumePositive());
				BigDecimal dn = x(r.getVolumeNegative());

				dp = dp.add(x(z.getVolumeNegative()));
				dn = dn.add(x(z.getVolumePositive()));

				//~: normalize delta
				BigDecimal d  = dp.subtract(dn);

				if(CMP.greZero(d)) {dp = d; dn = BigDecimal.ZERO;}
				else               {dn = d.negate(); dp = BigDecimal.ZERO;}

				//~: assign it to the right fixed item
				r.setVolumePositive(dp);
				r.setVolumeNegative(dn);
			}
		}

		//~: assign the value components
		aggrValue(struct).setAggrPositive(x(p));
		aggrValue(struct).setAggrNegative(x(n));
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
		deleteItems(struct, items);
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
		//c: proceed item-by-item
		for(AggrItem item : items)
		{
			//!: delete that item first
			session(struct).delete(item);
			session(struct).flush();
			session(struct).evict(item);

			//~: link the item for the calculations
			struct.items(items);

			//~: update the aggregated value
			updateAggrValueDelete(struct, (AggrItemVolume) item);
		}
	}

	protected void updateAggrValueDelete(AggrStruct struct, AggrItemVolume z)
	{
		//~: current value
		BigDecimal     p = x(aggrValue(struct).getAggrPositive());
		BigDecimal     n = x(aggrValue(struct).getAggrNegative());

		//~: find fixed item on the right
		AggrItemVolume r = findFixedItemRight(struct, z.getOrderIndex());

		//?: {there is no fixed history value on the right}
		if(r == null)
		{
			//HINT: this equation is valid for both fixed
			//  and plain items!

			p = p.subtract(x(z.getVolumePositive()));
			n = n.subtract(x(z.getVolumeNegative()));
		}
		//~: update the volume deltas of right item
		else
		{
			BigDecimal drp = x(r.getVolumePositive());
			BigDecimal drn = x(r.getVolumeNegative());

			//HINT: this equation is valid for both fixed
			//  and plain items!

			drp = drp.add(x(z.getVolumePositive()));
			drn = drn.add(x(z.getVolumeNegative()));

			BigDecimal d   = drp.subtract(drn);

			if(CMP.greZero(d)) {drp = d; drn = BigDecimal.ZERO;}
			else               {drn = d.negate(); drp = BigDecimal.ZERO;}

			r.setVolumePositive(drp);
			r.setVolumeNegative(drn);
		}

		//HINT: with fixed historical items present, the components
		//  of the aggregated value due to the subtraction may become
		//  sub zero. There is nothing to do here, but to normalize...

		if(!CMP.greZero(p)) {n = n.subtract(p); p = BigDecimal.ZERO;}
		if(!CMP.greZero(n)) {p = p.subtract(n); n = BigDecimal.ZERO;}

		//~: assign the value components
		aggrValue(struct).setAggrPositive(p);
		aggrValue(struct).setAggrNegative(n);
		aggrValue(struct).setAggrValue(p.subtract(n));
	}


	/* protected: aggregation helpers */

	protected AggrItemVolume  findFixedItemLeft(AggrStruct struct, Long orderIndex)
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
		  setLong     ("orderIndex", orderIndex).
		  setMaxResults(1).
		  uniqueResult();

		if((i != null) && !i.isAggrFixed()) throw EX.state(logsig(struct),
		  ": history aggregated item [", i.getPrimaryKey(), "] is not a fixed!");
		return i;
	}

	protected AggrItemVolume  findFixedItemRight(AggrStruct struct, Long orderIndex)
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
		  setLong     ("orderIndex", orderIndex).
		  setMaxResults(1).
		  uniqueResult();

		if((i != null) && !i.isAggrFixed()) throw EX.state(logsig(struct),
		  ": history aggregated item [", i.getPrimaryKey(), "] is not a fixed!");
		return i;
	}

	protected BigDecimal[]    summItems(AggrStruct struct, Long b, Long e)
	{
/*

 select sum(volumePositive), sum(volumeNegative)
 from AggrItem where (aggrValue = :aggrValue) and
   (orderIndex > :b) and (orderIndex < :e) and
   (historyIndex is null)

 */
		Object[] res = (Object[]) aggrItemQ(struct,

"select sum(volumePositive), sum(volumeNegative)\n" +
"from AggrItem where (aggrValue = :aggrValue) and\n" +
"  (orderIndex > :b) and (orderIndex < :e) and\n" +
"  (historyIndex is null)"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong     ("b",          b).
		  setLong     ("e",          e).
		  uniqueResult();

		return new BigDecimal[] {
		  x((BigDecimal) res[0]),
		  x((BigDecimal) res[1])
		};
	}

	private static BigDecimal x(BigDecimal n)
	{
		if(n == null)
			return BigDecimal.ZERO;

		if(BigDecimal.ZERO.compareTo(n) > 0)
			throw EX.state();
		return n;
	}


	/* private: aggregator parameters */

	private boolean ordering;
}