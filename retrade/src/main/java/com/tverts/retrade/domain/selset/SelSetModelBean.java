package com.tverts.retrade.domain.selset;

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

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model Bean of current Selection Set.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "selection-set-model")
public class SelSetModelBean extends DataSelectModelBean
{
	/* Selection Set Model  */

	public Long getLogin()
	{
		return login;
	}

	public void setLogin(Long login)
	{
		this.login = login;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new SelSetModelData(this);
	}


	/* private: encapsulated data */

	private Long login;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		IO.longer(o, login);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		login = IO.longer(i);
	}
}