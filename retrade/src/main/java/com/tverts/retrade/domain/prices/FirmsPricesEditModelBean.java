package com.tverts.retrade.domain.prices;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (data) */

import com.tverts.retrade.web.data.prices.FirmsPricesEditModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * A model bean to edit the Price Lists
 * associated with one or more selected firms.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "firms-prices-edit-model")
public class FirmsPricesEditModelBean extends DataSelectModelBean
{
	/* Firms Prices Edit Model */

	@XmlElement(name = "contractor")
	@XmlElementWrapper(name = "contractors-edit")
	public List<Long> getContractors()
	{
		return (contractors != null)?(contractors):
		  (contractors = new ArrayList<>(2));
	}

	public void setContractors(List<Long> contractors)
	{
		this.contractors = contractors;
	}

	@XmlElement(name = "price-list")
	@XmlElementWrapper(name = "price-lists-edit")
	public List<Long> getPriceLists()
	{
		return (priceLists != null)?(priceLists):
		  (priceLists = new ArrayList<>(4));
	}

	public void setPriceLists(List<Long> priceLists)
	{
		this.priceLists = priceLists;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new FirmsPricesEditModelData(this);
	}


	/* private: encapsulated data */

	private List<Long> contractors;
	private List<Long> priceLists;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.obj(o, contractors);
		IO.obj(o, priceLists);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		contractors = IO.obj(i, List.class);
		priceLists  = IO.obj(i, List.class);
	}
}