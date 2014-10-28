package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.DataSelectModel;
import com.tverts.model.DataSelectDelegate;
import com.tverts.model.DataSortModel;
import com.tverts.model.ModelData;
import com.tverts.model.NumericSelectModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.prices.PriceListModelData;

/* com.tverts: support */

import com.tverts.support.SU;


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


	/* Price List Model (read) */

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


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new PriceListModelData(this);
	}
}