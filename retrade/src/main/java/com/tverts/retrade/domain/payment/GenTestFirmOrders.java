package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Hibernate Persistence Layer */

import org.hibernate.type.DateType;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Generates test {@link FirmOrder}s for Buy-Sell Invoices.
 * Each Order includes from 1 to {@link #getMaxBills()}
 * Invoice Bills.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestFirmOrders extends GenesisHiberPartBase
{
	/* public: GenTestFirmOrders (parameters) interface */

	public int  getMaxBills()
	{
		return maxBills;
	}

	public void setMaxBills(int maxBills)
	{
		if(maxBills < 1) throw EX.arg();
		this.maxBills = maxBills;
	}


	/* public: Genesis interface */

	@SuppressWarnings("unchecked")
	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		final List<UnityType> ITYPES = Arrays.asList(
		  Invoices.typeInvoiceBuy(),
		  Invoices.typeInvoiceSell()
		);


		//~: select bills having no order of buy-sell invoices
/*

 select i.invoiceDate, i.invoiceType.id, ib.id, ib.contractor.id from
   InvoiceBill ib join ib.invoice i
 where (i.domain = :domain) and (ib.order is null) and
   (i.invoiceType in (:types))
 order by i.invoiceDate

 */
		List<Object[]> recs = (List<Object[]>) Q(

"select i.invoiceDate, i.invoiceType.id, ib.id, ib.contractor.id from\n" +
"  InvoiceBill ib join ib.invoice i\n" +
"where (i.domain = :domain) and (ib.order is null) and\n" +
"  (i.invoiceType in (:types))\n" +
"order by i.invoiceDate"

		).
		  setParameter    ("domain", ctx.get(Domain.class)).
		  setParameterList("types",  ITYPES).
		  list();

		//maps: Contractor -> list of Invoice Bills
		Map<Long, List<Long>> sells = new HashMap<Long, List<Long>>(17);
		Map<Long, List<Long>> buys  = new HashMap<Long, List<Long>>(17);
		Map<Long, Date>       sdays = new HashMap<Long, Date>(17);
		Map<Long, Date>       bdays = new HashMap<Long, Date>(17);

		//c: for all the records (in the date order)
		for(Object[] rec : recs)
		{
			Date day = DU.cleanTime((Date)rec[0]);
			Long cid = (Long) rec[3];

			//~: select map by the invoice type
			Map<Long, List<Long>> map;
			if(Invoices.typeInvoiceSell().getPrimaryKey().equals(rec[1]))
				{ map = sells; sdays.put(cid, day); }
			else if(Invoices.typeInvoiceBuy().getPrimaryKey().equals(rec[1]))
				{ map = buys;  bdays.put(cid, day); }
			else throw EX.state();

			//~: add by the contractor
			List<Long> bills = map.get(cid);
			if(bills == null)
				map.put(cid, bills = new ArrayList<Long>(getMaxBills()));
			bills.add((Long) rec[2]);

			//?: {got not enough objects} take the next record
			if(bills.size() < getMaxBills()) continue;

			//!: generate the order
			genOrders(ctx, cid, day, bills, (map == sells)?
			  (Invoices.typeInvoiceSell()):(Invoices.typeInvoiceBuy())
			);

			//~: clear the bills
			bills.clear();
		}

		//~: take the rests of the sells map
		for(Long cid : sells.keySet())
		{
			List<Long> bills = sells.get(cid);
			if(bills.isEmpty()) continue;

			genOrders(ctx, cid, sdays.get(cid), bills, Invoices.typeInvoiceSell());
		}

		//~: take the rests of the buys map
		for(Long cid : buys.keySet())
		{
			List<Long> bills = buys.get(cid);
			if(bills.isEmpty()) continue;

			genOrders(ctx, cid, bdays.get(cid), bills, Invoices.typeInvoiceBuy());
		}
	}


	/* protected: generation fragments */

	protected void genOrders
	  (GenCtx ctx, Long cid, Date day, List<Long> bills, UnityType itype)
	{
		//~: load the contractor
		Contractor con = (Contractor) session().load(Contractor.class, cid);
		if(!con.getDomain().equals(ctx.get(Domain.class))) throw EX.state();

		//~: create new firm order
		FirmOrder fo = new FirmOrder();

		//~: primary key
		setPrimaryKey(session(), fo, true);

		//~: domain
		fo.setDomain(ctx.get(Domain.class));

		//~: contractor
		fo.setContractor(con);

		//~: form order type (passed to the save action)
		UnityType otype; if(Invoices.typeInvoiceSell().equals(itype))
			otype = Payments.typeFirmOrderIncome();
		else if(Invoices.typeInvoiceBuy().equals(itype))
			otype = Payments.typeFirmOrderExpense();
		else throw EX.state();

		//~: time
		fo.setTime(day);

		//<: generate code

/*

 select count(fo.id) from FirmOrder fo where
   (fo.domain = :domain) and (fo.time between :minTime and :maxTime)

 */
		int count = ((Number) Q(

"select count(fo.id) from FirmOrder fo where\n" +
"  (fo.domain = :domain) and (fo.time between :minTime and :maxTime)"

		).
		  setParameter("domain",  ctx.get(Domain.class)).
		  setParameter("minTime", DU.cleanTime(day), DateType.INSTANCE).
		  setParameter("maxTime", DU.lastTime(day), DateType.INSTANCE).
		  uniqueResult()).intValue();

		fo.setCode(SU.cats(
		  Payments.typeFirmOrderIncome().equals(otype)?("РАСХ-"):("ПРИХ-"),
		  DU.date2str(day), "/", (count + 1)
		));

		//>: generate code

		//<: generate remarks

		StringBuilder r = new StringBuilder(128).
		  append("Тестовая генерация ").
		  append(Payments.typeFirmOrderIncome().equals(otype)?
		    ("приходного"):("расходного")).
		  append(" кассового ордера для даты ").append(DU.date2str(day)).
		  append(" с накладными: ");

		for(int i = 0;(i < bills.size());i++)
		{
			InvoiceBill ib = (InvoiceBill) session().
			  load(InvoiceBill.class, bills.get(i));
			Invoice     iv = ib.getInvoice();
			if(!iv.getDomain().equals(ctx.get(Domain.class))) throw EX.state();

			if(i != 0) r.append(", ");
			r.append(iv.getCode());
		}

		//~: assign the remarks
		fo.setRemarks(r.toString());

		//>: generate remarks


		//!: save the order
		actionRun(ActionType.SAVE, fo, Payments.PAY_ORDER_TYPE, otype);

		//~: adds the bills selected to the order
		for(Long bid : bills)
		{
			InvoiceBill ib = (InvoiceBill) session().load(InvoiceBill.class, bid);

			actionRun(Payments.FIRM_ORDER_ADD_BILL, fo,
			  Payments.INVOICE_BILL, ib
			);
		}

		//~: log
		LU.I(ctx.log(), "created Firm Order [", fo.getPrimaryKey(),
		  "] code [", fo.getCode(), "] for Contractor [", cid,
		  "] with [", bills.size(), "] invoices"
		);
	}


	/* parameters */

	private int maxBills = 4;
}