package com.tverts.retrade.exec.api.accounts;

/* standard Java classes */

import java.util.Date;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.Account;
import com.tverts.retrade.domain.account.GetAccount;
import com.tverts.retrade.domain.account.PayBank;
import com.tverts.retrade.domain.account.PayCash;
import com.tverts.retrade.domain.account.PayFirm;
import com.tverts.retrade.domain.account.PayIt;
import com.tverts.retrade.domain.account.PaySelf;
import com.tverts.retrade.domain.account.PayWay;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Finds or creates the default Accounts and the related
 * Payment Ways for the Contractor given, or for the Domain.
 *
 * @author anton.baukin@gmail.com.
 */
public class EnsureDefaults
{
	/* public: constructors */

	public static final String DEFAULT_CODE = "По умолчанию";

	public EnsureDefaults(Contractor contractor, String code)
	{
		this.domain     = contractor.getDomain();
		this.contractor = contractor;
		this.code       = SU.sXe(code)?(DEFAULT_CODE):(code);
	}

	public EnsureDefaults(Domain domain, String code)
	{
		this.domain     = domain;
		this.contractor = null;
		this.code       = SU.sXe(code)?(DEFAULT_CODE):(code);
	}


	/* public: EnsureDefaults interface */

	public void    ensure()
	{
		//?: {contractor is not defined} ensure self accounts
		if(contractor == null)
			ensureSelf();
		else
			ensureFirm();
	}

	public PaySelf getPaySelfCash()
	{
		return paySelfCash;
	}

	public PaySelf getPaySelfBank()
	{
		return paySelfBank;
	}

	public PayFirm getPayFirmCash()
	{
		return payFirmCash;
	}

	public PayFirm getPayFirmBank()
	{
		return payFirmBank;
	}


	/* protected: ensure execution */

	protected void    ensureSelf()
	{
		//~: create cash payment pair
		paySelfCash = (PaySelf) defaultPayCash();

		//~: create bank payment pair
		paySelfBank = (PaySelf) defaultPayBank();
	}

	protected void    ensureFirm()
	{
		//~: create cash payment pair
		payFirmCash = (PayFirm) defaultPayCash();

		//~: create bank payment pair
		payFirmBank = (PayFirm) defaultPayBank();
	}

	protected PayIt   defaultPayCash()
	{
		Account     a   = defaultAccount();
		List<PayIt> its = bean(GetAccount.class).getPayIts(a.getPrimaryKey());

		//~: search for the default cash way
		for(PayIt it : its)
		{
			PayWay w = HiberPoint.unproxy(it.getPayWay());

			//?: {default payment way for cash}
			if((w instanceof PayCash) && code.equals(w.getName()))
				return HiberPoint.unproxy(it);
		}

		//~: create new payment way
		PayWay w = createDefaultPayWayCash();

		//!: do save the way
		ActionsPoint.actionRun(ActionType.SAVE, w);


		//~: create new pay it
		PayIt it = (contractor == null)?(new PaySelf()):(new PayFirm());

		//~: account + way
		it.setAccount(a);
		it.setPayWay(w);


		//!: do save self payment joint
		ActionsPoint.actionRun(ActionType.SAVE, it);

		return it;
	}

	protected PayIt   defaultPayBank()
	{
		Account     a   = defaultAccount();
		List<PayIt> its = bean(GetAccount.class).getPayIts(a.getPrimaryKey());

		//~: search for the default cash way
		for(PayIt it : its)
		{
			PayWay w = HiberPoint.unproxy(it.getPayWay());

			//?: {default payment way for banks}
			if((w instanceof PayBank) && code.equals(w.getName()))
				return HiberPoint.unproxy(it);
		}

		//~: create new payment way
		PayWay w = createDefaultPayWayBank();

		//!: do save the way
		ActionsPoint.actionRun(ActionType.SAVE, w);


		//~: create new pay it
		PayIt it = (contractor == null)?(new PaySelf()):(new PayFirm());

		//~: account + way
		it.setAccount(a);
		it.setPayWay(w);


		//!: do save self payment joint
		ActionsPoint.actionRun(ActionType.SAVE, it);

		return it;
	}

	protected PaySelf defaultPaySelf(PayWay w)
	{
		Account a = defaultAccount();
		PayIt   x = bean(GetAccount.class).getPayIt(a, w);

		//?: {found it}
		if(x != null)
			return (PaySelf) HiberPoint.unproxy(x);

		//~: create it
		x = new PaySelf();

		//~: account + way
		x.setAccount(a);
		x.setPayWay(w);


		//!: do save
		ActionsPoint.actionRun(ActionType.SAVE, x);

		return (PaySelf)x;
	}

	protected Account defaultAccount()
	{
		//~: lookup the default account
		Account a = (contractor == null)
		  ?(bean(GetAccount.class).getOwnAccount(domain.getPrimaryKey(), code))
		  :(bean(GetAccount.class).getFirmAccount(contractor.getPrimaryKey(), code));

		//?: {found it}
		if(a != null) return a;

		//~: create it
		a = new Account();

		//~: domain
		a.setDomain(domain);

		//~: code
		a.setCode(code);

		//~: contractor
		a.setContractor(contractor);

		//~: name
		a.setName("Общий счёт по умолчанию");


		//~: remarks
		a.setRemarks(SU.cats(
		  (contractor == null)?
		    ("Собственный учётный счёт по умолчанию "):
		    ("Учётный счёт по умолчанию для контрагента. "),
		  "Общий для всех платёжных счетов. ",
		  "(Создан при импорте данных.)"
		));


		//!: do save
		ActionsPoint.actionRun(ActionType.SAVE, a);

		return a;
	}


	protected PayWay  createDefaultPayWayCash()
	{
		PayCash w = new PayCash();

		//~: domain
		w.setDomain(domain);

		//~: name
		w.setName(code);

		//~: remarks
		w.setRemarks(SU.cats(
		  "Платежный счёт для наличных операций. ",
		  "(Создан при импорте данных.)"
		));

		//~: open date
		w.setOpened(new Date(0L));

		return w;
	}

	protected PayWay  createDefaultPayWayBank()
	{
		PayBank w = new PayBank();

		//~: domain
		w.setDomain(domain);

		//~: name
		w.setName(code);

		//~: remarks
		w.setRemarks(SU.cats(
		  "Платежный счёт для безналичных (банковских) операций. ",
		  "(Создан при импорте данных.)"
		));

		//~: open date
		w.setOpened(new Date(0L));

		//~: bank id
		w.setBankId("000000000");

		//~: bank name
		w.setBankName("Укажите банковские реквизиты!");

		//~: bank account
		w.setBankAccount("00000000000000000000");

		//~: remittee account
		w.setRemitteeAccount("00000000000000000000");

		//~: remittee name
		if(contractor == null)
			w.setRemitteeName("Укажите название своей огранизации!");
		else
			w.setRemitteeName(contractor.getFirm().getFullName());

		return w;
	}


	/* protected: parameters */

	protected final Domain     domain;
	protected final Contractor contractor;
	protected final String     code;


	/* protected: results */

	protected PaySelf paySelfCash;
	protected PaySelf paySelfBank;
	protected PayFirm payFirmCash;
	protected PayFirm payFirmBank;
}