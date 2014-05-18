package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: aggregation (core + endure) */

import com.tverts.aggr.AggregatorBase.AggrStruct;
import com.tverts.aggr.calc.AggrCalcBase;
import com.tverts.endure.aggr.AggrItem;
import com.tverts.endure.aggr.volume.AggrItemVolume;
import com.tverts.endure.aggr.volume.AggrTaskVolumeCreate;
import com.tverts.endure.aggr.volume.AggrTaskVolumeDelete;

/* com.tverts: retrade domain (goods + stores + invoices) */

import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.store.StoreGood;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: objects + support */

import com.tverts.objects.StringsSeparated;
import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Special aggregation calculator to support Volume
 * Check Documents. The type of the calculation is
 * {@link Goods#AGGR_CALC_VOL_CHECK}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrCalcVolumeCheck extends AggrCalcBase
{
	/* public: constructor */

	public AggrCalcVolumeCheck()
	{
		setCalcTypes(new StringsSeparated(
		  Goods.AGGR_CALC_VOL_CHECK
		));
	}

	/* protected: calculations */

	protected void calc(AggrStruct struct)
	{
		if(aggrTask(struct) instanceof AggrTaskVolumeCreate)
			if(!struct.items().isEmpty())
				calc(struct, true);

		if(aggrTask(struct) instanceof AggrTaskVolumeDelete)
			if(!struct.items().isEmpty())
				calc(struct, false);
	}


	/* protected: calculations */

	protected void calc(AggrStruct struct, boolean create)
	{
		for(AggrItem item : struct.items())
			if(item instanceof AggrItemVolume)
				calc(struct, create, (AggrItemVolume)item);
	}

	protected void calc(AggrStruct struct, boolean create, AggrItemVolume item)
	{
		//flush(session(struct));

		//~: find fixed item on the right
		AggrItemVolume r = findFixedItemRight(struct, item.getOrderIndex());
		if(r != null) feedback(struct, r);

		//?: {creating & fixed history item}
		if(create && item.isAggrFixed())
			feedback(struct, item);
	}

	/**
	 * Searches for the Invoice being the item source,
	 * checks it's type (must be a Volume Check),
	 * takes the Store Good and changes it's volumes
	 * according to the items.
	 */
	protected void feedback(AggrStruct struct, AggrItemVolume item)
	{
		StoreGood g = findStoreGood(struct, item.getSourceKey(),
		  aggrValue(struct).getSelectorKey()
		);

		//?: {not of our case}
		if(g == null) return;

		//?: {good has illegal state}
		if(g.getVolumeLeft() == null) throw EX.state(
		  "Store Good [", g.getPrimaryKey(), "] of Volume Check (Invoice) [",
		  item.getSourceKey(), "] has volume left undefined!"
		);

		//?: {the actual volumes differs}
		if(g.getVolumeLeft().compareTo(item.getAggrPositive()) != 0) throw EX.state(
		  "Store Good [", g.getPrimaryKey(), "] of Volume Check (Invoice) [",
		  item.getSourceKey(), "] has volume left [", g.getVolumeLeft(),
		  "] not equal to fixed aggregated volume [", item.getAggrPositive(),
		  "] of Aggr Item Volume [", item.getPrimaryKey(), "]!"
		);


		//!: update the store good
		g.setVolumePositive(item.getVolumePositive());
		g.setVolumeNegative(item.getVolumeNegative());

		LU.I(getLog(), "updated Store Good [", g.getPrimaryKey(),
		  "] of Volume Check (Invoice) [", item.getSourceKey(),
		  "] set v+ [", item.getVolumePositive(), "] v- [",
		  item.getVolumeNegative(), "], actual volume is [",
		  g.getVolumeLeft(), "]"
		);
	}


	/* protected: calculations support */

	protected AggrItemVolume findFixedItemRight(AggrStruct struct, Long orderIndex)
	{

/*

 from AggrItemVolume where (aggrValue = :aggrValue) and
   (historyIndex > :orderIndex) and (aggrFixed = true)
 order by historyIndex asc

 */
		return (AggrItemVolume) Q(struct,

"from AggrItemVolume where (aggrValue = :aggrValue) and\n" +
"  (historyIndex > :orderIndex) and (aggrFixed = true)\n" +
"order by historyIndex asc"

		).
		  setParameter("aggrValue",  aggrValue(struct)).
		  setLong     ("orderIndex", orderIndex).
		  setMaxResults(1).
		  uniqueResult();
	}

	protected StoreGood      findStoreGood
	  (AggrStruct struct, Long invoice, Long good)
	{

/*

 select sg from StoreGood sg join sg.invoiceState.invoice i
 where (sg.goodUnit.id = :good) and (i.id = :invoice) and
   (i.invoiceType = :type)

 */

		return (StoreGood) Q(struct,

"select sg from StoreGood sg join sg.invoiceState.invoice i\n" +
"where (sg.goodUnit.id = :good) and (i.id = :invoice) and\n" +
"  (i.invoiceType = :type)"

		).
		  setLong     ("good",    good).
		  setLong     ("invoice", invoice).
		  setParameter("type",    Invoices.typeVolumeCheck()).
		  uniqueResult();
	}
}