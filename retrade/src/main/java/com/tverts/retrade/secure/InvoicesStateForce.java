package com.tverts.retrade.secure;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: events */

import com.tverts.event.Event;
import com.tverts.event.StateChangedEvent;

/* com.tverts: endure (core + secure) */

import com.tverts.endure.United;
import com.tverts.endure.UnityType;
import com.tverts.endure.secure.SecRule;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.Invoices;


/**
 * This Force works for all the Invoices of Domain
 * having the State (fixed or editing) defined.
 *
 * @author anton.baukin@gmail.com
 */
public class InvoicesStateForce extends InvoicesForce
{
	/* public: AllInvoicesForce (bean) interface */

	public boolean isFixedState()
	{
		return fixedState;
	}

	public void    setFixedState(boolean fixedState)
	{
		this.fixedState = fixedState;
	}

	/* public: Reactor interface */

	public void    react(Event event)
	{
		//?: {invoice created event}
		if(ise(event, StateChangedEvent.class) && ist(event, Invoice.class))
			if(isThatUnityType((Invoice) event.target()))
				reactInvoiceStateChanged((StateChangedEvent) event);

		super.react(event);
	}


	/* protected: reactions */

	protected boolean   isThatUnityEnsure(United u)
	{
		return super.isThatUnityEnsure(u) &&
		  isThatInvoiceState((Invoice) u);
	}

	protected boolean   isThatInvoiceState(Invoice i)
	{
		return (Invoices.isInvoiceFixed(i) == isFixedState());
	}

	protected void      reactInvoiceStateChanged(StateChangedEvent e)
	{
		//~: load domain rule
		SecRule rule = loadDomainRule(
		  ((Invoice) e.target()).getDomain().getPrimaryKey());

		//?: {it is that state} ensure the link
		if(isThatInvoiceState((Invoice) e.target()))
			ensureLink(getSecKey(), rule, (Invoice) e.target(), !isForbid());
		else
			removeLink(getSecKey(), rule, (Invoice) e.target());
	}

	protected UnityType getStateType()
	{
		return isFixedState()
		  ?(Invoices.typeInvoiceStateFixed())
		  :(Invoices.typeInvoiceStateEdited());
	}

	protected void      queryEntitiesWithoutRules(QueryBuilder qb)
	{
		//~: create the query base
		super.queryEntitiesWithoutRules(qb);

		//~: restrict with the invoices of type of interest
		qb.getClauseWhere().
		  addPart("e.invoiceState.unity.unityType = :st").
		  param("st", getStateType());
	}


	/* private: invoice state */

	private boolean fixedState;
}