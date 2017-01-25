package com.tverts.retrade.domain.invoice;

/* Java */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import static com.tverts.endure.ActionBuilderXRoot.SYNCH_AGGR;
import com.tverts.endure.core.Domain;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * This genesis is to fix Buy-Sel Invoices after they were
 * created. It works if no fixed Invoices in the fixed state
 * does present. It selects Buy-Sel Invoices in their index
 * order and fixes them (in that order).
 *
 * The number of Invoices is the percent of the all number
 * limited with optional {@link #getMaximum()} parameter.
 *
 * This implementation places own class in the generation
 * context (with true value) to indicate that Invoices
 * fixing really took place.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GenFixInvoices extends GenesisHiberPartBase
{
	/* public: GenToggleBuySellInvoicesStates (bean) interface */

	/**
	 * The percent of objects to fix of the all
	 * Buy-Sell Invoices number. By default is 50%.
	 * Values are from 0.0 to 100.0.
	 */
	@Param
	public double getPercent()
	{
		return percent;
	}

	private double  percent = 50.0;

	public void setPercent(double percent)
	{
		EX.assertx((percent >= 0.0) && (percent <= 100.0));
		this.percent = percent;
	}

	/**
	 * The absolute maximum of Invoices to fix.
	 * By default is undefined (unlimited).
	 */
	@Param
	public Integer getMaximum()
	{
		return maximum;
	}

	private Integer maximum;

	public void setMaximum(Integer maximum)
	{
		this.maximum = maximum;
	}

	@Param
	public boolean isBuy()
	{
		return buy;
	}

	private boolean buy = true;

	public void setBuy(boolean buy)
	{
		this.buy = buy;
	}

	@Param
	public boolean isSell()
	{
		return sell;
	}

	private boolean sell = true;

	public void setSell(boolean sell)
	{
		this.sell = sell;
	}

	@Param
	public boolean isMove()
	{
		return move;
	}

	private boolean move;

	public void setMove(boolean move)
	{
		this.move = move;
	}


	/* public: Genesis interface */

	@SuppressWarnings("unchecked")
	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: select the invoices of interest
		List<Long> invoices = selectInvoices(ctx);

		//?: {not found} exit
		if(invoices.isEmpty()) return;

		//!: mark the context
		ctx.set(this.getClass(), true);
		ctx.export(this.getClass()); //<-- for the shunts

		//c: fix that invoices
		long ts = System.currentTimeMillis();
		fixInvoices(ctx, invoices);

		LU.I(ctx.log(), getClass().getSimpleName(),
		  " done fixing [", invoices.size(), "] invoices in [",
		  (System.currentTimeMillis() - ts)/1000, "] seconds!"
		);
	}


	/* protected: invoices fixing */

	protected void fixInvoices(GenCtx ctx, List<Long> invoices)
	{
		long ts = System.currentTimeMillis(), tx = ts;
		int  px = 0; //<-- logged percent

		for(int b = 0;(b < invoices.size());)
		{
			int e = b + 2;
			if(e > invoices.size()) e = invoices.size();

			//~: fix invoices in batch in own small transaction
			fixInvoicesBatch(ctx, invoices.subList(b, e));
			b = e; //<-- advance

			//~: percent logging
			int p = b * 100 / invoices.size();
			if(p - px >= 5)
			{
				LU.I(ctx.log(), LU.cls(this), " done ",
				  p, "% is ", e, " in ", LU.td(ts),
				  (ts == tx)?(""):(" delta " + LU.td(tx))
				);

				px = p;
				tx = System.currentTimeMillis();
			}
		}
	}

	protected void fixInvoicesBatch(final GenCtx ctx, final List<Long> invoices)
	{
		nestTx(ctx, new Runnable()
		{
			public void run()
			{
				for(Long id : invoices)
					fixInvoice(ctx, id);
			}
		});
	}

	protected void fixInvoice(GenCtx ctx, Long id)
	{
		//~: fix the invoice
		Invoice invoice = session().load(Invoice.class, id);
		actionRun(Invoices.ACT_FIX, invoice, SYNCH_AGGR, true);
	}


	/* protected: invoices selecting */

	protected List<UnityType>  getInvoicesTypes(GenCtx ctx)
	{
		List<UnityType> r = new ArrayList<>(3);

		if(isBuy())
			r.add(Invoices.typeInvoiceBuy());

		if(isSell())
			r.add(Invoices.typeInvoiceSell());

		if(isMove())
			r.add(Invoices.typeInvoiceMove());

		return r;
	}

	protected String           nameInvoicesTypes(GenCtx ctx)
	{
		List<UnityType> ts = getInvoicesTypes(ctx);
		StringBuilder   sb = new StringBuilder(64);

		for(int i = 0;(i < ts.size());i++)
		{
			if(i != 0) sb.append("; ");
			sb.append(ts.get(i).getTitleLo());
		}

		return sb.toString();
	}

	protected List<Long>       selectInvoices(GenCtx ctx)
	{
		//?: {there are fixed invoices present} skip this generation
		if(foundFixedInvoices(ctx))
		{
			LU.I(ctx.log(), getClass().getSimpleName(),
			  " skips Invoices of types [", nameInvoicesTypes(ctx),
			  "] fixing as fixed ones were found."
			);

			return Collections.emptyList();
		}

		List<Long> res = selectAllInvoices(ctx);

		//?: {no invoices were found at all}
		if(res.isEmpty())
		{
			LU.W(ctx.log(), getClass().getSimpleName(),
			  " found no Invoices of types [",
			  nameInvoicesTypes(ctx), "]!"
			);

			return Collections.emptyList();
		}

		//~: choose random invoices of the all found
		res = chooseInvoices(ctx, res);
		EX.assertx(!res.isEmpty());

		LU.I(ctx.log(), getClass().getSimpleName(),
		  " found [", res.size(), "] Invoices of types [",
		  nameInvoicesTypes(ctx), "] to fix..."
		);

		return res;
	}

	protected boolean          foundFixedInvoices(GenCtx ctx)
	{
/*

 select count(id) from Invoice where (invoiceType in (:types)) and
   (domain = :domain) and (invoiceState.unity.unityType = :fixedType)

 */
		return ((Number) Q(

"select count(id) from Invoice where (invoiceType in (:types)) and\n" +
"  (domain = :domain) and (invoiceState.unity.unityType = :fixedType)"

		).
		  setParameterList("types",     getInvoicesTypes(ctx)).
		  setParameter    ("domain",    ctx.get(Domain.class)).
		  setParameter    ("fixedType", Invoices.typeInvoiceStateFixed()).
		  uniqueResult()).longValue() != 0;
	}

	@SuppressWarnings("unchecked")
	protected List<Long>       selectAllInvoices(GenCtx ctx)
	{

/*

 select id from Invoice where (invoiceType in (:types)) and
   (domain = :domain) and (invoiceState.unity.unityType = :editType)
 order by orderIndex

 */

		return (List<Long>) Q(

"select id from Invoice where (invoiceType in (:types)) and\n" +
"  (domain = :domain) and (invoiceState.unity.unityType = :editType)\n" +
"order by orderIndex"

		).
		  setParameterList("types",    getInvoicesTypes(ctx)).
		  setParameter    ("domain",   ctx.get(Domain.class)).
		  setParameter    ("editType", Invoices.typeInvoiceStateEdited()).
		  list();
	}

	protected List<Long>       chooseInvoices(GenCtx ctx, List<Long> invoices)
	{
		int n = (int)((invoices.size() * getPercent()) / 100.0);

		//?: {there is a limit defined and gained}
		if((getMaximum() != null) && (n > getMaximum()))
			n = getMaximum();
		if(n <= 0) n = 1;


		//~: create selection indices
		List<Integer> tmp = new ArrayList<>(invoices.size());
		for(int i = 0;(i < invoices.size());i++) tmp.add(i);
		Collections.shuffle(tmp, ctx.gen());
		tmp = tmp.subList(0, n);
		Collections.sort(tmp); //<-- to select in the invoices order

		//~: select random invoices (in their order)
		List<Long> res = new ArrayList<>(n);
		for(Integer i : tmp) res.add(invoices.get(i));

		return res;
	}
}