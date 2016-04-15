package com.tverts.retrade.domain.payment;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: retrade domain (firms + invoices) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.invoice.Invoice;


/**
 * Loads {@link InvoiceBill} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component
public class GetInvoiceBill extends GetObjectBase
{
	/* Get Invoice Bill */

	public InvoiceBill getInvoiceBill(Invoice invoice)
	{

// from InvoiceBill where invoice = :invoice

		return (InvoiceBill) Q(
		  "from InvoiceBill where invoice = :invoice"
		).
		  setParameter("invoice", invoice).
		  uniqueResult();
	}

	public Contractor  getInvoiceBillContractor(Invoice invoice)
	{

// select ib.contractor from InvoiceBill ib where ib.invoice = :invoice

		return (Contractor) Q(
		  "select ib.contractor from InvoiceBill ib where ib.invoice = :invoice"
		).
		  setParameter("invoice", invoice).
		  uniqueResult();
	}
}