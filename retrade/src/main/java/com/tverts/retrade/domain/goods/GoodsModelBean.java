package com.tverts.retrade.domain.goods;

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

import com.tverts.retrade.web.data.goods.GoodsModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model bean for table with views on all the goods.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "goods-model")
public class GoodsModelBean extends DataSelectModelBean
{
	/* Data Select Model */

	public Object getRestriction(Object flag)
	{
		return flag.equals(restriction)?(Boolean.TRUE):(null);
	}


	/* Goods Model */

	/**
	 * Specifies a contractor to display the goods for.
	 * If not defined, displays all the goods of the Domain.
	 */
	public Long getContractor()
	{
		return contractor;
	}

	private Long contractor;

	public void setContractor(Long contractor)
	{
		this.contractor = contractor;
	}

	/**
	 * Tells to select the aggregated values from the database.
	 */
	public boolean isAggrValues()
	{
		return aggrValues;
	}

	private boolean aggrValues;

	public void setAggrValues(boolean aggrValues)
	{
		this.aggrValues = aggrValues;
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
		ModelData md = super.modelData();
		return (md != null)?(md):(new GoodsModelData(this));
	}


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.longer(o, contractor);
		o.writeBoolean(aggrValues);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		contractor = IO.longer(i);
		aggrValues = i.readBoolean();
	}
}