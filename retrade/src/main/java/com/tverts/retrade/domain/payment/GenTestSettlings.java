package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.GetAccount;
import com.tverts.retrade.domain.account.PayIt;
import com.tverts.retrade.domain.account.PayFirm;
import com.tverts.retrade.domain.account.PaySelf;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Generates test Settling Payments for Firm Orders
 * (of Buy-Sell Invoice Bills).
 *
 * This implementation creates 1 to 3 payments for
 * not payed orders, and adds 1 payment to that having
 * total income-expense amounts greater than the actual.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestSettlings extends GenesisHiberPartBase
{
	/* public: Genesis interface */

	@SuppressWarnings("unchecked")
	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		final List<UnityType> ORDER_TYPES = Arrays.asList(
		  Payments.typeFirmOrderExpense(),
		  Payments.typeFirmOrderIncome()
		);

/*

 select id from FirmOrder where
 (unity.unityType in (:types)) and (domain = :domain)
   and
 ((
    (totalIncome is not null) and
    ((actualIncome is null) or (actualIncome < totalIncome))
  )
   or
  (
    (totalExpense is not null) and
    ((actualExpense is null) or (actualExpense < totalExpense))
  )
 )

 */
		List<Long> fos = (List<Long>) Q(

"select id from FirmOrder where\n" +
"(unity.unityType in (:types)) and (domain = :domain)\n" +
"  and\n" +
"((\n" +
"   (totalIncome is not null) and\n" +
"   ((actualIncome is null) or (actualIncome < totalIncome))\n" +
" )\n" +
"  or\n" +
" (\n" +
"   (totalExpense is not null) and\n" +
"   ((actualExpense is null) or (actualExpense < totalExpense))\n" +
" )\n" +
")"

		).
		  setParameterList("types",  ORDER_TYPES).
		  setParameter    ("domain", ctx.get(Domain.class)).
		  list();

		LU.I(ctx.log(), "found [", fos.isEmpty()?("NO"):(fos.size()),
		   "] Firm Orders to include in Settling Payments.");

		//c: for all the orders found
		for(Long id : fos)
		{
			generate(ctx, (FirmOrder) session().load(FirmOrder.class, id));
			//flush(session());
		}
	}


	/* protected: generation */

	protected void         generate(GenCtx ctx, FirmOrder fo)
	{
		//~: check the total income-expense presence
		EX.assertx(
		  ((fo.getTotalIncome() != null) && (fo.getTotalExpense() == null)) ||
		  ((fo.getTotalIncome() == null) && (fo.getTotalExpense() != null)),

		  "Firm Order [", fo.getPrimaryKey(),
		  "] has total expense and income defined simultaneously!"
		);

		//~: generate the payment amounts
		BigDecimal[] pays = genPayAmounts(ctx, fo);

		//~: create payments
		for(BigDecimal pay : pays)
			genPayment(ctx, fo, pay);
	}

	protected void         genPayment(GenCtx ctx, FirmOrder fo, BigDecimal pay)
	{
		Settling s = new Settling();

		//~: domain
		s.setDomain(fo.getDomain());

		//~: pay self
		s.setPaySelf(selectPaySelf(ctx, fo));

		//~: pay firm
		s.setPayFirm(selectPayFirm(ctx, fo));

		//~: firm order
		s.setPayOrder(fo);

		//~: time within that day
		Date time = DU.cleanTime(fo.getTime());
		time.setTime(time.getTime() + ctx.gen().nextInt(1000 * 60 * 60 * 24));
		s.setTime(time);

		//~: code
		int payments = bean(GetAccount.class).getPayments(fo.getPrimaryKey()).size();
		s.setCode("ПЛ-" + (payments + 1) + '-' + fo.getCode());

		//~: remarks
		s.setRemarks("Платёж №" + (payments + 1) + " по ордеру " + fo.getCode());

		//~: income
		if(fo.getTotalIncome() != null)
			s.setIncome(pay.setScale(2, BigDecimal.ROUND_UP));

		//~: expense
		if(fo.getTotalExpense() != null)
			s.setExpense(pay.setScale(2, BigDecimal.ROUND_UP));


		//!: save the settling
		actionRun(ActionType.SAVE, s,
		  Payments.PAYMENT_AUTO_ORDER, true
		);
	}

	protected PaySelf      selectPaySelf(GenCtx ctx, FirmOrder fo)
	{
		//~: select all self payments
		return (PaySelf) selectPayIt(ctx, fo, "Self",
		  bean(GetAccount.class).getSelfPays(ctx.get(Domain.class).getPrimaryKey())
		);
	}

	protected PayFirm      selectPayFirm(GenCtx ctx, FirmOrder fo)
	{
		//~: select all self payments
		return (PayFirm) selectPayIt(ctx, fo,
		  "Firm for Contractor [" + fo.getContractor().getPrimaryKey() + ']',
		  bean(GetAccount.class).getFirmPays(fo.getContractor())
		);
	}

	protected PayIt        selectPayIt
	  (GenCtx ctx, FirmOrder fo, String paycls, List<? extends PayIt> pays)
	{
		EX.assertx(!pays.isEmpty(), "Domain [",
		  ctx.get(Domain.class).getPrimaryKey(),
		  "] has no ", paycls, " Payments!"
		);

		//~: exclude that are of wrong direction
		char xtype;

		if(fo.getTotalExpense() != null)
			xtype = 'I'; //<-- income only
		else if(fo.getTotalIncome() != null)
			xtype = 'E';
		else throw EX.state();

		for(Iterator<? extends PayIt> i = pays.iterator();(i.hasNext());)
			if(xtype == i.next().getPayWay().getTypeFlag())
				i.remove();

		EX.assertx(!pays.isEmpty(), "Domain [",
		  ctx.get(Domain.class).getPrimaryKey(),
		  "] has no ", paycls, " Payments available for ",
		  (fo.getTotalExpense() != null)?("expense"):("income"), '!'
		);

		return pays.get(ctx.gen().nextInt(pays.size()));
	}

	protected BigDecimal[] genPayAmounts(GenCtx ctx, FirmOrder fo)
	{
		//?: {there are payments already present}
		boolean repeated = (fo.getActualExpense() != null) ||
		  (fo.getActualIncome() != null);

		return genPayAmounts(ctx, fo, (repeated)?(1):(1 + ctx.gen().nextInt(3)));
	}

	protected BigDecimal[] genPayAmounts(GenCtx ctx, FirmOrder fo, int n)
	{
		//~: get delta from actual to total
		BigDecimal d = null;

		if(fo.getTotalIncome() != null)
			d = fo.getTotalIncome().subtract(n(fo.getActualIncome()));

		if(fo.getTotalExpense() != null)
		{
			EX.assertx(d == null);
			d = fo.getTotalExpense().subtract(n(fo.getActualExpense()));
		}

		if(d == null)      throw EX.state();
		if(!CMP.grZero(d)) throw EX.state();

		//~: view on the delta as an integer
		long   i = d.unscaledValue().longValue();
		if(i < n) n = (int)i;

		//~: separate payments into (almost) equal parts
		long   s = 0;
		long[] r = new long[n];
		for(int j = 0;(j < n);j++)
		{
			r[j] = i/n; s += r[j];
			EX.assertx(r[j] > 0);
		}

		while(s < i)
		{
			r[ctx.gen().nextInt(n)]++;
			s++;
		}

		EX.assertx(s == i);

		//~: now, variate the payments
		int vn = ctx.gen().nextInt(n * 2);
		for(int j = 0;(j < vn);j++)
		{
			int  k = ctx.gen().nextInt(n);
			int  l = ctx.gen().nextInt(n);
			if(k == l) continue;

			int  x = ctx.gen().nextInt(
			  (r[k]/2 + 1 < Integer.MAX_VALUE)?(int)(1 + r[k]/2):(Integer.MAX_VALUE));
			if(x >= r[k]) x = (int)(r[k] - 1);
			if(x <= 0) continue;
			r[k] -= x; r[l] += x;
		}

		//~: check the summary
		s = 0; for(int j = 0;(j < n);j++) s += r[j];
		EX.assertx(s == i);

		//~: check the amounts
		for(int j = 0;(j < n);j++)
			EX.assertx(r[j] > 0);


		//~: now, convert them back to decimals
		EX.assertx(d.scale() >= 0);
		BigDecimal[] R = new BigDecimal[r.length];
		for(int j = 0;(j < n);j++)
			R[j] = new BigDecimal(r[j]).scaleByPowerOfTen(-d.scale());

		//~: check their sum is the delta
		BigDecimal S = BigDecimal.ZERO;
		for(int j = 0;(j < n);j++)
			S = S.add(R[j]);
		EX.assertx(CMP.eq(d, S));

		return R;
	}

	private BigDecimal     n(BigDecimal n)
	{
		n = (n == null)?(BigDecimal.ZERO):(n);
		if(BigDecimal.ZERO.compareTo(n) > 0) throw EX.state();
		return n;
	}
}