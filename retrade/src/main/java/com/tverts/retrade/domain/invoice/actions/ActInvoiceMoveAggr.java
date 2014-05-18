package com.tverts.retrade.domain.invoice.actions;

/* standard Java classes */

import java.util.Set;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: retrade (goods + invoices) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.MoveData;
import com.tverts.retrade.domain.invoice.MoveGood;
import com.tverts.retrade.domain.invoice.ResGood;

/* com.tverts: aggregator (volume) */

import com.tverts.endure.aggr.volume.AggrTaskVolumeCreate;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * As Move Invoices do no change the rest cost and total
 * volume aggregated values, that requests are stripped.
 * Store volume aggregation requests are the same.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActInvoiceMoveAggr extends ActInvoiceAggrBase
{
	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);
		return Invoices.TYPE_INVOICE_MOVE.equals(tname);
	}


	/* protected: aggregate on create fixed state */

	protected void checkCreateFixedStateAltered(AggrCreateFixedState x)
	{}

	protected void aggrCreateGoodAggrItems(AggrCreateFixedState x)
	{
		//?: {invoice is regular move}
		if(!x.data.isAltered())
			aggrCreateFixedStateGoodRegular(x);
		else
			throw EX.state();
	}

	protected void aggrInitGoodUnitsInStoreVolume
	  (AggrCreateFixedState x, AggrTaskVolumeCreate task)
	{
		//?: {invoice is altered}
		if(x.data.isAltered())
			aggrInitGoodUnitsInStoreVolumeAltered(x, task);
		else
			aggrInitGoodUnitsInStoreVolumeRegular(x, task);
	}


	/* protected: aggregate on create fixed state for regular moves */

	protected void aggrCreateFixedStateGoodRegular(AggrCreateFixedState x)
	{
		//~: plus volumes to the destination store
		EX.assertx(CMP.grZero(x.good.getVolume()));
		x.store = ((MoveData)getInvoiceData(x.abr)).
		  getStore().getPrimaryKey();

		aggrCreateGoodUnitsInStoreVolume(x);

		//~: minus volumes from the source store
		x.good.setVolume(x.good.getVolume().negate()); //<-- reverted
		x.store = ((MoveData)getInvoiceData(x.abr)).
		  getSourceStore().getPrimaryKey();

		aggrCreateGoodUnitsInStoreVolume(x);
	}

	protected void aggrInitGoodUnitsInStoreVolumeRegular
	  (AggrCreateFixedState x, AggrTaskVolumeCreate task)
	{
		//?: {plus to the destination store}
		if(CMP.grZero(x.good.getVolume()))
			task.setVolumePositive(x.good.getVolume());
		//~: {minus from the source store}
		else
		{
			x.good.setVolume(x.good.getVolume().negate()); //<-- reverted
			task.setVolumeNegative(x.good.getVolume());
		}
	}


	/* protected: aggregate on create fixed state for altered move invoices */

	protected void aggrCreateFixedStateAltered(AggrCreateFixedState x)
	{
		//HINT: for produce invoices, resulting
		//  goods are the ingredients to take from
		//  the source store, and the invoice goods
		//  are the goods created.


		//~: take goods from the source store
		for(ResGood g : x.data.getResGoods())
		{
			x.good = g;
			aggrCreateFixedStateAlteredTake(x);
		}

		//~: place goods to the destination store
		for(MoveGood g : ((MoveData)x.data).getGoods())
			//?: {good is NOT to take-only} take it
			if(!Boolean.FALSE.equals(g.getMoveOn()))
			{
				x.good = g;
				aggrCreateFixedStateAlteredPlace(x);
			}
	}

	protected void aggrCreateFixedStateAlteredTake(AggrCreateFixedState x)
	{
		//~: source store to take from
		x.store = ((MoveData) getInvoiceData(x.abr)).
		  getSourceStore().getPrimaryKey();

		aggrCreateGoodUnitsInStoreVolume(x);
	}

	protected void aggrCreateFixedStateAlteredPlace(AggrCreateFixedState x)
	{
		//~: destination store to place to
		x.store = ((MoveData) getInvoiceData(x.abr)).
		  getStore().getPrimaryKey();

		aggrCreateGoodUnitsInStoreVolume(x);
	}

	protected void aggrInitGoodUnitsInStoreVolumeAltered
	  (AggrCreateFixedState x, AggrTaskVolumeCreate task)
	{
		//?: {this is a place request}
		if(x.good instanceof MoveGood)
		{
			//~: {not a take-only good}
			EX.assertx(!Boolean.FALSE.equals(((MoveGood)x.good).getMoveOn()));

			//~: +volume
			task.setVolumePositive(x.good.getVolume());
		}

		//?: {this is a take request}
		if(x.good instanceof ResGood)
		{
			//~: -volume
			task.setVolumeNegative(x.good.getVolume());

			//?: {this good is transient} balance +/- volume
			if(((ResGood)x.good).getGoodCalc() != null)
				task.setVolumePositive(x.good.getVolume());
		}
	}


	/* protected: aggregate on create edit state */

	protected void aggrDeleteItemsCollectGoods
	  (AggrCreateEditState x, Set<GoodUnit> goods)
	{
		//~: take all the goods
		for(InvGood g : x.data.getGoods())
			goods.add(g.getGoodUnit());

		//~: take resulting goods
		for(ResGood g : x.data.getResGoods())
			goods.add(g.getGoodUnit());
	}

	protected void aggrDeleteGoodItems(AggrCreateEditState x)
	{
		//~: delete aggr items of the source store
		x.store = ((MoveData)getInvoiceData(x.abr)).
		  getSourceStore().getPrimaryKey();
		aggrDeleteGoodUnitInStoreVolume(x);

		//?: {source and destination stores are the same}
		if(x.store.equals(getInvoiceData(x.abr).getStore().getPrimaryKey()))
			return;

		//~: delete aggr items of the destination store
		x.store = getInvoiceData(x.abr).
		  getStore().getPrimaryKey();
		aggrDeleteGoodUnitInStoreVolume(x);
	}
}