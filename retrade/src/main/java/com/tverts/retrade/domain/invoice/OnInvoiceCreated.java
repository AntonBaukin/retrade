package com.tverts.retrade.domain.invoice;

/* com.tverts: events */

import com.tverts.event.CreatedEvent;
import com.tverts.event.Event;
import com.tverts.event.Reactor;

/* com.tverts: endure (core, messages) */

import com.tverts.endure.UnityType;
import com.tverts.endure.msg.Msg;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.sells.Sells;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Reacts on an Invoice creation.
 *
 * @author anton.baukin@gmail.com.
 */
public class OnInvoiceCreated implements Reactor
{
	public void react(Event event)
	{
		if(!(event instanceof CreatedEvent))
			return;

		if(!(event.target() instanceof Invoice))
			return;

		Invoice   i = (Invoice)event.target();
		UnityType u = i.getInvoiceType();
		String    t; //<-- domain event type

		//?: {sell}
		if(Invoices.isSellInvoice(i))
			t = Invoices.MSG_CREATE_SELL;
		//?: {move}
		else if(Invoices.isMoveInvoice(i))
			t = Invoices.MSG_CREATE_MOVE;
		//?: {sells}
		else if(Sells.isSellsInvoice(i))
			t = Sells.MSG_CREATE_SELLS;
		//?: {buy}
		else if(Invoices.isBuyInvoice(i))
			t = Invoices.MSG_CREATE_BUY;
		else
			return; //<-- do nothing

		//!: notify system user
		Msg.create().types(t).domain(i).title(
		  "Создана ", u.getTitleLo().toLowerCase(), " №", i.getCode(),
		  " от ", DU.datetime2str(i.getInvoiceDate())
		).send();
	}
}