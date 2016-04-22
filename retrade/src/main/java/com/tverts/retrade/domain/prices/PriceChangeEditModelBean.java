package com.tverts.retrade.domain.prices;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelData;
import com.tverts.model.ModelsAccessPoint;

/* com.tverts: retrade model data */

import com.tverts.retrade.web.data.PriceChangeEditModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model bean to store {@link PriceChangeEdit} instance.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "price-change-edit-model")
public class PriceChangeEditModelBean extends ModelBeanBase
{
	/* Price Change Edit Model Bean */

	public PriceChangeEdit getPriceChange()
	{
		return priceChange;
	}

	public void setPriceChange(PriceChangeEdit priceChange)
	{
		this.priceChange = priceChange;
	}

	public String getRepriceDocModelKey()
	{
		return repriceDocModelKey;
	}

	public void setRepriceDocModelKey(String rdmKey)
	{
		this.repriceDocModelKey = rdmKey;
	}

	public String[] getSearchGoods()
	{
		return searchGoods;
	}

	public void setSearchGoods(String[] searchGoods)
	{
		this.searchGoods = searchGoods;
	}


	/* Price Change Edit Model Bean (support) */

	public RepriceDocEditModelBean getRepriceDocModel()
	{
		return ModelsAccessPoint.read(getRepriceDocModelKey(),
		  RepriceDocEditModelBean.class);
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new PriceChangeEditModelData(this);
	}


	/* private: encapsulated data */

	private PriceChangeEdit priceChange;
	private String          repriceDocModelKey;
	private String[]        searchGoods;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.xml(o, priceChange);
		IO.str(o, repriceDocModelKey);
		IO.obj(o, searchGoods);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		priceChange = IO.xml(i, PriceChangeEdit.class);
		repriceDocModelKey = IO.str(i);
		searchGoods = IO.obj(i, String[].class);
	}
}