package com.tverts.retrade.domain.invoice.shunts;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/* JUnit */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.flush;

/* com.tverts: (spring + tx) */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;

/* com.tverts: self shunts */

import com.tverts.shunts.SelfShuntDescr;
import com.tverts.shunts.SelfShuntGroups;
import com.tverts.shunts.SelfShuntMethod;
import com.tverts.shunts.SelfShuntUnit;
import com.tverts.shunts.ShuntPlain;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import static com.tverts.endure.ActionBuilderXRoot.SYNCH_AGGR;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.GenFixInvoices;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Shunts {@link Invoice}s of {@link Invoices#TYPE_INVOICE_BUY}
 * and {@link Invoices#TYPE_INVOICE_SELL} unity types.
 *
 * The test selects random invoices and toggles their state
 * from (to) edit to (from) fixed. After each change it checks
 * the aggregated values related to the test trade store and
 * the good units.
 *
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit
@SelfShuntGroups({"retrade:invoices", "retrade:invoices:buy-sell"})
@SelfShuntDescr("Shunts Invoices of Buy and Sell Unity types.")
public class ShuntInvoicesBuySell extends ShuntPlain
{
	/* shunting constants  */

	public static double DEF_PERCENT = 25.0; //<-- 25%
	public static int    DEF_INVMAX  = 500;


	/* public: ShuntInvoicesBuySell (parameters access) */

	/**
	 * The seed of the random generator. May be undefined:
	 * the current time is selected.
	 */
	public Long    getSeed()
	{
		return seed;
	}

	public void    setSeed(Long seed)
	{
		this.seed = seed;
	}

	/**
	 * The percent of the number of invoices stored
	 * in database to change the state. It may be
	 * greater than 1.0.
	 */
	public double  getPercent()
	{
		return percent;
	}

	public void    setPercent(double percent)
	{
		if(percent <= 0.0) throw new IllegalArgumentException();
		this.percent = percent;
	}

	/**
	 * The maximum number of the invoices to change the state.
	 * By default is unlimited.
	 */
	public int     getInvmax()
	{
		return invmax;
	}

	public void    setInvmax(int invmax)
	{
		if(invmax <  0L) throw new IllegalArgumentException();
		if(invmax == 0L) invmax = Integer.MAX_VALUE;
		this.invmax = invmax;
	}

	public boolean isRedate()
	{
		return redate;
	}

	public void    setRedate(boolean redate)
	{
		this.redate = redate;
	}


	/* shunt methods */

	@SelfShuntMethod(order = 0, critical = true)
	public void testBegin()
	{
		//~: Invoice type Buy exists
		Invoices.typeInvoiceBuy();

		//~: Invoice type Sell exists
		Invoices.typeInvoiceSell();

		//?: {has no seed} set it
		long seed = (getSeed() != null)?(getSeed()):
		  System.currentTimeMillis();
		LU.I(getLog(), "using seed = " + seed);

		//~: create the random generator
		gen = new Random(seed);

		//~: the number of invoices
		assertTrue("No Buy-Sell Invoices are found in the database!",
		  (invoicesNumber = findNumberOfInvoices()) != 0);

		LU.I(getLog(), "found Buy-Sell Invoices: ", invoicesNumber);
	}

	@SelfShuntMethod(order = 1, critical = true)
	public void testInvoicesOrder()
	{
		ShuntInvoicesShared sis = new ShuntInvoicesShared(
		  session(), domain());

		//~: test Buy Invoices
		sis.testInvoicesOrder(Invoices.typeInvoiceBuy());

		//~: test Sell Invoices
		sis.testInvoicesOrder(Invoices.typeInvoiceSell());

		//~: test Move Invoices
		sis.testInvoicesOrder(Invoices.typeInvoiceMove());

		//~: test the shared order
		sis.testInvoicesSharedOrder(UnityTypes.unityType(
		  Invoice.class, Invoices.OTYPE_INV_BUYSELL
		));
	}

	@SelfShuntMethod(order = 2, critical = true, editing = true)
	public void testToggleInvoicesStates()
	{
		//?: {GenFixBuySellInvoices took place}
		if(ctx().getGenCtx().containsKey(GenFixInvoices.class))
		{
			LU.I(getLog(), "GenFixBuySellInvoices took place ",
			  "in this run — toggle is skipped for now...");

			return;
		}

		long ts = System.currentTimeMillis(), tx = ts;
		int  px = 0; //<-- logged percent

		//~: get the number of invoices to toggle
		int  in = genInvoicesToggleNumber();

		//~: toggle invoces, each in own transaction
		for(int i = 0;(i < in);i++)
		{
			//~: toggle state of single random invoice
			bean(TxBean.class).execute(() ->
			  toggleInvoiceState(EX.assertn(selectNextInvoice())));

			//~: percent logging
			int p = (i + 1) * 100 / in;
			if(p - px >= 5)
			{
				LU.I(getLog(), "  toggle done ",
				  p, "% is ", i, " in ", LU.td(ts),
				  (ts == tx)?(""):(" delta " + LU.td(tx))
				);

				px = p;
				tx = System.currentTimeMillis();
			}
		}
	}


	/* protected: test supporting routines */

	protected void      toggleInvoiceState(Invoice invoice)
	{
		//?: {this is a buy invoice}
		if(Invoices.isBuyInvoice(invoice))
			toggleInvoiceBuyState(invoice);

		//?: {this is a sell invoice}
		if(Invoices.isSellInvoice(invoice))
			toggleInvoiceSellState(invoice);
	}

	protected void      toggleInvoiceBuyState(Invoice invoice)
	{
		assertInvoiceState(invoice, null);
		invoice = reload(invoice);

		//?: {this invoice is in Edit state} fix it
		if(Invoices.isInvoiceEdited(invoice))
		{
			//?: {changing the date}
			if(isRedate())
				editDate(invoice);

			//!: fix the invoice
			actionRun(Invoices.ACT_FIX, invoice, SYNCH_AGGR, true);
			assertInvoiceState(invoice, Invoices.TYPE_INVSTATE_FIXED);

			return;
		}


		//?: {this invoice is in Fixed state} edit it
		if(Invoices.isInvoiceFixed(invoice))
		{
			actionRun(Invoices.ACT_EDIT, invoice, SYNCH_AGGR, true);
			assertInvoiceState(invoice, Invoices.TYPE_INVSTATE_EDIT);
		}
	}

	protected void      toggleInvoiceSellState(Invoice invoice)
	{
		toggleInvoiceBuyState(invoice);
	}

	/**
	 * This method edits the Invoice: changes the date
	 * by +/- 2 days, thus affecting it's order.
	 */
	protected void      editDate(Invoice i)
	{
		InvoiceEdit ie = new InvoiceEdit().init(i);

		//~: change the date (left the time)
		ie.setEditDate(DU.addDays(
		  ie.getEditDate(), 2 - gen.nextInt(5)
		));

		//!: run edit action (change the date and order)
		actionRun(Invoices.ACT_UPDATE, i, Invoices.INVOICE_EDIT, ie);
	}

	protected Invoice   reload(Invoice invoice)
	{
		return session().load(Invoice.class, invoice.getPrimaryKey());
	}

	/**
	 * Warning! This test flushes and clears the session.
	 */
	protected void      assertInvoiceState(Invoice invoice, String stateName)
	{
		//!: flush and evict invoice to make it's check fair
		flush(session());
		session().evict(invoice);

		//~: reload the invoice
		invoice = reload(invoice);

		//~: test the state
		assertNotNull("Invoice state must be assigned after the state change!",
		  invoice.getInvoiceState());

		if(stateName != null) assertEquals(
		  "Invoice state has wrong state Unity Type after the state change!",
		  stateName, invoice.getInvoiceState().getUnity().getUnityType().getTypeName()
		);

	}

	protected int       findNumberOfInvoices()
	{
/*

select count(inv.id) from Invoice inv where
  (inv.domain = :domain) and (inv.invoiceType in (:invoiceTypes))

*/

		return ((Number)( Q (

"select count(inv.id) from Invoice inv where\n" +
"  (inv.domain = :domain) and (inv.invoiceType in (:invoiceTypes))"

		).
		  setParameter    ("domain",       domain()).
		  setParameterList("invoiceTypes", getInvoiceTypes()).
		  uniqueResult())).
		  intValue();
	}

	protected Invoice   selectNextInvoice()
	{
		int offest = 0;

		if(invoicesNumber > 0)
			offest = gen.nextInt(invoicesNumber);

/*

from Invoice where (domain = :domain) and
  (invoiceType in (:invoiceTypes)) order by primaryKey

*/

		return (Invoice) Q (

"from Invoice where (domain = :domain) and\n" +
"  (invoiceType in (:invoiceTypes)) order by primaryKey"

		).
		  setParameter    ("domain",       domain()).
		  setParameterList("invoiceTypes", getInvoiceTypes()).
		  setFirstResult(offest).setMaxResults(1).
		  uniqueResult();
	}

	protected int       genInvoicesToggleNumber()
	{
		int res = (int)(percent * 0.01 * invoicesNumber);
		return (res <= 0)?(1):(res > invmax)?(invmax):(res);
	}

	protected Collection<UnityType> getInvoiceTypes()
	{
		return Arrays.asList(
		  Invoices.typeInvoiceBuy(),
		  Invoices.typeInvoiceSell()
		);
	}


	/* private: parameters of the shunt unit */

	private Long      seed;
	private double    percent = DEF_PERCENT;
	private int       invmax  = DEF_INVMAX;
	private boolean   redate;


	/* protected: the runtime state of the shunt unit */

	protected Random  gen;
	protected int     invoicesNumber;
}