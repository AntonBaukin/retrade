package com.tverts.retrade.domain.prices;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: transactions */

import com.tverts.system.tx.SetTxAction;

/* com.tverts: endure (core) */

import com.tverts.endure.core.ActUnity;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.ActionBuilderReTrade;


/**
 * Actions builder for {@link PriceListEntity}
 * instances. Save and ensure actions are supported.
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
		//?: {target is not a Price List}
		checkTargetClass(abr, PriceListEntity.class);

		//~: save the price list
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set store unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr));

		complete(abr);
	}

	protected void updatePriceList(ActionBuildRec abr)
	{
		//?: {target is not a Price List}
		checkTargetClass(abr, PriceListEntity.class);

		//~: update the Txn
		chain(abr).first(new SetTxAction(task(abr)));

		complete(abr);
	}

	protected void ensurePriceList(ActionBuildRec abr)
	{
		//?: {target is not a Price List}
		checkTargetClass(abr, PriceListEntity.class);

		complete(abr);
	}

	/*
	@SuppressWarnings("unchecked")
	protected void mergePrices(ActionBuildRec abr)
	{
		//~: get the prices parameter
		Collection<GoodPrice> src = (Collection<GoodPrice>)
		  param(abr, PRICES, Collection.class);
		if((src == null) || src.isEmpty())
			return;

		//~: check the values
		for(GoodPrice gp : src)
		{
			EX.assertn( gp.getPrice(),
			  "Updating Good Price [", gp.getPrimaryKey(), "] of List [",
			  target(abr, PriceListEntity.class).getPrimaryKey(), "] has value undefined!"
			);

			EX.assertx( CMP.grZero(gp.getPrice()),
			  "Updating Good Price [", gp.getPrimaryKey(), "] of List [",
			  target(abr, PriceListEntity.class).getPrimaryKey(), "] has illegal value!"
			);
		}

		//~: load & map the existing prices
		PriceListEntity pl  = target(abr, PriceListEntity.class);
		GetGoods        get = bean(GetGoods.class);
		Map<Long, Long> exs = new HashMap<Long, Long>(101);
		get.getPriceListPrices(pl.getPrimaryKey(), exs);

		//c: for all the source goods
		for(GoodPrice s : src)
			//?: {update the exiting prices}
			if(exs.containsKey(s.getGoodUnit().getPrimaryKey()))
			{
				GoodPrice d = EX.assertn(get.getGoodPrice(
					 s.getGoodUnit().getPrimaryKey()
				  )
				);

				//?: {the prices are equal} skip
				if(CMP.eq(d.getPrice(), s.getPrice()))
					continue;

				//!: update the price
				d.setPrice(s.getPrice());
				chain(abr).first(new SetTxAction(task(abr), d));
			}
			//!: insert new prices
			else
			{
				//=: price list
				s.setPriceList(target(abr, PriceListEntity.class));

				//!: save the price
				chain(abr).first(new SaveNumericIdentified(task(abr), s));
			}

		//HINT: in this method the prices are not removed!
	}
	*/
}