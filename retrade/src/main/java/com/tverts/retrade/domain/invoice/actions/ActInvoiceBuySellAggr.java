package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import static com.tverts.endure.UnityTypes.unityType;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrRequest;
import com.tverts.endure.aggr.AggrValue;

/* com.tverts: aggregator (rest cost) */

import com.tverts.endure.aggr.cost.AggrTaskRestCostCreate;
import com.tverts.endure.aggr.cost.AggrTaskRestCostDelete;

/* com.tverts: aggregator (volume) */

import com.tverts.endure.aggr.volume.AggrTaskVolumeCreate;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.ResGood;
import com.tverts.retrade.domain.invoice.SellGood;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * This action builder creates actions to add aggregation
 * requests when the state of Buy or Sell Invoices is
 * changed to (from) fixed from (to) edit.
 *
 * It also supports Sells Invoices that are saved always
 * in the Fixed State and fully resemble Sell Invoices.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActInvoiceBuySellAggr extends ActInvoiceAggrBase
{
	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);

		return Invoices.TYPE_INVOICE_BUY.equals(tname) ||
		  Invoices.TYPE_INVOICE_SELLS.equals(tname) ||
		  Invoices.TYPE_INVOICE_SELL.equals(tname);
	}


	/* protected: aggregate on create fixed state */

	protected void    checkCreateFixedStateAltered(AggrCreateFixedState x)
	{
		//?: {not a buy invoice}
		EX.assertx( !Invoices.isBuyInvoice(x.data.getInvoice()),

		  "Can't fix aggregation of an Altered Buy Invoices [",
		  x.data.getInvoice().getPrimaryKey(), "]!"
		);

		//?: {there are no resulting goods}
		if(!x.data.getGoods().isEmpty())
			EX.asserte(x.data.getResGoods());
	}

	protected void    aggrCreateGoodAggrItems(AggrCreateFixedState x)
	{
		//~: add aggr item of good unit' volume in store
		aggrCreateGoodUnitsInStoreVolume(x);

		//~: add aggr item of good unit' rest cost
		if(hasRestCostAggrItems(x))
			aggrCreateGoodUnitsRestCost(x);
	}

	protected boolean hasRestCostAggrItems(AggrCreateFixedState x)
	{
		//?: {this is not a resulting good of altered sell invoice}
		return !(x.good instanceof ResGood) ||
		  //?: {or it is not a transient position}
		  (((ResGood)x.good).getGoodCalc() == null);
	}

	protected void    aggrInitGoodUnitsInStoreVolume
	  (AggrCreateFixedState x, AggrTaskVolumeCreate task)
	{
		//?: {buy invoice}
		if(x.good instanceof BuyGood)
			task.setVolumePositive(x.good.getVolume());

		//?: {sell invoice}
		if(x.good instanceof SellGood)
			task.setVolumeNegative(x.good.getVolume());

		//?: {resulting good of an altered sell | sells invoice}
		if(x.good instanceof ResGood)
		{
			//HINT: resulting goods of altered sell invoices
			//  having calculations are transient positions
			//  in the invoices: they must be taken and
			//  returned to the store at the same time.
			//  This is to update the calculations...

			//?: {has no calculation} regular take-from-store
			if(((ResGood)x.good).getGoodCalc() == null)
				task.setVolumeNegative(x.good.getVolume());
			//~: take and return transient good
			else
			{
				task.setVolumePositive(x.good.getVolume());
				task.setVolumeNegative(x.good.getVolume());
			}
		}
	}

	/**
	 * Creates requests to add components of Aggregated
	 * Value {@link Goods#AGGRVAL_GOOD_REST_COST} of the
	 * {@link GoodUnit}s of the goods in the target invoice.
	 */
	protected void    aggrCreateGoodUnitsRestCost(AggrCreateFixedState x)
	{
		//~: unity type of the target aggregated value
		UnityType   avt = unityType(
		  AggrValue.class, Goods.AGGRVAL_GOOD_REST_COST);

		//~: load the aggregated value
		AggrValue   val = x.getav.loadAggrValue(
		  x.good.getGoodUnit().getPrimaryKey(), avt, null);

		//~: create the aggregation request
		AggrRequest req = new AggrRequest();

		//~: request aggregated value
		req.setAggrValue(val);

		//~: request source: the invoice
		req.setSource(x.source);

		//~: create the task
		AggrTaskRestCostCreate task = new AggrTaskRestCostCreate();

		//?: {buy invoice}
		if(x.good instanceof BuyGood)
		{
			task.setGoodVolume(x.good.getVolume());
			task.setVolumeCost(((BuyGood)x.good).getCost());
		}
		//?: {sell | sells  invoice}
		else if(x.good instanceof SellGood)
			task.setGoodVolume(x.good.getVolume().negate());
		//?: {not a transient resulting good of altered sell invoice}
		else if(x.good instanceof ResGood)
		{
			EX.assertx(((ResGood)x.good).getGoodCalc() == null);
			task.setGoodVolume(x.good.getVolume().negate());
		}
		else
			throw EX.state("Not a Buy-Sell Good!");

		//~: assign request task
		req.setAggrTask(task);

		//!: add the aggregation request
		x.action.add(req);
	}


	/* protected: aggregate on create edit state */

	protected void    aggrDeleteGoodItems(AggrCreateEditState x)
	{
		//~: delete aggr item of good unit' in store volume
		aggrDeleteGoodUnitInStoreVolume(x);

		//~: delete aggr item of good unit' rest cost
		aggrDeleteGoodUnitsRestCost(x);
	}

	/**
	 * Creates requests to remove components of Aggregated
	 * Value {@link Goods#AGGRVAL_GOOD_REST_COST} of the
	 * {@link GoodUnit}s of the goods in the target invoice.
	 */
	protected void    aggrDeleteGoodUnitsRestCost(AggrCreateEditState x)
	{
		//~: unity type of the target aggregated value
		UnityType   avt = unityType(
		  AggrValue.class, Goods.AGGRVAL_GOOD_REST_COST);

		//~: load the aggregated value
		AggrValue   val = x.getav.loadAggrValue(
		  x.good.getPrimaryKey(), avt, null);

		//~: create the aggregation request
		AggrRequest req = new AggrRequest();

		//~: request aggregated value
		req.setAggrValue(val);

		//~: request source: the invoice
		req.setSource(x.invuni);


		AggrTaskRestCostDelete task = new AggrTaskRestCostDelete();

		//~: assign request task   <-- filled automatically
		req.setAggrTask(task);

		//!: add the aggregation request
		x.action.add(req);
	}
}