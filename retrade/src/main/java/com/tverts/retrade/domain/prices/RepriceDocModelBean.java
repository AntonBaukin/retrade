package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.RepriceDocModelData;


/**
 * Model bean for read-only view of
 * a {@link RepriceDoc} document.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "price-change-document-model")
public class RepriceDocModelBean extends UnityModelBean
{
	/* public: constructors */

	public RepriceDocModelBean()
	{}

	public RepriceDocModelBean(RepriceDoc rd)
	{
		this.setInstance(rd);
	}


	/* Price Change Document Model Bean (read) */

	public RepriceDoc repriceDoc()
	{
		return (RepriceDoc) accessEntity();
	}


	/* Model Bean (data access) */

	public ModelData  modelData()
	{
		return new RepriceDocModelData(this);
	}
}