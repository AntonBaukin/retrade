package com.tverts.retrade.domain.firm;

/* SAX Parser */

import javax.xml.parsers.SAXParserFactory;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

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

import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.Account;
import com.tverts.retrade.domain.account.ActAccount;
import com.tverts.retrade.domain.account.GenTestPayWays;
import com.tverts.retrade.domain.account.GetAccount;
import com.tverts.retrade.domain.account.PayWay;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Generates the test {@link Contractor}s.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestContractors extends GenesisHiberPartBase
{
	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: create the test contractor + firm
		try
		{
			createTestContractors(ctx);
		}
		catch(Exception e)
		{
			throw new GenesisError(e, this, ctx);
		}
	}


	/* protected: test instances generation & verification */

	protected void    createTestContractors(GenCtx ctx)
	  throws Exception
	{
		GetContractor       getcont = bean(GetContractor.class);
		ReadTestContractors handler = new ReadTestContractors(ctx);

		//!: invoke the sax parser
		SAXParserFactory.newInstance().newSAXParser().parse(
		  GenTestContractors.class.getResource("GenTestContractors.xml").
		    toURI().toString(), handler
		);

		//c: for all the contractors generated
		for(Contractor c : handler.getResult())
		{
			//~: set primary key
			setPrimaryKey(session(), c, true);

			//~: assign test domain
			c.setDomain(ctx.get(Domain.class));

			//?: {the contractor is already exist} do nothing
			if(getcont.getContractor(c.getDomain(), c.getCode()) != null)
				continue;

			//!: save the contractor
			actionRun(ActContractor.SAVE, c, ActContractor.SAVE_FIRM, true);

			//~: create the accounts
			createContractorAccounts(ctx, c);

			//~: log success
			if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
			  " had created Test Contractor ", c.getCode(),
			  " with PK = ", c.getPrimaryKey()
			);
		}
	}

	protected void    createContractorAccounts(GenCtx ctx, Contractor c)
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

	protected void    createContractorPayBankWay(GenCtx ctx, Account a)
	{
		//~: create bank payment way
		PayWay  w = GenTestPayWays.getInstance().createTestPayBank(ctx,
		  "Расчётный счёт", null, a.getContractor().getName());

		//~: add the way to the account
		actionRun(ActAccount.ADD_WAY, a, ActAccount.PAY_WAY, w);
	}

	protected void    createContractorPayCashWay(GenCtx ctx, Account a)
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
	protected Account createContractorAccount(GenCtx ctx, Contractor c, String... p)
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