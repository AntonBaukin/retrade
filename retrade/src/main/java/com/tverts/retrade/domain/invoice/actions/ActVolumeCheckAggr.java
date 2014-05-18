package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: aggregator (volume) */

import com.tverts.endure.aggr.volume.AggrTaskVolumeCreate;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Volume Check Documents has volume aggregation items
 * of special kind: they assign the aggregation value,
 * in that terms, are fixed historical items.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActVolumeCheckAggr extends ActInvoiceAggrBase
{
	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);
		return Invoices.TYPE_VOLUME_CHECK.equals(tname);
	}


	/* protected: aggregate on create fixed state */

	protected void checkCreateFixedStateAltered(AggrCreateFixedState x)
	{
		throw EX.state("Volume Check Invoices may not be altered!");
	}

	protected void aggrCreateGoodAggrItems(AggrCreateFixedState x)
	{
		//~: add aggr item of good unit' volume in store
		aggrCreateGoodUnitsInStoreVolume(x);
	}

	protected void aggrInitGoodUnitsInStoreVolume
	  (AggrCreateFixedState x, AggrTaskVolumeCreate task)
	{
		//~: only positive aggregate component presents
		task.setAggrPositive(x.good.getVolume());

		//!: it is fixed history item
		task.setAggrFixed(true);
	}


	/* protected: aggregate on create edit state */

	protected void aggrDeleteGoodItems(AggrCreateEditState x)
	{
		//~: delete aggr item of good unit' in store volume
		aggrDeleteGoodUnitInStoreVolume(x);
	}
}