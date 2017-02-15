package com.tverts.retrade.domain.invoice.shunts;

/* standard Java classes */

import java.util.Random;

/* JUnit */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
import static com.tverts.endure.UnityTypes.unityType;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceStateFixed;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * General implementation to shunt {@link Invoice}s
 * of the defined Unity Type.
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
@SelfShuntGroups({"retrade:invoices", "retrade:invoices:type"})
@SelfShuntDescr("Shunts Invoices of configured type.")
public class ShuntInvoicesType extends ShuntPlain
{
	/* shunting constants  */

	public static double DEF_PERCENT = 25.0; //<-- 25%
	public static long   DEF_INVMAX  = 500;


	/* public: ShuntInvoicesMove (parameters access) */

	/**
	 * Required parameter with the Invoice Unity Type.
	 */
	public String  getTypeName()
	{
		return typeName;
	}

	public void    setTypeName(String typeName)
	{
		this.typeName = SU.s2s(typeName);
	}

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
		if(percent <= 0.0) throw EX.state();
		this.percent = percent;
	}

	/**
	 * The maximum number of the invoices to change the state.
	 * By default is unlimited.
	 */
	public long    getInvmax()
	{
		return invmax;
	}

	public void    setInvmax(long invmax)
	{
		if(invmax <  0L) throw EX.state();
		if(invmax == 0L) invmax = Long.MAX_VALUE;
		this.invmax = invmax;
	}

	public boolean isRequired()
	{
		return required;
	}

	public void    setRequired(boolean required)
	{
		this.required = required;
	}


	/* shunt methods */

	@SelfShuntMethod(order = 0, critical = true)
	public void testBegin()
	{
		//~: invoice move type
		assertNotNull("Invoice type '" + getTypeName() +
		  "' is not found!", getInvoiceType()
		);

		//?: {has no seed} set it
		long seed = (getSeed() != null)?(getSeed()):
		  System.currentTimeMillis();
		LU.I(getLog(), "using seed = " + seed);

		//~: create the random generator
		gen = new Random(seed);

		//~: the number of invoices
		if(findNumberOfInvoices() == 0)
		{
			if(required) fail(
			  "No Invoices type '" + getTypeName() +
			  "' are found in the database!"
			);

			LU.W(getLog(), "found NO '", getTypeName(), "' Invoices!");
			return;
		}

		LU.I(getLog(), "found '", getTypeName(),
		  "' Invoices: ", invoicesNumber
		);
	}

	@SelfShuntMethod(order = 2, critical = true, editing = true)
	public void testToggleInvoicesStates()
	{
		long isize = genInvoicesToggleNumber();

		LU.I(getLog(), " Toggling states of ", isize,
		  " '", getTypeName(), "' Invoices..."
		);

		long percent = 0;

		//~: main invoice' state toggle cycle
		for(long itest = 0L;(itest < isize);)
		{
			itest += toggleInvoiceState(isize - itest);

			long p = itest * 100 / isize;
			if(p - percent >= 5)
			{
				LU.I(getLog(), ' ', p, '%');
				percent = p;
			}
		}

		LU.I(getLog(), "... done!");

		//!: test the global state
		assertGlobalInvariant();
	}


	/* protected: test supporting routines */

	/**
	 * We need to use separated transactions as single
	 * transaction accumulates too many database changes.
	 */
	protected long      toggleInvoiceState(final long imax)
	{
		final long[] res = new long[1];

		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				res[0] = toggleInvoiceStateTx(imax);
			}
		});

		return res[0];
	}

	protected long      toggleInvoiceStateTx(long imax)
	{
		long i; if(imax > 25) imax = 25;

		for(i = 0;(i < imax);i++)
		{
			//~: select the next random invoice
			Invoice invoice = selectNextInvoice();
			if(invoice == null) throw EX.state(
			  "Unable to select next Move Invoice!");

			//!: toggle the state
			toggleInvoiceState(invoice);
		}

		return i;
	}

	protected void      toggleInvoiceState(Invoice invoice)
	{
		assertInvoiceState(invoice, null);
		invoice = reload(invoice);

		//?: {this invoice is in Edit state} fix it
		if(getInvoiceStateEditType().equals(getInvoiceStateType(invoice)))
		{
			actionRun(Invoices.ACT_FIX, invoice);
			assertInvoiceState(invoice, Invoices.TYPE_INVSTATE_FIXED);
			return;
		}

		//?: {this invoice is in Fixed state} edit it
		if(getInvoiceStateFixedType().equals(getInvoiceStateType(invoice)))
		{
			actionRun(Invoices.ACT_EDIT, invoice);
			assertInvoiceState(invoice, Invoices.TYPE_INVSTATE_EDIT);
		}
	}

	protected Invoice   reload(Invoice invoice)
	{
		return (Invoice) session().
		  load(Invoice.class, invoice.getPrimaryKey());
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

	protected void      assertGlobalInvariant()
	{}

	protected UnityType getInvoiceType()
	{
		if(getTypeName() == null) throw EX.state(
		  "Required parameter of the Invoices type name is undefined!");

		return unityType(Invoice.class, getTypeName());
	}

	protected UnityType getInvoiceStateType(Invoice invoice)
	{
		return (invoice.getInvoiceState() == null)?(null):
		  (invoice.getInvoiceState().getUnity() == null)?(null):
		  (invoice.getInvoiceState().getUnity().getUnityType());
	}

	protected UnityType getInvoiceStateEditType()
	{
		return Invoices.typeInvoiceStateEdited();
	}

	protected UnityType getInvoiceStateFixedType()
	{
		return unityType(InvoiceStateFixed.class, Invoices.TYPE_INVSTATE_FIXED);
	}

	protected long      findNumberOfInvoices()
	{

/*

 select count(inv.id) from Invoice inv where
   (inv.domain = :domain) and (inv.invoiceType = :invoiceType)

 */

		return ((Number)( Q (

"select count(inv.id) from Invoice inv where\n" +
"  (inv.domain = :domain) and (inv.invoiceType = :invoiceType)"

		).
		  setParameter("domain",      domain()).
		  setParameter("invoiceType", getInvoiceType()).
		  uniqueResult())).
		  longValue();
	}

	protected Invoice   selectNextInvoice()
	{
		int offest = 0;

		if((int)invoicesNumber > 0)
			offest = gen.nextInt((int)invoicesNumber);

/*

 from Invoice where (domain = :domain) and
   (invoiceType = :invoiceType) order by primaryKey

 */

		return (Invoice) Q (

"from Invoice where (domain = :domain) and\n" +
"  (invoiceType = :invoiceType) order by primaryKey"

		).
		  setParameter("domain",      domain()).
		  setParameter("invoiceType", getInvoiceType()).
		  setFirstResult(offest).setMaxResults(1).
		  uniqueResult();
	}

	protected long      genInvoicesToggleNumber()
	{
		long res = (long)(percent * 0.01 * invoicesNumber);

		return (res <= 0L)?(1L):(res > invmax)?(invmax):(res);
	}


	/* private: parameters of the shunt unit */

	private String    typeName;
	private Long      seed;
	private double    percent = DEF_PERCENT;
	private long      invmax  = DEF_INVMAX;
	private boolean   required;


	/* protected: the runtime state of the shunt unit */

	protected Random  gen;
	protected long    invoicesNumber;
}