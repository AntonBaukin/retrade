package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (core) */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.Invoices;


/**
 * Collects constants and static routines
 * for handling Sell Desks entities.
 *
 * @author anton.baukin@gmail.com
 */
public class Sells
{
	/* Unity Types */

	/**
	 * Unity Type name for {@link PayDesk} entities.
	 */
	public static final String TYPE_PAY_DESK   =
	  "ReTrade: Sells: Payment Desk";

	/**
	 * Unity Type name for {@link SellsDesk} entities.
	 */
	public static final String TYPE_SELLS_DESK =
	  "ReTrade: Sells: Sells Desk";

	/**
	 * Unity Type name for {@link SellsSession} entities.
	 */
	public static final String TYPE_SELLS_SESS =
	  "ReTrade: Sells: Sells Session";

	/**
	 * Special version of Invoice initiated by Sells Session
	 * to take the goods from the Stores.
	 */
	public static final String TYPE_INVOICE    =
	  Invoices.TYPE_INVOICE_SELLS;

	/**
	 * Unity Type of {@link SellsPay} Payments.
	 */
	public static final String TYPE_SELLS_PAY  =
	  "ReTrade: Sells: Payment";


	/* Messages Types */

	public static final String MSG_CREATE_SELLS = "invoice: sells: create";
	public static final String MSG_FIX_SELLS    = "invoice: sells: fix";
	public static final String MSG_EDIT_SELLS   = "invoice: sells: edit";
	public static final String MSG_UPD_SELLS    = "invoice: sells: updated";


	/* Sells Types Access */

	public static UnityType typeSellsSession()
	{
		return UnityTypes.unityType(SellsSession.class, TYPE_SELLS_SESS);
	}

	public static boolean   isSellsSession(Unity unity)
	{
		return (unity != null) &&
		  typeSellsSession().equals(unity.getUnityType());
	}

	public static UnityType typeSellsInvoice()
	{
		return UnityTypes.unityType(Invoice.class, TYPE_INVOICE);
	}

	public static boolean   isSellsInvoice(Invoice invoice)
	{
		return typeSellsInvoice().equals(invoice.getInvoiceType());
	}

	public static boolean   isSellsInvoice(Unity unity)
	{
		return (unity != null) &&
		  typeSellsInvoice().equals(unity.getUnityType());
	}

	public static UnityType typeSellsPayment()
	{
		return UnityTypes.unityType(SellsPay.class, TYPE_SELLS_PAY);
	}


	/* Calculations Support */

	public static BigDecimal calcReceiptsIncome(SellsSession s)
	{
		BigDecimal r = BigDecimal.ZERO;

		if(s.getReceipts() != null) for(SellReceipt sr : s.getReceipts())
			if(sr.getIncome() != null)
				r = r.add(sr.getIncome());

		return r;
	}
}