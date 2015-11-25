package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (tx) */

import com.tverts.system.tx.SetTxAction;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.core.ActUnity;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.ActAggrValue;

/* com.tverts: retrade domain (core + stores) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;


/**
 * {@link GoodUnit} processing actions builder.
 *
 * @author anton.baukin@gmail.com
 */
public class ActGoodUnit extends ActionBuilderReTrade
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
	 * Boolean flag, if set the {@link MeasureUnit} reference
	 * would be explicitly saved.
	 */
	public static final String SAVE_MEASURE_UNIT =
	  ActGoodUnit.class.getName() + ": save measure unit";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
		{
			saveGoodUnit(abr);

			//!: take exclusive locks (is executed very first!)
			lockGoodUnits(abr);
		}

		if(UPDATE.equals(actionType(abr)))
			updateGoodUnit(abr);

		if(ENSURE.equals(actionType(abr)))
		{
			ensureGoodUnit(abr);

			//!: take exclusive locks (is executed very first!)
			lockGoodUnits(abr);
		}
	}


	/* protected: action methods */

	protected void saveGoodUnit(ActionBuildRec abr)
	{
		//?: {target is not a GoodUnit}
		checkTargetClass(abr, GoodUnit.class);

		//~: ensure the good unit        <-- is executed last!
		xnest(abr, ActGoodUnit.ENSURE, target(abr));

		//~: save the good unit
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//?: {set good unit' unity}
		if(target(abr, GoodUnit.class).getUnity() == null)
			xnest(abr, ActUnity.CREATE, target(abr));

		//?: {has save measure unit flag set}
		if(flag(abr, SAVE_MEASURE_UNIT))
			saveMeasureUnit(abr);

		complete(abr);
	}

	protected void saveMeasureUnit(ActionBuildRec abr)
	{
		MeasureUnit munit = target(abr, GoodUnit.class).getMeasure();

		//?: {there is no measure unit reference}
		if(munit == null) return;

		//~: the domain
		munit.setDomain(target(abr, GoodUnit.class).getDomain());

		//?: {it is a test good unit} create test key
		if((munit.getPrimaryKey() == null) && isTestTarget(abr))
			setPrimaryKey(session(abr), munit, true);

		//!: nest the measure creation
		xnest(abr, ActMeasureUnit.SAVE, munit);
	}

	protected void updateGoodUnit(ActionBuildRec abr)
	{
		//?: {target is not a GoodUnit}
		checkTargetClass(abr, GoodUnit.class);

		//~: update the Txn
		chain(abr).first(new SetTxAction(task(abr)));

		complete(abr);
	}

	protected void ensureGoodUnit(ActionBuildRec abr)
	{
		//?: {target is not a GoodUnit}
		checkTargetClass(abr, GoodUnit.class);

		//~: create the needed aggregated values
		ensureGoodUnitAggrValues(abr);

		complete(abr);
	}

	protected void ensureGoodUnitAggrValues(ActionBuildRec abr)
	{
		//~: the rest cost of the good unit
		buildAggrValue(abr, Goods.AGGRVAL_GOOD_REST_COST, null);

		GoodUnit good = target(abr, GoodUnit.class);

		//~: ensure volume aggregated values for each store present
		List<TradeStore> stores = bean(GetTradeStore.class).
		  getTradeStores(good.getDomain().getPrimaryKey());

		for(TradeStore store : stores)
			buildAggrValue(abr, Goods.AGGRVAL_STORE_VOLUME, good,
			  ActAggrValue.OWNER, store,
			  ActAggrValue.CALCS, Goods.AGGR_CALC_VOL_CHECK
			);
	}

	/**
	 * Takes {@link Goods#LOCK_XGOODS} lock.
	 */
	protected void lockGoodUnits(ActionBuildRec abr)
	{
		takeLock(abr, target(abr, GoodUnit.class).getDomain(),
		  Goods.LOCK_XGOODS);
	}
}