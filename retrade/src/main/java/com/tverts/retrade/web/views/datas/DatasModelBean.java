package com.tverts.retrade.web.views.datas;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: objects */

import com.tverts.objects.BinarySource;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * A model to display Data Sources list
 * and select one of them.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model")
@XmlType(name = "datas-model")
public class      DatasModelBean
       extends    DataSelectModelBean
       implements BinarySource
{
	/* Data Sources Model */

	public void setXMLData(byte[] data)
	{
		this.data = data;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new DatasModelData(this);
	}


	/* public: Binary Source */

	public void download(Biny biny)
	  throws IOException
	{
		OutputStream o = biny.stream(Biny.DEFLATE);

		//~: content type for XML data file
		biny.set("Content-Type", "application/xml;charset=UTF-8");

		if(data != null) try
		{
			o.write(data);
		}
		finally
		{
			//!: erase the data to save the memory
			data = null;
		}
	}


	/* private: encapsulated data */

	private byte[] data; //<-- the xml document saved


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		IO.obj(o, data);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		data = IO.obj(i, byte[].class);
	}
}