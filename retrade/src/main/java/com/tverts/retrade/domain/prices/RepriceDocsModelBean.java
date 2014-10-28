package com.tverts.retrade.domain.prices;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.RepriceDocsModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model bean for table with views on all
 * price change documents of the Domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "price-change-documents-model")
public class RepriceDocsModelBean extends DataSelectModelBean
{
	/* Price Change Documents Model */

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

	public boolean isFixedOnly()
	{
		return fixedOnly;
	}

	public void setFixedOnly(boolean fixedOnly)
	{
		this.fixedOnly = fixedOnly;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new RepriceDocsModelData(this);
	}


	/* private: encapsulated data */

	private Date    minDate;
	private Date    maxDate;
	private boolean fixedOnly = true;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.obj(o, minDate);
		IO.obj(o, maxDate);
		o.writeBoolean(fixedOnly);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		minDate   = IO.obj(i, Date.class);
		maxDate   = IO.obj(i, Date.class);
		fixedOnly = i.readBoolean();
	}
}