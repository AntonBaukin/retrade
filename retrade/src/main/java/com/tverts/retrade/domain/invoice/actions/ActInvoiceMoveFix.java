package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.StoreGood;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.InvoiceStateFixed;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.MoveData;
import com.tverts.retrade.domain.invoice.MoveGood;
import com.tverts.retrade.domain.invoice.ResGood;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Fix action builder for Move {@link Invoice}s.
 *
 * @author anton.baukin@gmail.com
 */
public class ActInvoiceMoveFix extends ActInvoiceBuySellFix
{
	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);
		return Invoices.TYPE_INVOICE_MOVE.equals(tname);
	}


	/* protected: fix action methods */

	protected boolean isAltInvoiceSupported(ActionBuildRec abr)
	{
		return true;
	}

	protected void    copyResultGoods(ActionBuildRec abr, InvoiceStateFixed fstate)
	{
		MoveData data = (MoveData) target(abr, Invoice.class).getInvoiceData();

		//HINT: for produce invoices, resulting
		//  goods are the ingredients to take from
		//  the source store, and the invoice goods
		//  are the goods created.

		//~: take goods from the source store
		for(ResGood g : data.getResGoods())
			insertStoreGoodAltered(abr, fstate, g);

		//~: place goods to the destination store
		for(MoveGood g : data.getGoods())
			//?: {good is NOT to take-only} take it
			if(!Boolean.FALSE.equals(g.getMoveOn()))
				insertStoreGoodAltered(abr, fstate, g);
	}

	protected void    insertStoreGood (
	    ActionBuildRec    abr,
	    InvoiceStateFixed fstate,
	    InvGood           igood
	  )
	{
		//?: {invoice is altered}
		if(target(abr, Invoice.class).getInvoiceData().isAltered())
			insertStoreGoodAltered(abr, fstate, igood);
		else
			insertStoreGoodRegular(abr, fstate, igood);
	}

	protected void    insertStoreGoodRegular (
	    ActionBuildRec    abr,
	    InvoiceStateFixed fstate,
	    InvGood           igood
	  )
	{
		StoreGood pgood = new StoreGood(); //<-- + good
		StoreGood mgood = new StoreGood(); //<-- - good

		//~: set the primary key
		setPrimaryKey(session(abr), pgood, isTestTarget(abr));
		setPrimaryKey(session(abr), mgood, isTestTarget(abr));

		//~: +store
		pgood.setStore(getInvoiceData(abr).getStore());

		//~: -store
		mgood.setStore(((MoveData)getInvoiceData(abr)).getSourceStore());

		//~: invoice' state
		pgood.setInvoiceState(fstate);
		mgood.setInvoiceState(fstate);
		fstate.getStoreGoods().add(pgood);
		fstate.getStoreGoods().add(mgood);

		//~: good unit
		pgood.setGoodUnit(igood.getGoodUnit());
		mgood.setGoodUnit(igood.getGoodUnit());

		//~: + volume
		pgood.setVolumePositive(igood.getVolume());

		//~: - volume
		mgood.setVolumeNegative(igood.getVolume());
	}

	protected void    insertStoreGoodAltered (
	    ActionBuildRec    abr,
	    InvoiceStateFixed fstate,
	    InvGood           igood
	  )
	{
		StoreGood mgood = null; //<-- - good
		StoreGood pgood = null; //<-- + good

		//?: {this is a place request}
		if(igood instanceof MoveGood)
		{
			//~: {not a take-only good}
			EX.assertx(!Boolean.FALSE.equals(((MoveGood)igood).getMoveOn()));

			//~: +volume
			pgood = new StoreGood();
			pgood.setVolumePositive(igood.getVolume());
		}

		//?: {this is a take request}
		if(igood instanceof ResGood)
		{
			//~: -volume
			mgood = new StoreGood();
			mgood.setVolumeNegative(igood.getVolume());

			//?: {this good is transient} balance +/- volume on the source store
			if(((ResGood)igood).getGoodCalc() != null)
				mgood.setVolumePositive(igood.getVolume());
		}

		//?: {take good}
		if(mgood != null)
		{
			//~: set the primary key
			setPrimaryKey(session(abr), mgood, isTestTarget(abr));

			//~: -store
			mgood.setStore(((MoveData)getInvoiceData(abr)).getSourceStore());

			//~: good unit
			mgood.setGoodUnit(igood.getGoodUnit());

			//~: state
			mgood.setInvoiceState(fstate);

			//!: add it
			fstate.getStoreGoods().add(mgood);
		}

		//?: {place good}
		if(pgood != null)
		{
			//~: set the primary key
			setPrimaryKey(session(abr), pgood, isTestTarget(abr));

			//~: +store
			pgood.setStore(getInvoiceData(abr).getStore());

			//~: good unit
			pgood.setGoodUnit(igood.getGoodUnit());

			//~: state
			pgood.setInvoiceState(fstate);

			//!: add it
			fstate.getStoreGoods().add(pgood);
		}
	}
}