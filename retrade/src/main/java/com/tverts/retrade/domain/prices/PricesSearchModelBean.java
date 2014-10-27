package com.tverts.retrade.domain.prices;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.prices.PricesSearchModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model bean to search for the Goods,
 * Price Lists, and the Prices.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "prices-search")
public class PricesSearchModelBean extends DataSelectModelBean
{
	/* Prices Search Model */

	public Long getPriceList()
	{
		return priceList;
	}

	public void setPriceList(Long priceList)
	{
		this.priceList = priceList;
	}

	public Long getGoodUnit()
	{
		return goodUnit;
	}

	public void setGoodUnit(Long goodUnit)
	{
		this.goodUnit = goodUnit;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new PricesSearchModelData(this);
	}


	/* private: encapsulated data */

	private Long priceList;
	private Long goodUnit;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.longer(o, priceList);
		IO.longer(o, goodUnit);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		priceList = IO.longer(i);
		goodUnit  = IO.longer(i);
	}
}