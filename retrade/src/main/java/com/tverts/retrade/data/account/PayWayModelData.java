package com.tverts.retrade.data.account;

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
import com.tverts.retrade.domain.account.GetAccount;
import com.tverts.retrade.domain.account.PayWayModelBean;


/**
 * Model data provider for a Payment Way view;
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "accounts"})
public class PayWayModelData implements ModelData
{
	/* public: constructors */

	public PayWayModelData()
	{}

	public PayWayModelData(PayWayModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public PayWayModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "account")
	@XmlElementWrapper(name = "accounts")
	@SuppressWarnings("unchecked")
	public List<AccountView> getAccounts()
	{
		List<Object[]>    sel = bean(GetAccount.class).
		  selectPayWayAccounts(getModel().getPrimaryKey());
		List<AccountView> res = new ArrayList<AccountView>(sel.size());

		for(Object[] rec : sel)
			res.add(new AccountView().
			  init(rec).
			  initIncome((BigDecimal) rec[1]).
			  initExpense((BigDecimal) rec[2])
			);

		return res;
	}


	/* private: model */

	private PayWayModelBean model;
}