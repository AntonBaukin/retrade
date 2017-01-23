package com.tverts.retrade.domain.core;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (accounts + sells) */

import com.tverts.retrade.domain.account.Account;
import com.tverts.retrade.domain.account.Accounts;
import com.tverts.retrade.domain.account.ActAccount;
import com.tverts.retrade.domain.account.GenTestPayWays;
import com.tverts.retrade.domain.account.GetAccount;
import com.tverts.retrade.domain.account.PayBank;
import com.tverts.retrade.domain.account.PayCash;
import com.tverts.retrade.domain.account.PaySelf;
import com.tverts.retrade.domain.account.PayWay;
import com.tverts.retrade.domain.sells.GetSells;
import com.tverts.retrade.domain.sells.PayDesk;
import com.tverts.retrade.domain.sells.SellsDesk;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;
import com.tverts.support.misc.Pair;


/**
 * Extends ReTrade Domain generation with creation of
 * test instances related to ReTrade application.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestReTradeDomain extends GenReTradeDomain
{
	/* public: constructor */

	public GenTestReTradeDomain()
	{
		//~: test Domain for default
		setTestDomain(true);
	}


	/* public: GenTestReTradeDomain (bean) interface */

	public int  getDaysBack()
	{
		return daysBack;
	}

	@Param
	public void setDaysBack(int daysBack)
	{
		EX.assertx(daysBack > 0);
		this.daysBack = daysBack;
	}


	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: set the back day
		setBackDay(ctx);

		//~: create the test domain
		super.generate(ctx);

		//~: create domain catalogues
		createDomainCatalogues(ctx);

		//~: create the accounts
		createDomainAccounts(ctx);

		//~: create sells payments desks
		createDomainPayDesks(ctx);

		//~: create sells desks (terminals)
		createDomainSellsDesks(ctx);
	}


	/* protected: generation steps */

	protected void    setBackDay(GenCtx ctx)
	{
		ctx.set(Date.class, DU.addDaysClean(new Date(), -getDaysBack()));
	}

	protected void    createDomainCatalogues(GenCtx ctx)
	{}


	/* protected: accounts generation */

	protected void    createDomainAccounts(GenCtx ctx)
	{
		//~: bank accounts
		createDomainBankAccount(ctx);

		//~: cash account
		createDomainCashAccount(ctx);
	}

	protected void    createDomainBankAccount(GenCtx ctx)
	{
		final String CODE = "40702";

		GetAccount ga = bean(GetAccount.class);

		//?: {this account already exists}
		if(ga.getOwnAccount(ctx.get(Domain.class).getPrimaryKey(), CODE) != null)
			return;

		//~: primary bank account
		Account a = createDomainAccount(ctx, CODE, "Расчётный счёт 40702",
		  "Расчётный счёт 40702 для операций с внешними контрагентами через банк.");

		//~: create bank payment way
		PayWay  w = GenTestPayWays.getInstance().
		  createTestPayBank(ctx, "Счёт 40702", null, "Тестовый домен ReTrade");

		//~: add the way to the account
		actionRun(ActAccount.ADD_WAY, a, ActAccount.PAY_WAY, w);
	}

	protected void    createDomainCashAccount(GenCtx ctx)
	{
		final String CODE0 = "50 (ВСЕ)";
		final String CODE1 = "50 (ОСН)";

		GetAccount ga = bean(GetAccount.class);

		//?: {this account already exists}
		if(ga.getOwnAccount(ctx.get(Domain.class).getPrimaryKey(), CODE0) != null)
			return;

		//~: cash desks account
		Account a0 = createDomainAccount(ctx, CODE0, "Кассы организации (все)",
		  "Балансовый счёт всех касс организации.");

		Account a1 = createDomainAccount(ctx, CODE1, "Кассы организации (основные)",
		  "Балансовый счёт всех основных касс организации.");

		//~: create 4 cash ways...
		for(int i = 1;(i <= 2);i++)
		{
			PayWay w0 = GenTestPayWays.getInstance().createTestPayCash(
			  ctx, "Кассы продаж №" + i + " (ОСН)",
			  "Основные кассы продаж в офисе " + i + ".");

			PayWay w1 = GenTestPayWays.getInstance().createTestPayCash(
			  ctx, "Кассы продаж №" + i + " (ДОП)",
			  "Дополнительные кассы продаж в офисе " + i + ".");

			//!: sell (income) only operations
			w0.setTypeFlag('I');
			w1.setTypeFlag('I');


			//~: add the ways to the accounts
			actionRun(ActAccount.ADD_WAY, a0, ActAccount.PAY_WAY, w0);
			actionRun(ActAccount.ADD_WAY, a0, ActAccount.PAY_WAY, w1);
			actionRun(ActAccount.ADD_WAY, a1, ActAccount.PAY_WAY, w0);
		}
	}

	/**
	 * The parameters of account creation are:
	 *  0) code; 1) name; 2) description.
	 */
	protected Account createDomainAccount(GenCtx ctx, String... p)
	{
		Account a = new Account();

		//~: primary key
		setPrimaryKey(session(), a, true);

		//~: test domain
		a.setDomain(ctx.get(Domain.class));

		//~: code
		a.setCode(p[0]);

		//~: name
		a.setName(p[1]);

		//~: description
		a.setRemarks(p[2]);

		//!: save it
		actionRun(ActionType.SAVE, a);

		return a;
	}


	/* protected: sell desks generation */

	protected void    createDomainPayDesks(GenCtx ctx)
	{
		//~: try to find existing desks
		List<PayDesk> desks = bean(GetSells.class).
		  getPayDesks(ctx.get(Domain.class).getPrimaryKey());

		if(!desks.isEmpty())
		{
			//~: attach to the genesis context
			ctx.set(PayDesk[].class, desks.toArray(new PayDesk[desks.size()]));

			LU.I(ctx.log(), "Found [", desks.size(), "] Payments Desks");
			return;
		}

		//~: load self payment destinations
		List<PaySelf> pays = bean(GetAccount.class).
		  getSelfPays(ctx.get(Domain.class).getPrimaryKey());

		EX.asserte(pays, "Domain has no Self Payments!");

		//~: select bank payments
		List<PaySelf> banks = new ArrayList<PaySelf>(2);
		for(PaySelf p : pays)
			if(p.getPayWay() instanceof PayBank)
				if(Accounts.isIncomeAllowed(p.getPayWay()))
					banks.add(p);

		EX.asserte(banks, "Domain has no Bank Payment Ways!");

		//~: select cash payments
		List<PaySelf> cashes = new ArrayList<PaySelf>(2);
		for(PaySelf p : pays)
			if(p.getPayWay() instanceof PayCash)
				if(Accounts.isIncomeAllowed(p.getPayWay()))
					cashes.add(p);

		EX.asserte(cashes, "Domain has no Cash Payment Ways!");

		//~: take all the pairs
		List<Pair<PaySelf, PaySelf>> pairs =
		  new ArrayList<Pair<PaySelf, PaySelf>>(4);

		for(PaySelf b : banks)
			for(PaySelf c : cashes)
				pairs.add(new Pair<PaySelf, PaySelf>(b, c));

		//~: shuffle them
		Collections.shuffle(pairs, ctx.gen());
		pairs = pairs.subList(0, 1 + ctx.gen().nextInt(pairs.size()));

		//~: generate some payment desks
		desks = new ArrayList<PayDesk>(2);
		for(int i = 0;(i < pairs.size());i++)
		{
			Pair<PaySelf, PaySelf> pair = pairs.get(i);
			PayDesk                desk = new PayDesk();

			//~: domain
			desk.setDomain(ctx.get(Domain.class));

			//~: pay bank
			desk.setPayBank(pair.getKey());

			//~: pay cash
			desk.setPayCash(pair.getValue());

			//~: name
			desk.setName("Терминал платежей №" + i);

			//~: remarks
			desk.setRemarks(SU.cats(
			  "Тестовый платёжный терминал №", i,
			  " для банковских карт с привязкой к '",
			  desk.getPayBank().getPayWay().getName(),
			  "' счёта №", desk.getPayBank().getAccount().getCode(),
			  " и кассовой привязки '",
			  desk.getPayCash().getPayWay().getName(),
			  "' счёта №", desk.getPayCash().getAccount().getCode(),
			  '.'
			));

			//~: open date (some days ago)
			desk.setOpenDate(DU.addDaysClean(
			  new Date(), -90 - ctx.gen().nextInt(365/2 - 90)
			));

			//!: save it
			desks.add(desk);
			actionRun(ActionType.SAVE, desk);
		}

		//~: attach to the genesis context
		ctx.set(PayDesk[].class, desks.toArray(new PayDesk[desks.size()]));
		LU.I(ctx.log(), "Found no Payments Desks, created: [", desks.size(), "]");
	}

	protected void    createDomainSellsDesks(GenCtx ctx)
	{
		//~: payments desks just generated
		PayDesk[] pays = ctx.get(PayDesk[].class);
		EX.asserte(pays);

		//~: try to find existing desks
		List<SellsDesk> desks = bean(GetSells.class).
		  getSellsDesks(ctx.get(Domain.class).getPrimaryKey());

		if(!desks.isEmpty())
		{
			//~: attach to the genesis context
			ctx.set(SellsDesk[].class, desks.toArray(new SellsDesk[desks.size()]));

			LU.I(ctx.log(), "Found [", desks.size(), "] Sells Desks (POS terminals)");
			return;
		}

		//c: for each payment desk: create up to 3 terminals
		desks = new ArrayList<SellsDesk>(2);
		for(int j = 0;(j < pays.length);j++)
			for(int i = 0, n = 1 + ctx.gen().nextInt(3);(i < n);i++)
			{
				SellsDesk desk = new SellsDesk();

				//~: domain
				desk.setDomain(ctx.get(Domain.class));

				//~: payments desk
				desk.setPayDesk(pays[j]);

				//~: code
				desk.setCode(SU.cats("К-", j+1 , '-', i+1));

				//~: name
				desk.setName(SU.cats("Касса-", j+1 , '-', i+1));

				//~: remarks
				desk.setRemarks(SU.cats("Тестовый терминал (касса) продаж №",
				  j+1 , '-', i+1, "для '", pays[j].getName(), "'."
				));


				//!: save it
				desks.add(desk);
				actionRun(ActionType.SAVE, desk);
			}

		//~: attach to the genesis context
		ctx.set(SellsDesk[].class, desks.toArray(new SellsDesk[desks.size()]));
		LU.I(ctx.log(), "Found no Sells Desks, created: [", desks.size(), "]");
	}


	/* private: parameters of the generator */

	private int daysBack = 10;
}