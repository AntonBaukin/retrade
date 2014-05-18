package com.tverts.retrade.exec.datas.invoices;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.GetUnityType;

/* com.tverts: execution */

import com.tverts.exec.ExecutorBase;

/* com.tverts: data sources */

import com.tverts.data.SingleEntity;

/* com.tverts: retrade.domain (invoices + payments) */

import com.tverts.retrade.domain.invoice.GetInvoice;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.payment.GetInvoiceBill;
import com.tverts.retrade.domain.payment.InvoiceBill;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * XML Data provider for Invoices of the types:
 * Buy, not altered Sell, not altered Move.
 *
 * @author anton.baukin@gmail.com.
 */
public class DatasInvoiceGeneral extends ExecutorBase
{
	/* public: Executor */

	public Object execute(Object request)
	{
		SingleEntity e;

		//?: {not a single entity request}
		if(!(request instanceof SingleEntity)) return null;
		e = (SingleEntity) request;

		//~: cache unity type
		cacheUnityType(e);

		//?: {not that invoice type}
		if(!isThatType())
			return null;

		//~: cache invoice
		cacheInvoice(e);

		//?: {not that invoice sub-type}
		if(!isThatInvoice())
			return null;

		return wrapInvoice();
	}


	/* protected: execution internals */

	protected void    cacheUnityType(SingleEntity e)
	{
		if(val(UnityType.class) != null) return;
		val(EX.assertn(bean(GetUnityType.class).getUnityType(e.getUnityType())));
	}

	protected void    cacheInvoice(SingleEntity e)
	{
		if(val(Invoice.class) != null) return;
		val(EX.assertn(bean(GetInvoice.class).getInvoice(e.getPrimaryKey())));
	}

	protected boolean isThatType()
	{
		UnityType ut = val(UnityType.class);

		//?: {not an invoice}
		if(!ut.getTypeClass().equals(Invoice.class))
			return false;

		//?: {is type supported}
		return Invoices.typeInvoiceBuy().equals(ut) ||
		  Invoices.typeInvoiceSell().equals(ut) ||
		  Invoices.typeInvoiceMove().equals(ut);
	}

	protected boolean isThatInvoice()
	{
		Invoice i = val(Invoice.class);

		//?: {not an altered move | sell invoice}
		return !(Invoices.isSellInvoice(i) || Invoices.isMoveInvoice(i)) ||
		  !i.getInvoiceData().isAltered();
	}

	protected Object  wrapInvoice()
	{
		Invoice     i = val(Invoice.class);
		InvoiceEdit v = new InvoiceEdit().init(i);

		//~: invoice bill -> contractor
		InvoiceBill b = bean(GetInvoiceBill.class).getInvoiceBill(i);
		if(b != null) v.init(b.getContractor());

		return v;
	}
}