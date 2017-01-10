package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: self shunts */

import com.tverts.shunts.SelfShuntDescr;
import com.tverts.shunts.SelfShuntGroups;
import com.tverts.shunts.SelfShuntMethod;
import com.tverts.shunts.SelfShuntUnit;
import com.tverts.shunts.ShuntPlain;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (core + aggregation) */

import com.tverts.endure.UnityType;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.aggr.GetAggrValue;
import com.tverts.endure.aggr.volume.DatePeriodVolumeCalcItem;
import com.tverts.endure.aggr.volume.GetAggrVolume;

/* com.tverts: retrade domain (firms + invoices) */

import com.tverts.retrade.domain.firm.Contractors;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.misc.Pair;


/**
 * Shunts {@link InvoiceBill} instances and the debt aggregated values.
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit
@SelfShuntGroups({"retrade:payment", "retrade:payment:bills",
  "retrade:aggr", "retrade:aggr:payment"})
@SelfShuntDescr("Shunts Bills of buy-sell Invoices.")
public class ShuntInvoicesBills extends ShuntPlain
{
	/* shunt methods */

	@SelfShuntMethod(order = 0, critical = true)
	public void testInvoicesHaveBills()
	{
		final List<UnityType> ITYPES = Arrays.asList(
		  Invoices.typeInvoiceBuy(),
		  Invoices.typeInvoiceSell()
		);

/*

select i.id, ib.id from
  InvoiceBill ib right outer join ib.invoice i
where (i.domain = :domain) and
  (i.invoiceType in (:invoiceTypes)) and
  (i.invoiceState.unity.unityType = :stateType)

*/
		List rows = session().createQuery(

"select i.id, ib.id from\n" +
"  InvoiceBill ib right outer join ib.invoice i\n" +
"where (i.domain = :domain) and\n" +
"  (i.invoiceType in (:invoiceTypes)) and\n" +
"  (i.invoiceState.unity.unityType = :stateType)"

		).
		  setParameter("domain", domain()).
		  setParameterList("invoiceTypes", ITYPES).
		  setParameter("stateType", Invoices.typeInvoiceStateFixed()).
		  list();

		for(Object row : rows)
		{
			Long   i  = (Long)((Object[])row)[0];
			Object ib = ((Object[])row)[1];

			EX.assertn(ib, "Invoice [", i, "] in Fixed State has no Bill!");
		}

	}

	@SelfShuntMethod(order = 1, critical = true)
	public void testInvoiceBillsAmounts()
	{
		List bills = session().
		  createQuery("select id from InvoiceBill").
		  list();

		for(Object id : bills)
		{
			InvoiceBill ib = (InvoiceBill) session().
			  load(InvoiceBill.class, (Long)id);
			session().setReadOnly(ib, true);

			BigDecimal  gc = EX.assertn(
			  Invoices.getInvoiceGoodsCost(ib.getInvoice())
			);

			//~: check the invoice state Vs bill effectiveness
			if(Invoices.isInvoiceFixed(ib.getInvoice()))
			{
				EX.assertx( ib.isEffective(),

				  "Invoice Bill [", ib.getPrimaryKey(), "] for fixed Invoice [",
				  ib.getInvoice().getPrimaryKey(), "] is not effective!"
				);
			}
			else if(Invoices.isInvoiceEdited(ib.getInvoice()))
			{
				EX.assertx( !ib.isEffective(),

				  "Invoice Bill [", ib.getPrimaryKey(), "] for edited Invoice [",
				  ib.getInvoice().getPrimaryKey(), "] is still effective!"
				);

				EX.assertx(ib.getExpense() == null);
				EX.assertx(ib.getIncome()  == null);

				continue;
			}
			else throw EX.ass(
			  "Invoice [", ib.getInvoice().getPrimaryKey(),
			  "] with Invoice Bill [", ib.getPrimaryKey(),
			  "] has wrong state!"
			);

			//~: compare the expense-income amounts
			if(Invoices.isBuyInvoice(ib.getInvoice()))
			{
				EX.assertn(ib.getExpense());
				EX.assertx(ib.getIncome() == null);

				EX.assertx(
				  gc.compareTo(ib.getExpense()) == 0,

				  "Invoice Bill [", ib.getPrimaryKey(), "] for Invoice [",
				  ib.getInvoice().getPrimaryKey(), "] has wrong expence value [",
				  ib.getExpense().toString(), "] vs actual [", gc.toString(), "]!"
				);
			}
			else if(Invoices.isSellInvoice(ib.getInvoice()))
			{
				EX.assertx(ib.getExpense() == null);
				EX.assertn(ib.getIncome());

				EX.assertx(
				  gc.compareTo(ib.getIncome()) == 0,

				  "Invoice Bill [", ib.getPrimaryKey(), "] for Invoice [",
				  ib.getInvoice().getPrimaryKey(), "] has wrong income value [",
				  ib.getIncome().toString(), "] vs actual [", gc.toString(), "]!"
				);
			}
			else throw EX.ass(
			  "Invoice [", ib.getInvoice().getPrimaryKey(),
			  "] with Invoice Bill [", ib.getPrimaryKey(),
			  "] has wrong type!"
			);
		}
	}

	@SelfShuntMethod(order = 2, critical = true)
	@SuppressWarnings("unchecked")
	public void testContractorsDebts()
	{

/*

select ib.id from InvoiceBill ib where
  (ib.invoice.domain.id = :domain)

*/

		//~: load bills of the domain
		List bills = session().createQuery(

"select ib.id from InvoiceBill ib where\n" +
"  (ib.invoice.domain.id = :domain)"

		).
		  setParameter("domain", ctx().getDomain()).
		  list();

		//~: contractor -> summary (buy-expense, sell-income) bills
		Map<Long, Pair<BigDecimal, BigDecimal>> summary =
		  new HashMap<Long, Pair<BigDecimal, BigDecimal>>(17);

		//~: coded year & month first day -> contractor -> summary (...)
		Map<Integer, Map<Long, Pair<BigDecimal, BigDecimal>>> monthly =
		  new HashMap<Integer, Map<Long, Pair<BigDecimal, BigDecimal>>>(11);


		//~: collect the summary of the bills
		for(Object id : bills)
		{
			session().clear();

			InvoiceBill ib = (InvoiceBill) session().
			  load(InvoiceBill.class, (Long)id);

			if(!ib.isEffective())
				continue;

			EX.assertx(Invoices.isInvoiceFixed(ib.getInvoice()));
			EX.assertn(ib.getContractor());

			//~: add to summary debts
			addToSummary(summary, ib);

			//~: add to monthly summary

			Integer ym = getYearMonth(ib);
			Map<Long, Pair<BigDecimal, BigDecimal>> ms = monthly.get(ym);
			if(ms == null) monthly.put(ym, ms =
			  new HashMap<Long, Pair<BigDecimal, BigDecimal>>(17));

			addToSummary(ms, ib);
		}

/*

select co.id from Contractor co where
  (co.domain.id = :domain)

*/

		//~: load contractors of the domain
		List cids = session().createQuery(

"select co.id from Contractor co where\n" +
"  (co.domain.id = :domain)"

		).
		  setParameter("domain", ctx().getDomain()).
		  list();

		GetAggrValue  gav = bean(GetAggrValue.class);
		GetAggrVolume gvo = bean(GetAggrVolume.class);


		//<: check the aggregated debts of the contractors

		for(Object cid : cids)
		{
			//<: compare the aggregated values

			AggrValue av = EX.assertn(
			  gav.getAggrValue((Long)cid, Contractors.AGGRVAL_CONTRACTOR_DEBT, null),

			  "Contractor [", cid, "] has no Debt Aggregated Value!"
			);

			BigDecimal avin = av.getAggrPositive(); //<-- sold
			BigDecimal avex = av.getAggrNegative(); //<-- bought (the debt)

			Pair<BigDecimal, BigDecimal> sp = summary.get(cid);
			BigDecimal mpin = (sp == null)?(null):(sp.getValue()); //<-- sold
			BigDecimal mpex = (sp == null)?(null):(sp.getKey());   //<-- bought

			if(avin == null) avin = BigDecimal.ZERO;
			if(avex == null) avex = BigDecimal.ZERO;
			if(mpin == null) mpin = BigDecimal.ZERO;
			if(mpex == null) mpex = BigDecimal.ZERO;

			EX.assertx(
			  mpin.compareTo(avin) == 0,

			  "Contractor's [", cid, "] Debt aggregated value [",
			  av.getPrimaryKey(), "] has wrong Income sum: saved [",
			  avin, "] Vs actual [", mpin, "]!"
			);

			EX.assertx(
			  mpex.compareTo(avex) == 0,

			  "Contractor's [", cid, "] Debt aggregated value [",
			  av.getPrimaryKey(), "] has wrong Expense sum: saved [",
			  avex, "] Vs actual [", mpex, "]!"
			);

			BigDecimal mpbl = mpin.subtract(mpex);
			BigDecimal avbl = av.getAggrValue();
			if(avbl == null) avbl = BigDecimal.ZERO;

			EX.assertx(mpbl.compareTo(avbl) == 0);

			//>: compare the aggregated values


			//<: compare monthly volumes

			//~: map the volumes
			List<DatePeriodVolumeCalcItem> mvcis = gvo.getMonthVolumeCalcItems(av);
			Map<Integer, Pair<BigDecimal, BigDecimal>> mvols =
			  new HashMap<Integer, Pair<BigDecimal, BigDecimal>>(mvcis.size());

			for(DatePeriodVolumeCalcItem mvci : mvcis)
				mvols.put(getYearMonth(mvci), new Pair<BigDecimal, BigDecimal>(
				  mvci.getVolumeNegative(), mvci.getVolumePositive()));

			//c: for all the months of invoices
			for(Integer my : monthly.keySet())
				//?: {the volumes are not defined}
				if(!monthly.get(my).containsKey((Long)cid))
				{
					Pair<BigDecimal, BigDecimal> vp = mvols.get(my);
					if(vp == null)  continue;

					int k = BigDecimal.ZERO.compareTo(vp.getKey());
					int v = BigDecimal.ZERO.compareTo(vp.getValue());

					EX.assertx(
					  (k == 0) && (v == 0),

					  "Year [", year(my), "] month day: [", day(my),
					  "] Aggr Value's [", av.getPrimaryKey(),
					  "] Monthly Volume Calc Item must be undefined or have zero values!"
					);
				}
				//!: the volumes are defined
				else
				{
					//~: get aggregated values
					Pair<BigDecimal, BigDecimal> xp = mvols.get(my);
					if(xp == null) xp = new Pair<BigDecimal, BigDecimal>(
					  BigDecimal.ZERO, BigDecimal.ZERO);

					avin = xp.getValue(); //<-- sold
					avex = xp.getKey();   //<-- bought (the debt)

					//~: get test calculated values
					Pair<BigDecimal, BigDecimal> yp = monthly.get(my).get((Long)cid);

					mpin = yp.getValue(); //<-- sold
					mpex = yp.getKey();   //<-- bought

					if(avin == null) avin = BigDecimal.ZERO;
					if(avex == null) avex = BigDecimal.ZERO;
					if(mpin == null) mpin = BigDecimal.ZERO;
					if(mpex == null) mpex = BigDecimal.ZERO;


					EX.assertx(
					  mpin.compareTo(avin) == 0,

					  "Contractor's [", cid, "] Debt Aggregated value's [",
					  av.getPrimaryKey(), "] Monthly (year ", year(my), ", month day ",
					  day(my), ") Volume has wrong Income (+) sum: saved [",
					  avin, "] Vs actual [", mpin, "]!"
					);

					EX.assertx(
					  mpex.compareTo(avex) == 0,

					  "Contractor's [", cid, "] Debt Aggregated value's [",
					  av.getPrimaryKey(), "] Monthly (year ", year(my), ", month day ",
					  day(my), ") Volume has wrong Expense (-) sum: saved [",
					  avex, "] Vs actual [", mpex, "]!"
					);
				}

			//>: compare monthly volumes
		}

		//>: check the aggregated debts of the contractors
	}


	/* private: support */

	private static Integer getYearMonth(InvoiceBill ib)
	{
		return getDateYearMonth(ib.getInvoice().getInvoiceDate());
	}

	private static Integer getYearMonth(DatePeriodVolumeCalcItem mvci)
	{
		return (mvci.getYear() << 10) | mvci.getDay();
	}

	private static Integer getDateYearMonth(Date date)
	{
		Calendar cl =  Calendar.getInstance();
		cl.setTime(date);

		return (cl.get(Calendar.YEAR) << 10) |
		  DU.monthDay(cl.get(Calendar.YEAR), cl.get(Calendar.MONTH));
	}

	private static int     year(int ydcode)
	{
		return ydcode >> 10;
	}

	private static int     day(int ydcode)
	{
		return ydcode & 0x3FF;
	}

	private static void    addToSummary
	  (Map<Long, Pair<BigDecimal, BigDecimal>> summary, InvoiceBill ib)
	{
		Pair<BigDecimal, BigDecimal> sp =
		  summary.get(ib.getContractor().getPrimaryKey());

		if(sp == null) summary.put(
		  ib.getContractor().getPrimaryKey(), sp =
		    new Pair<BigDecimal, BigDecimal>(BigDecimal.ZERO, BigDecimal.ZERO));

		BigDecimal gc = Invoices.getInvoiceGoodsCost(ib.getInvoice());

		if(Invoices.isBuyInvoice(ib.getInvoice()))
			sp.setKey(sp.getKey().add(gc));
		if(Invoices.isSellInvoice(ib.getInvoice()))
			sp.setValue(sp.getValue().add(gc));
	}
}