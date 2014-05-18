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

import com.tverts.retrade.domain.account.GetAccount;
import com.tverts.retrade.domain.account.PayWayView;
import com.tverts.retrade.domain.account.PayWaysModelBean;


/**
 * Model data provider for the table of all the
 * Payment Ways of the Domain.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "payWaysNumber", "payWays"})
public class PayWaysModelData implements ModelData
{
	/* public: constructors */

	public PayWaysModelData()
	{}

	public PayWaysModelData(PayWaysModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public PayWaysModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int getPayWaysNumber()
	{
		return bean(GetAccount.class).countPayWays(getModel());
	}

	@XmlElement(name = "pay-way")
	@XmlElementWrapper(name = "pay-ways")
	@SuppressWarnings("unchecked")
	public List<PayWayView> getPayWays()
	{
		List<Object[]>   sel = bean(GetAccount.class).selectPayWays(getModel());
		List<PayWayView> res = new ArrayList<PayWayView>(sel.size());

		for(Object[] rec : sel)
			res.add(new PayWayView().
			  init(rec).
			  initIncome((BigDecimal) rec[2]).
			  initExpense((BigDecimal) rec[3])
			);

		return res;
	}


	/* private: model */

	private PayWaysModelBean model;
}