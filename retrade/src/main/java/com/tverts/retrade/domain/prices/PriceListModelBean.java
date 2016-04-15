package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.NumericSelectModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.web.data.prices.PriceListModelData;

/* com.tverts: support */


/**
 * Model bean to display {@link PriceListEntity}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "price-list-model")
public class PriceListModelBean extends NumericSelectModelBean
{
	/* public: constructors */

	public PriceListModelBean()
	{}

	public PriceListModelBean(PriceListEntity pl)
	{
		this.setInstance(pl);
	}


	/* Data Select Model */

	public Object getRestriction(Object flag)
	{
		return flag.equals(restriction)?(Boolean.TRUE):(null);
	}


	/* Price List Model */

	public PriceListEntity priceList()
	{
		return (PriceListEntity)accessNumeric();
	}

	@XmlElement
	public Long getObjectKey()
	{
		return (priceList() == null)?(null):
		  (priceList().getPrimaryKey());
	}

	public String getRestriction()
	{
		return restriction;
	}

	private String restriction;

	public void setRestriction(String restriction)
	{
		this.restriction = restriction;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new PriceListModelData(this);
	}
}