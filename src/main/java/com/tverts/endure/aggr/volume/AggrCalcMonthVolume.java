package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: endure (aggregation + calculations) */

import com.tverts.endure.aggr.AggrItem;
import com.tverts.endure.aggr.calc.AggrCalcs;

/* com.tverts: aggregation (core + calculations) */

import com.tverts.aggr.AggregatorBase.AggrStruct;
import com.tverts.aggr.calc.AggrCalcBase;


/**
 * Calculator on {@link AggrItemVolume} items.
 * Takes the source timestamp and selects
 * the month of the year. Aggregates the volumes
 * grouped by the months.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrCalcMonthVolume extends AggrCalcBase
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

	protected void calcCreate(AggrStruct struct)
	{
		//?: {the source time is not provided}
		if(sourceTime(struct) == null)
			return;

		MonthVolumeCalcItem citem = null;

		for(AggrItem item : struct.items()) if(item instanceof AggrItemVolume)
		{
			//?: {calc item is not loaded yet}
			if(citem == null)
				citem = loadMonthVolumeCalcItem(struct);

			//?: {calc item does not exist}
			if(citem == null)
				citem = createMonthVolumeCalcItem(struct);

			//!: create the link
			createMonthVolumeCalcLink(struct, citem, (AggrItemVolume)item);
		}
	}

	protected void calcDelete(AggrStruct struct)
	{
		for(AggrItem item : struct.items()) if(item instanceof AggrItemVolume)
		{
			//~: load the link
			MonthVolumeCalcLink link  =
			  loadMonthVolumeCalcLink(struct, (AggrItemVolume)item);

			//?: {not founded}
			if(link == null) throw new IllegalStateException(String.format(
			  "MonthVolumeCalcLink was not created for AggrItemVolume [%d] " +
			  "in AggrCalc [%d] of AggrValue [%d]!",
			  item.getPrimaryKey(), aggrCalc(struct).getPrimaryKey(),
			  aggrValue(struct).getPrimaryKey()
			));

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
			updateMonthVolumeCalcItem(struct, link.getCalcItem());
		}
	}

	protected MonthVolumeCalcItem
	               loadMonthVolumeCalcItem(AggrStruct struct)
	{
		Calendar cl = Calendar.getInstance();
		cl.setTime(sourceTime(struct));

/*

from MonthVolumeCalcItem where (aggrCalc = :aggrCalc)
  and (year = :year) and (month = :month)

 */
		return (MonthVolumeCalcItem) Q(struct,

"from MonthVolumeCalcItem where (aggrCalc = :aggrCalc)\n" +
"  and (year = :year) and (month = :month)"

		).
		  setParameter("aggrCalc", aggrCalc(struct)).
		  setInteger("year", cl.get(Calendar.YEAR)).
		  setInteger("month", cl.get(Calendar.MONTH)).
		  uniqueResult();
	}

	protected MonthVolumeCalcItem
	               createMonthVolumeCalcItem(AggrStruct struct)
	{
		MonthVolumeCalcItem item = new MonthVolumeCalcItem();

		//~: set primary key
		setPrimaryKey(session(struct), item,
		  isTestInstance(aggrCalc(struct)));

		//~: calculation link
		item.setAggrCalc(aggrCalc(struct));

		//~: year + month
		Calendar cl = Calendar.getInstance();
		cl.setTime(sourceTime(struct));

		item.setYear(cl.get(Calendar.YEAR));
		item.setMonth(cl.get(Calendar.MONTH));

		//~: volumes
		item.setVolumeNegative(BigDecimal.ZERO);
		item.setVolumePositive(BigDecimal.ZERO);

		//!: save the item
		session(struct).save(item);

		return item;
	}

	protected void createMonthVolumeCalcLink
	  (AggrStruct struct, MonthVolumeCalcItem citem, AggrItemVolume aitem)
	{
		MonthVolumeCalcLink link = new MonthVolumeCalcLink();

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

	protected MonthVolumeCalcLink
	               loadMonthVolumeCalcLink
	  (AggrStruct struct, AggrItemVolume aitem)
	{
/*

from MonthVolumeCalcLink where (aggrItem = :aggrItem)
  and (calcItem.aggrCalc = :aggrCalc)

 */
		return (MonthVolumeCalcLink) Q(struct,

"from MonthVolumeCalcLink where (aggrItem = :aggrItem)\n" +
"  and (calcItem.aggrCalc = :aggrCalc)"

		).
		  setParameter("aggrItem", aitem.getPrimaryKey()).
		  setParameter("aggrCalc", aggrCalc(struct)).
		  uniqueResult();
	}

	protected void updateMonthVolumeCalcItem
	  (AggrStruct struct, MonthVolumeCalcItem item)
	{
		int cn = item.getVolumeNegative().compareTo(BigDecimal.ZERO);
		int cp = item.getVolumePositive().compareTo(BigDecimal.ZERO);

		//?: {the volumes are wrong}
		if((cn < 0) || (cp < 0))
			throw new IllegalStateException(String.format(
			  "MonthVolumeCalcItem [%d] in AggrCalc [%d] of AggrValue [%d] " +
			  "has negative aggregated volumes!",

			  item.getPrimaryKey(), aggrCalc(struct).getPrimaryKey(),
			  aggrValue(struct).getPrimaryKey()
			));

		//?: {the volumes become zeros} delete the item
		if((cn == 0) && (cp == 0))
			session(struct).delete(item);
	}


	/* protected: support routines */

	protected Date sourceTime(AggrStruct struct)
	{
		return param(struct, AggrCalcs.PARAM_SOURCE_TIME, Date.class);
	}
}