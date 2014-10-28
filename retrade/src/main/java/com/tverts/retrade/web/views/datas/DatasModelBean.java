package com.tverts.retrade.web.views.datas;

/* Java */

import java.io.IOException;
import java.io.OutputStream;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelData;

/* com.tverts: endure (reports) */

import com.tverts.endure.report.ReportsSelectModelBean;

/* com.tverts: objects */

import com.tverts.objects.BinarySource;


/**
 * A model to display Data Sources list
 * and select one of them.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model")
@XmlType(name = "datas-model")
public class      DatasModelBean
       extends    ReportsSelectModelBean
       implements BinarySource
{
	public static final long serialVersionUID = 0L;


	/* public: Data Sources Model Bean */

	public void setXMLData(byte[] data)
	{
		this.data = data;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new DatasModelData(this);
	}


	/* public: Binary Source */

	public void download(Biny biny)
	  throws IOException
	{
		OutputStream o = biny.stream(Biny.DEFLATE);

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


	/* private: the xml document saved */

	private byte[] data;
}