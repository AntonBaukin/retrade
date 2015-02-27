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
	/* Goods Model */

	/**
	 * Specifies a contractor to display the goods for.
	 * If not defined, displays all the goods of the Domain.
	 */
	public Long getContractor()
	{
		return contractor;
	}

	public void setContractor(Long contractor)
	{
		this.contractor = contractor;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		ModelData md = super.modelData();
		return (md != null)?(md):(new GoodsModelData(this));
	}


	/* private: encapsulated data */

	private Long contractor;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		IO.longer(o, contractor);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		contractor = IO.longer(i);
	}
}