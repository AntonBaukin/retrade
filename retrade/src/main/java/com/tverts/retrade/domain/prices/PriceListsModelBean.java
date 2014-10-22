package com.tverts.retrade.domain.prices;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelData;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.prices.PriceListsModelData;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * Model bean for table with views on all
 * price lists of the domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "price-lists-model")
public class PriceListsModelBean extends ModelBeanBase
{
	/* Price Lists Model */

	public CatItemView getView()
	{
		return view;
	}

	private CatItemView view;

	public void setView(CatItemView view)
	{
		this.view = view;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new PriceListsModelData(this);
	}


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		OU.obj2xml(o, view);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		view = OU.xml2obj(i, CatItemView.class);
	}
}