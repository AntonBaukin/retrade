package com.tverts.retrade.domain.invoice.gen;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.MoveData;
import com.tverts.retrade.domain.invoice.MoveGood;


/**
 * Generates Correction Invoices being special
 * sub-types of Move Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class GenCorrectionInvoice extends GenInvoiceMove
{
	/* protected: GenInvoiceBase interface */

	protected UnityType   getDayMarkType()
	{
		return Invoices.typeInvoiceCorrection();
	}

	protected InvoiceData createInvoiceData(GenCtx ctx)
	{
		MoveData d = new MoveData();

		//~: correction invoice sub-type
		d.setSubType('C');

		return d;
	}

	/**
	 * All altered Move Invoices has the source Store
	 * the same the destination one.
	 */
	protected void        assignSourceStore(GenCtx ctx, InvoiceData data)
	{
		((MoveData)data).setSourceStore(data.getStore());
	}

	protected void        genGoods(GenCtx ctx, InvoiceData data)
	{
		super.genGoods(ctx, data);

		//HINT:  correction is placing or taking any
		//  volumes of goods, but not regular moves.

		//~: make the goods be taken-only | placed-only
		for(MoveGood g : ((MoveData)data).getGoods())
			g.setMoveOn(ctx.gen().nextBoolean());
	}
}