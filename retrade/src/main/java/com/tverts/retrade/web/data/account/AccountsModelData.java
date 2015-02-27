package com.tverts.retrade.web.data.account;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.AccountView;
import com.tverts.retrade.domain.account.AccountsModelBean;
import com.tverts.retrade.domain.account.GetAccount;


/**
 * Model data provider for the table of all the
 * Accounts of the Domain.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "accountsNumber", "accounts"})
public class AccountsModelData implements ModelData
{
	/* public: constructors */

	public AccountsModelData()
	{}

	public AccountsModelData(AccountsModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public AccountsModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int getAccountsNumber()
	{
		return bean(GetAccount.class).countAccounts(getModel());
	}

	@XmlElement(name = "account")
	@XmlElementWrapper(name = "accounts")
	@SuppressWarnings("unchecked")
	public List<AccountView> getAccounts()
	{
		List<Object[]>    sel = bean(GetAccount.class).selectAccounts(getModel());
		List<AccountView> res = new ArrayList<AccountView>(sel.size());

		for(Object[] rec : sel)
			res.add(new AccountView().
			  init(rec).
			  initIncome((BigDecimal) rec[2]).
			  initExpense((BigDecimal) rec[3])
			);

		return res;
	}


	/* private: model */

	private AccountsModelBean model;
}