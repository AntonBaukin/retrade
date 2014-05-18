package com.tverts.retrade.domain.store;

/* standard Java classes */

import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (tx) */

import com.tverts.system.tx.SetTxAction;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.core.ActUnity;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.ActAggrValue;


/* com.tverts: retrade domain (core + goods) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.GoodUnit;


/**
 * {@link TradeStore} processing actions builder.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActTradeStore extends ActionBuilderReTrade
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
		{
			saveTradeStore(abr);

			//!: take exclusive locks (is executed very first!)
			lockTradeStore(abr);
		}

		//HINT: update operation changes just the
		// attributes, no lock is required!

		if(UPDATE.equals(actionType(abr)))
			updateTradeStore(abr);

		if(ENSURE.equals(actionType(abr)))
		{
			ensureTradeStore(abr);

			//!: take exclusive locks (is executed very first!)
			lockTradeStore(abr);
		}
	}


	/* protected: action methods */

	protected void saveTradeStore(ActionBuildRec abr)
	{
		//?: {target is not a TradeStore}
		checkTargetClass(abr, TradeStore.class);

		//~: ensure the store <-- is executed last!
		xnest(abr, ActTradeStore.ENSURE, target(abr));

		//~: save the store
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set store unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr));

		complete(abr);
	}

	protected void updateTradeStore(ActionBuildRec abr)
	{
		//?: {target is not a TradeStore}
		checkTargetClass(abr, TradeStore.class);

		//~: update the Txn
		chain(abr).first(new SetTxAction(task(abr)));

		complete(abr);
	}

	protected void ensureTradeStore(ActionBuildRec abr)
	{
		//?: {target is not a TradeStore}
		checkTargetClass(abr, TradeStore.class);

		//~: create aggregated values for all the good units
		ensureTradeStoreAggrValues(abr);

		complete(abr);
	}

	protected void ensureTradeStoreAggrValues(ActionBuildRec abr)
	{
		TradeStore     store = target(abr, TradeStore.class);

		//~: select all the goods present
		List<GoodUnit> goods = bean(GetGoods.class).
		  getGoodUnits(store.getDomain());

		for(GoodUnit good : goods)
			buildAggrValue(abr, Goods.AGGRVAL_STORE_VOLUME, good,
			  ActAggrValue.CALCS, Goods.AGGR_CALC_VOL_CHECK
			);
	}

	/**
	 * Takes {@link Goods#LOCK_XGOODS} lock.
	 */
	protected void lockTradeStore(ActionBuildRec abr)
	{
		takeLock(abr, target(abr, TradeStore.class).getDomain(),
		  Goods.LOCK_XGOODS);
	}
}