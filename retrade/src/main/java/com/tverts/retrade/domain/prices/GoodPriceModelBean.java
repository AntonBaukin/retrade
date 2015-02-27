package com.tverts.retrade.domain.prices;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.NumericSelectModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.web.data.GoodPriceModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model bean to display {@link GoodPrice}.
 *
 * Data selection model here is for displaying
 * history of the good prices of this position.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "good-price-model")
public class  GoodPriceModelBean extends NumericSelectModelBean
{
	/* public: constructors */

	public GoodPriceModelBean()
	{}

	public GoodPriceModelBean(GoodPrice gp)
	{
		this.setInstance(gp);
	}


	/* Good Price Model Bean (read) */

	public GoodPrice  goodPrice()
	{
		return (GoodPrice)accessNumeric();
	}

	@XmlElement
	public Long       getObjectKey()
	{
		return (goodPrice() == null)?(null):
		  (goodPrice().getPrimaryKey());
	}


	/* Good Price Model (bean) */

	public Date getMinDate()
	{
		return minDate;
	}

	public void setMinDate(Date minDate)
	{
		this.minDate = minDate;
	}

	public Date getMaxDate()
	{
		return maxDate;
	}

	public void setMaxDate(Date maxDate)
	{
		this.maxDate = maxDate;
	}


	/* Model Bean (data access) */

	public ModelData  modelData()
	{
		return new GoodPriceModelData(this);
	}


	/* private: the object's class and key */

	private Date minDate;
	private Date maxDate;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.obj(o, minDate);
		IO.obj(o, maxDate);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		minDate = IO.obj(i, Date.class);
		maxDate = IO.obj(i, Date.class);
	}
}