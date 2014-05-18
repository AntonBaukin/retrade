package com.tverts.retrade.secure;

/* com.tverts: secure */

import com.tverts.secure.force.DomainUnityForce;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * This Force works for all the Invoices of Domain.
 *
 * @author anton.baukin@gmail.com
 */
public class InvoicesForce extends DomainUnityForce
{
	/* public: InvoicesForce (bean) interface */

	public Class getTypeClass()
	{
		return Invoice.class;
	}

	public void  setTypeClass(Class cls)
	{
		throw EX.unop();
	}
}