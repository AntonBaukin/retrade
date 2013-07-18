package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Date;

/* com.tverts: system (txn) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: endure (aggregation + calculations) */

import com.tverts.endure.aggr.AggrItem;
import com.tverts.endure.aggr.calc.AggrCalcs;

/* com.tverts: aggregation (core + calculations) */

import com.tverts.aggr.AggregatorBase.AggrStruct;
import com.tverts.aggr.calc.AggrCalcBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Implementation base for calculators on
 * {@link AggrItemVolume} items with
 * {@link DatePeriodVolumeCalcItem}s.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrCalcDatePeriodVolumeBase
       extends        AggrCalcBase
{
	/* protected: AggrCalcBase interface */

	protected void calc(AggrStruct struct)
	{
		if(aggrTask(struct) instanceof AggrTaskVolumeCreate)
			if(!struct.items().isEmpty())
				calcCreate(struct);

		if(aggrTask(struct) instanceof AggrTaskVolumeDelete)
			if(!struct.items().isEmpty())
				calcDelete(struct);
	}


	/* protected: calculations */

	protected abstract int calcYear(AggrStruct struct);
	protected abstract int calcDay(AggrStruct struct);
	protected abstract int calcLength(AggrStruct struct);

	protected void calcCreate(AggrStruct struct)
	{
		//?: {the source time is not provided}
		if(sourceTime(struct) == null)
			return;

		DatePeriodVolumeCalcItem citem = null;

		for(AggrItem item : struct.items()) if(item instanceof AggrItemVolume)
		{
			//?: {calc item is not loaded yet}
			if(citem == null)
				citem = loadCalcItem(struct);

			//?: {calc item does not exist}
			if(citem == null)
				citem = createCalcItem(struct);

			//!: create the link
			createCalcLink(struct, citem, (AggrItemVolume)item);
		}

		//~: touch the item
		if(citem != null)
			TxPoint.txn(tx(struct), citem);
	}

	protected void calcDelete(AggrStruct struct)
	{
		for(AggrItem item : struct.items()) if(item instanceof AggrItemVolume)
		{
			//~: load the link
			DatePeriodVolumeCalcLink link  =
			  loadCalcLink(struct, (AggrItemVolume)item);

			//?: {not founded}
			if(link == null) throw EX.state(
			  "DatePeriodVolumeCalcLink was not created for AggrItemVolume [",
			  item.getPrimaryKey(), "] in AggrCalc [",
			  aggrCalc(struct).getPrimaryKey(), "] of AggrValue [",
			  aggrValue(struct).getPrimaryKey(), "]!"
			);

			//~: recalculate the item
			if(link.getVolumeNegative() != null)
				link.getCalcItem().setVolumeNegative(
				  link.getCalcItem().getVolumeNegative().subtract(
				    link.getVolumeNegative()));

			if(link.getVolumePositive() != null)
				link.getCalcItem().setVolumePositive(
				  link.getCalcItem().getVolumePositive().subtract(
				    link.getVolumePositive()));

			//!: remove the link
			session(struct).delete(link);

			//~: update the item
			updateCalcItem(struct, link.getCalcItem());
		}
	}

	protected DatePeriodVolumeCalcItem loadCalcItem(AggrStruct struct)
	{
/*

 from DatePeriodVolumeCalcItem where (aggrCalc = :aggrCalc)
   and (year = :year) and (day = :day)

*/

		return (DatePeriodVolumeCalcItem) Q(struct,

"from DatePeriodVolumeCalcItem where (aggrCalc = :aggrCalc)\n" +
"  and (year = :year) and (day = :day)"

		).
		  setParameter("aggrCalc", aggrCalc(struct)).
		  setInteger("year", calcYear(struct)).
		  setInteger("day", calcDay(struct)).
		  uniqueResult();
	}

	protected DatePeriodVolumeCalcLink loadCalcLink
	  (AggrStruct struct, AggrItemVolume aitem)
	{
/*

 from DatePeriodVolumeCalcLink where (aggrItem = :aggrItem)
   and (calcItem.aggrCalc = :aggrCalc)

*/
		return (DatePeriodVolumeCalcLink) Q(struct,

"from DatePeriodVolumeCalcLink where (aggrItem = :aggrItem)\n" +
"  and (calcItem.aggrCalc = :aggrCalc)"

		).
		  setParameter("aggrItem", aitem.getPrimaryKey()).
		  setParameter("aggrCalc", aggrCalc(struct)).
		  uniqueResult();
	}

	protected DatePeriodVolumeCalcItem createCalcItem(AggrStruct struct)
	{
		DatePeriodVolumeCalcItem item =
		  new DatePeriodVolumeCalcItem();

		//~: set primary key
		setPrimaryKey(session(struct), item,
		  isTestInstance(aggrCalc(struct)));

		//~: set transaction number
		TxPoint.txn(tx(struct), item);

		//~: calculation link
		item.setAggrCalc(aggrCalc(struct));

		item.setYear(calcYear(struct));
		item.setDay(calcDay(struct));
		item.setLength(calcLength(struct));

		//~: volumes
		item.setVolumeNegative(BigDecimal.ZERO);
		item.setVolumePositive(BigDecimal.ZERO);

		//!: save the item
		session(struct).save(item);

		return item;
	}

	protected void                     createCalcLink
	  (AggrStruct struct, DatePeriodVolumeCalcItem citem, AggrItemVolume aitem)
	{
		DatePeriodVolumeCalcLink link =
		  new DatePeriodVolumeCalcLink();

		//~: set primary key
		setPrimaryKey(session(struct), link,
		  isTestInstance(citem));

		//~: month calc item reference
		link.setCalcItem(citem);

		//~: copy key of the aggregation item
		link.setAggrItem(aitem.getPrimaryKey());

		//~: the volumes
		link.setVolumeNegative(aitem.getVolumeNegative());
		link.setVolumePositive(aitem.getVolumePositive());


		//!: save it
		session(struct).save(link);


		//~: recalculate the item
		if(link.getVolumeNegative() != null)
			citem.setVolumeNegative(citem.getVolumeNegative().
			  add(link.getVolumeNegative()));

		if(link.getVolumePositive() != null)
			citem.setVolumePositive(citem.getVolumePositive().
			  add(link.getVolumePositive()));
	}

	protected void                     updateCalcItem
	  (AggrStruct struct, DatePeriodVolumeCalcItem item)
	{
		int cn = item.getVolumeNegative().compareTo(BigDecimal.ZERO);
		int cp = item.getVolumePositive().compareTo(BigDecimal.ZERO);

		//?: {the volumes are wrong}
		if((cn < 0) || (cp < 0)) throw EX.state(
		  "DatePeriodVolumeCalcItem [", item.getPrimaryKey(),
		  "] in AggrCalc [", aggrCalc(struct).getPrimaryKey(),
		  "] of AggrValue [", aggrValue(struct).getPrimaryKey(),
		  "] has negative aggregated volumes!"
		);

		//?: {the volumes become zeros} delete the item
		if((cn == 0) && (cp == 0))
			session(struct).delete(item);
		else
			//~: touch the item
			TxPoint.txn(tx(struct), item);
	}


	/* protected: support routines */

	protected Date sourceTime(AggrStruct struct)
	{
		return param(struct, AggrCalcs.PARAM_SOURCE_TIME, Date.class);
	}
}