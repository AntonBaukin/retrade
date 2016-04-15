package com.tverts.retrade.domain.invoice;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.web.data.InvoiceModelData;


/**
 * Model of an Invoice.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "invoice-model")
public class InvoiceModelBean extends UnityModelBean
{
	/* public: InvoiceModelBean (support) interface */

	public Invoice   invoice()
	{
		return (Invoice)accessEntity();
	}


	/* public: InvoiceModelBean (configuration) interface */

	public boolean isPositiveVolumeOnly()
	{
		return positiveVolumeOnly;
	}

	public void setPositiveVolumeOnly(boolean positiveVolumeOnly)
	{
		this.positiveVolumeOnly = positiveVolumeOnly;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new InvoiceModelData(this);
	}


	/* private: encapsulated data */

	private boolean positiveVolumeOnly;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		o.writeBoolean(positiveVolumeOnly);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		positiveVolumeOnly = i.readBoolean();
	}
}