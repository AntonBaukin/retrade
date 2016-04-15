package com.tverts.retrade.secure;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (data) */

import com.tverts.retrade.web.data.settings.SecSetsModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model of viewing Secure Sets.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "secure-sets-model")
public class SecSetsModelBean extends DataSelectModelBean
{
	/* Secure Rules Model Bean (bean) */

	public String getAblesModel()
	{
		return ablesModel;
	}

	public void setAblesModel(String ablesModel)
	{
		this.ablesModel = ablesModel;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new SecSetsModelData(this);
	}


	/* private: encapsulated data */

	private String ablesModel;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		IO.str(o, ablesModel);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		ablesModel = IO.str(i);
	}
}