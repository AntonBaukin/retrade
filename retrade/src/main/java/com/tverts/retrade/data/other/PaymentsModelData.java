package com.tverts.retrade.data.other;

/* standard Java classes */

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

/* com.tverts: retrade domain (payments) */

import com.tverts.retrade.domain.payment.GetPayment;
import com.tverts.retrade.domain.payment.Payment;
import com.tverts.retrade.domain.payment.PaymentView;
import com.tverts.retrade.domain.payment.PaymentsModelBean;


/**
 * Data provider for view Payments by the variety of projections.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "paymentsNumber", "payments"})
public class PaymentsModelData implements ModelData
{
	/* public: constructors */

	public PaymentsModelData()
	{}

	public PaymentsModelData(PaymentsModelBean model)
	{
		this.model = model;
	}


	/* public: data bean interface */

	@XmlElement
	public PaymentsModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int getPaymentsNumber()
	{
		return bean(GetPayment.class).countPaymentsDisp(getModel());
	}

	@XmlElement(name = "payment")
	@XmlElementWrapper(name = "payments")
	@SuppressWarnings("unchecked")
	public List<PaymentView> getPayments()
	{
		List<Object[]>    sel = bean(GetPayment.class).selectPaymentsDisp(getModel());
		List<PaymentView> res = new ArrayList<PaymentView>(sel.size());

		for(Object[] rec : sel)
			res.add(new PaymentView().init(
			  model.getModelFlags(), (Payment) rec[0]
			));

		return res;
	}


	/* private: model */

	private PaymentsModelBean model;
}