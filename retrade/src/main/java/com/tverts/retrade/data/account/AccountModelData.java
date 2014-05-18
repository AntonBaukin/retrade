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

import com.tverts.retrade.domain.account.AccountModelBean;
import com.tverts.retrade.domain.account.GetAccount;
import com.tverts.retrade.domain.account.PayWayView;


/**
 * Model data provider for an Account view.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "payWays"})
public class AccountModelData implements ModelData
{
	/* public: constructors */

	public AccountModelData()
	{}

	public AccountModelData(AccountModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public AccountModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "pay-way")
	@XmlElementWrapper(name = "pay-ways")
	@SuppressWarnings("unchecked")
	public List<PayWayView> getPayWays()
	{
		List<Object[]>   sel = bean(GetAccount.class).
		  selectAccountPayWays(getModel().getPrimaryKey());
		List<PayWayView> res = new ArrayList<PayWayView>(sel.size());

		for(Object[] rec : sel)
			res.add(new PayWayView().
			  init(rec).
			  initIncome((BigDecimal) rec[1]).
			  initExpense((BigDecimal) rec[2])
			);

		return res;
	}


	/* private: model */

	private AccountModelBean model;
}