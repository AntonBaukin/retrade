package com.tverts.retrade.domain.prices;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* com.tverts: model */

import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelData;

/* com.tverts: retrade model data */

import com.tverts.retrade.data.PriceChangeEditModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model bean to store {@link PriceChangeEdit} instance.
 *
 * @author anton.baukin@gmail.com
 */
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
		return readModelBean(getRepriceDocModelKey(),
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
		o.writeObject(searchGoods);
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