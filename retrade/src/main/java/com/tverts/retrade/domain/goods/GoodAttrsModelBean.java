package com.tverts.retrade.domain.goods;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;

/* com.tverts: support */

import com.tverts.model.ModelData;
import com.tverts.retrade.web.data.goods.GoodAttrsModelData;
import com.tverts.support.IO;


/**
 * Model bean that combines accessing both
 * the Attributes Types of the Goods of the Domain,
 * with the concrete values of attributes of
 * some Good Unit provided.
 *
 * @author anton.baukin@gmail.com
 */
public class GoodAttrsModelBean extends ModelBeanBase
{
	/* Good Attributes Model */

	public Long getGoodUnit()
	{
		return goodUnit;
	}

	private Long goodUnit;

	public void setGoodUnit(Long goodUnit)
	{
		this.goodUnit = goodUnit;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new GoodAttrsModelData(this);
	}


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.longer(o, goodUnit);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		goodUnit = IO.longer(i);
	}
}