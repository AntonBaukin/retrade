package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/* com.tverts: self shunts */

import com.tverts.shunts.SelfShuntDescr;
import com.tverts.shunts.SelfShuntGroups;
import com.tverts.shunts.SelfShuntMethod;
import com.tverts.shunts.SelfShuntUnit;
import com.tverts.shunts.ShuntPlain;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Shunts {@link FirmOrder} and the related instances,
 * including general {@link Payment}s.
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit
@SelfShuntGroups({"retrade:payment", "retrade:payment:invoices",
 "retrade:payment:orders", "retrade:payment:orders:invoices"})
@SelfShuntDescr("Shunts Firm Orders for Bills of (buy-sell) Invoices.")
public class ShuntInvoicesPayments extends ShuntPlain
{
	/* shunt methods */

	@SelfShuntMethod(order = 0, critical = false, descrEn =
	  "Tests that Bills of all  buy-sell Invoices has their Firm Order."
	)
	public void testAllBillsHasFirmOrders()
	{
		final List<UnityType> ITYPES = Arrays.asList(
		  Invoices.typeInvoiceBuy(),
		  Invoices.typeInvoiceSell()
		);

/*

 select count(i.id) from InvoiceBill ib join ib.invoice i
 where (i.domain = :domain) and (ib.order is null) and
   (i.invoiceType in (:types))

 */
		long missing = ((Number) session().createQuery(

"select count(i.id) from InvoiceBill ib join ib.invoice i\n" +
"where (i.domain = :domain) and (ib.order is null) and\n" +
"  (i.invoiceType in (:types))"

		).
		  setParameter    ("domain", domain()).
		  setParameterList("types",  ITYPES).
		  uniqueResult()).longValue();

		EX.assertx(missing == 0,
		  "Found [", missing, "] buy-sell Invoices having Bills ",
		  "not included in any Firm Order!"
		);
	}

	@SelfShuntMethod(order = 1, critical = true)
	@SuppressWarnings("unchecked")
	public void testFirmOrdersTotals()
	{
/*

 select fo.id, sum(ib.income), sum(ib.expense) from
   InvoiceBill ib join ib.order fo
 where (fo.domain = :domain) and (ib.effective = true)
 group by fo.id

 */
		List<Object[]> orders = (List<Object[]>) session().createQuery(

"select fo.id, sum(ib.income), sum(ib.expense) from\n" +
"  InvoiceBill ib join ib.order fo\n" +
"where (fo.domain = :domain) and (ib.effective = true)\n" +
"group by fo.id"

		).
		  setParameter("domain", domain()).
		  list();

		//c: check totals of each bill
		for(Object[] rec : orders)
		{
			//~: load that firm order
			FirmOrder  fo = (FirmOrder) session().load(FirmOrder.class, (Long)rec[0]);
			BigDecimal oi = v(fo.getTotalIncome());
			BigDecimal oe = v(fo.getTotalExpense());
			BigDecimal si = v(rec[1]);
			BigDecimal se = v(rec[2]);

			//?: {totals are not equal}
			EX.assertx( CMP.eq(oi, si) && CMP.eq(oe, se),
			  "Firm Order [", rec[0], "] has (income, expense) = [",
			  oi, "; ", oe, "] != test query loaded [", si, "; ", se, "]!"
			);
		}
	}

	@SelfShuntMethod(order = 2, critical = true)
	@SuppressWarnings("unchecked")
	public void testPaymentsOrder()
	{
/*

 select p.id, p.time, p.orderIndex
 from Payment p where (p.domain = :domain)
 order by p.orderIndex

 */
		List<Object[]> payments = (List<Object[]>) Q(

"select p.id, p.time, p.orderIndex\n" +
"from Payment p where (p.domain = :domain)\n" +
"order by p.orderIndex"

		).
		  setParameter("domain", domain()).
		  list();

		if(payments.isEmpty()) LU.I(getLog(),
		  "no Payments were found in the system!"
		);

		//c: check the order comparing by the timestamp
		for(int i = 1;(i < payments.size());i++)
		{
			EX.assertx(
			  !payments.get(i - 1)[2].equals(payments.get(i)[2]),
			  "Same order index of Payments at [",
			  payments.get(i - 1)[0], ", ",
			  payments.get(i)[0], "]! (@cycle = ", i, ')'
			);

			Date dx = (Date) payments.get(i - 1)[1];
			Date dy = (Date) payments.get(i)[1];

			EX.assertx( !dx.after(dy),
			  "Wrong Payments order at [", payments.get(i - 1)[0], ", ",
			  payments.get(i)[0], "]! (@cycle = ", i, ')'
			);
		}
	}

	@SelfShuntMethod(order = 3, critical = true)
	@SuppressWarnings("unchecked")
	public void testPaymentsActuals()
	{

/*

 select o.id, o.actualIncome, o.actualExpense,
   sum(p.income), sum(p.expense)
 from Payment p join p.payOrder o
 where (p.domain = :domain)
 group by o.id, o.actualIncome, o.actualExpense

 */
		List<Object[]> orders = (List<Object[]>) Q(

"select o.id, o.actualIncome, o.actualExpense,\n" +
"  sum(p.income), sum(p.expense)\n" +
"from Payment p join p.payOrder o\n" +
"where (p.domain = :domain)\n" +
"group by o.id, o.actualIncome, o.actualExpense"

		).
		  setParameter("domain", domain()).
		  list();

		//c: for all the records
		for(Object[] row : orders)
		{
			BigDecimal I = v(row[1]);
			BigDecimal E = v(row[2]);
			BigDecimal i = v(row[3]);
			BigDecimal e = v(row[4]);

			EX.assertx(CMP.eq(I, i) && CMP.eq(E, e),
			  "Payment Order [", row[0], "] has wrong actual totals!"
			);
		}
	}


	/* private: shunt helpers */

	private static BigDecimal v(Object o)
	{
		if(o == null) return BigDecimal.ZERO;

		if(!(o instanceof BigDecimal))
			throw EX.arg("Not a money decimal!");
		BigDecimal n = (BigDecimal) o;

		EX.assertx(CMP.greZero(n), "Money component is below zero!");
		return n;
	}
}