package com.tverts.retrade.domain.store;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.UnitySelectModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.TradeStoreModelData;


/**
 * Model bean to display {@link TradeStore}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "trade-store-model")
public class TradeStoreModelBean extends UnitySelectModelBean
{
	/* public: constructors */

	public TradeStoreModelBean()
	{}

	public TradeStoreModelBean(TradeStore ts)
	{
		this.setInstance(ts);
	}


	/* Trade Store Model (read) */

	public TradeStore store()
	{
		return (TradeStore)accessEntity();
	}

	@XmlElement
	public Long getObjectKey()
	{
		return (store() == null)?(null):(store().getPrimaryKey());
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new TradeStoreModelData(this);
	}
}