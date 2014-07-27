package com.tverts.retrade.domain.prices;

/* standard Java classes */

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: transactions */

import com.tverts.system.tx.SetTxAction;

/* com.tverts: endure (core) */

import com.tverts.endure.core.ActUnity;

/* com.tverts: retrade domain (core + goods) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.goods.GetGoods;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Actions builder for {@link PriceList} instances.
 * Save and ensure actions are supported.
 *
 * @author anton.baukin@gmail.com
 */
public class ActPriceList extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType UPDATE =
	  ActionType.UPDATE;

	public static final ActionType ENSURE =
	  ActionType.ENSURE;


	/* parameters of the action task */

	/**
	 * This parameter provides a collection of
	 * {@link GoodPrice} instances for UPDATE
	 * operation. These instances are merged
	 * into the database.
	 */
	public static final String PRICES     =
	  ActPriceList.class.getName() + ": prices";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			savePriceList(abr);

		if(UPDATE.equals(actionType(abr)))
			updatePriceList(abr);

		if(ENSURE.equals(actionType(abr)))
			ensurePriceList(abr);
	}


	/* protected: action methods */

	protected void savePriceList(ActionBuildRec abr)
	{
		//?: {target is not a PriceList}
		checkTargetClass(abr, PriceList.class);

		//~: save the price list
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set store unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr));

		complete(abr);
	}

	protected void updatePriceList(ActionBuildRec abr)
	{
		//?: {target is not a PriceList}
		checkTargetClass(abr, PriceList.class);

		//~: update the Txn
		chain(abr).first(new SetTxAction(task(abr)));

		//~: merge the prices
		mergePrices(abr);

		complete(abr);
	}

	protected void ensurePriceList(ActionBuildRec abr)
	{
		//?: {target is not a PriceList}
		checkTargetClass(abr, PriceList.class);

		complete(abr);
	}


	/* protected: support */

	@SuppressWarnings("unchecked")
	protected void mergePrices(ActionBuildRec abr)
	{
		//~: get the prices parameter
		Collection<GoodPrice> source = (Collection<GoodPrice>)
		  param(abr, PRICES, Collection.class);
		if((source == null) || source.isEmpty())
			return;

		//~: check the values
		for(GoodPrice gp : source)
		{
			EX.assertn( gp.getPrice(),
			  "Updating Good Price [", gp.getPrimaryKey(), "] of List [",
			  target(abr, PriceList.class).getPrimaryKey(), "] has value undefined!"
			);

			EX.assertx( CMP.grZero(gp.getPrice()),
			  "Updating Good Price [", gp.getPrimaryKey(), "] of List [",
			  target(abr, PriceList.class).getPrimaryKey(), "] has illegal value!"
			);
		}

		//~: load & map the existing prices
		List<GoodPrice> existing = bean(GetGoods.class).
		  getPriceListPrices(target(abr, PriceList.class));

		Map<Long, GoodPrice> emap =
		  new HashMap<Long, GoodPrice>(existing.size());
		for(GoodPrice gp : existing)
			emap.put(gp.getGoodUnit().getPrimaryKey(), gp);

		//c: for all the source goods
		for(GoodPrice s : source)
			//?: {update the exiting prices}
			if(emap.containsKey(s.getGoodUnit().getPrimaryKey()))
			{
				GoodPrice d = emap.get(s.getGoodUnit().getPrimaryKey());

				//?: {the prices are equal} skip
				if(d.getPrice().compareTo(s.getPrice()) == 0)
					continue;

				//!: update the price
				d.setPrice(s.getPrice());
				chain(abr).first(new SetTxAction(task(abr), d));
			}
			//!: insert new prices
			else
			{
				//~: price list
				s.setPriceList(target(abr, PriceList.class));

				//!: save the price
				chain(abr).first(new SaveNumericIdentified(task(abr), s));
			}

		//HINT: in this method the prices are not removed!
	}
}