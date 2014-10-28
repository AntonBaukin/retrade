package com.tverts.retrade.domain.account;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade models data */

import com.tverts.retrade.data.account.PayWayModelData;


/**
 * Model bean with main info on a {@link PayWay}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "payment-way-model")
public class PayWayModelBean extends UnityModelBean
{
	/* Pay Way Model */

	public PayWay accessEntity()
	{
		return (PayWay) super.accessEntity();
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new PayWayModelData(this);
	}
}