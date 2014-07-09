package com.tverts.retrade.domain.firm;

/* Java */

import java.net.URL;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;

/* com.tverts: endure (core + persons) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.person.FirmEntity;
import com.tverts.endure.person.GenTestFirms;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.Account;
import com.tverts.retrade.domain.account.ActAccount;
import com.tverts.retrade.domain.account.GenTestPayWays;
import com.tverts.retrade.domain.account.GetAccount;
import com.tverts.retrade.domain.account.PayWay;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Generates the test {@link Contractor}s.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestContractors extends GenTestFirms
{
	/* protected: generation */

	protected URL        getDataFile()
	{
		return EX.assertn(
		  getClass().getResource("GenTestContractors.xml"),
		  "No GenTestContractors.xml file found!"
		);
	}

	protected FirmEntity saveFirm(GenCtx ctx, GenState s)
	{
		//~: save the firm
		FirmEntity fe = super.saveFirm(ctx, s);

		//~: save the contractor
		Contractor c = saveContractor(ctx, fe);

		//~: create the accounts
		createContractorAccounts(ctx, c);

		return fe;
	}

	protected Contractor saveContractor(GenCtx ctx, FirmEntity fe)
	{
		Contractor c = new Contractor();

		//=: domain
		c.setDomain(fe.getDomain());

		//=: code
		c.setCode(fe.getCode());

		//=: name
		c.setName(fe.getName());

		//!: save it
		actionRun(ActContractor.SAVE, c);

		//~: log success
		if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
		  " created test contractor [", c.getCode(),
		  "], pkey [", c.getPrimaryKey(), "]"
		);

		return c;
	}

	protected void       createContractorAccounts(GenCtx ctx, Contractor c)
	{
		final String CODE = "40702+50";
		GetAccount   ga   = bean(GetAccount.class);

		//?: {this account already exists}
		if(ga.getFirmAccount(c.getPrimaryKey(), CODE) != null)
			return;

		//~: primary bank account
		Account a = createContractorAccount(ctx, c, CODE, "Объединённый счёт",
		  "Объединённый счёт для банковских и кассовых операций с контрагентом."
		);

		//~: create bank payment way
		createContractorPayBankWay(ctx, a);

		//~: cash payment way
		createContractorPayCashWay(ctx, a);
	}

	protected void       createContractorPayBankWay(GenCtx ctx, Account a)
	{
		//~: create bank payment way
		PayWay  w = GenTestPayWays.getInstance().createTestPayBank(ctx,
		  "Расчётный счёт", null, a.getContractor().getName());

		//~: add the way to the account
		actionRun(ActAccount.ADD_WAY, a, ActAccount.PAY_WAY, w);
	}

	protected void       createContractorPayCashWay(GenCtx ctx, Account a)
	{
		//~: create bank payment way
		PayWay  w = GenTestPayWays.getInstance().createTestPayCash(ctx,
		  "Касса организации", "Касса контрагента " + a.getContractor().getName());

		//~: add the way to the account
		actionRun(ActAccount.ADD_WAY, a, ActAccount.PAY_WAY, w);
	}

	/**
	 * The parameters of account creation are:
	 *  0) code; 1) name; 2) description.
	 */
	protected Account    createContractorAccount(GenCtx ctx, Contractor c, String... p)
	{
		Account a = new Account();

		//~: primary key
		setPrimaryKey(session(), a, true);

		//~: test domain
		a.setDomain(ctx.get(Domain.class));

		//~: contractor
		a.setContractor(c);

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
}