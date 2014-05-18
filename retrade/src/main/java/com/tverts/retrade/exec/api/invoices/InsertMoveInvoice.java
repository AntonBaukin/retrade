package com.tverts.retrade.exec.api.invoices;

/* standard Java classes */

import java.util.Map;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.MoveData;
import com.tverts.retrade.domain.invoice.MoveGood;

/* com.tverts: api */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.document.BuySell;
import com.tverts.api.retrade.document.Move;


/**
 * Inserts new Move Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertMoveInvoice extends InsertInvoiceBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Move);
	}


	/* protected: UpdateInvoiceBase interface */

	protected String      getInvoiceTypeName()
	{
		return Invoices.TYPE_INVOICE_MOVE;
	}

	protected InvoiceData createInvoiceData()
	{
		return new MoveData();
	}

	protected InvGood     createGood()
	{
		return new MoveGood();
	}

	/**
	 * HINT: Move Invoices has no Contractor.
	 */
	protected void assignContractor(BuySell bs, Map params)
	{}

	protected void assignStore(InvoiceData d, BuySell bs)
	{
		super.assignStore(d, bs);

		//~: set the source store
		((MoveData)d).setSourceStore(loadStore(
		  bs.getXkey(), ((Move)bs).getXSource(), ((Move)bs).getSource()));
	}
}