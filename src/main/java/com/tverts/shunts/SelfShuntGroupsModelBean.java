package com.tverts.shunts;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Set;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;

/* com.tverts: objects */

import com.tverts.objects.ObjectParamView;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;


/**
 * Model bean to Self-Shunt Domain instance.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "self-shunt-groups-model", propOrder = {
  "domain", "updating", "logParam", "groups", "params"
})
public class SelfShuntGroupsModelBean extends ModelBeanBase
{
	/* Self Shunt Groups Model Bean */

	@XmlElement
	public Long getDomain()
	{
		return super.getDomain();
	}

	@XmlElement
	public boolean isUpdating()
	{
		return updating;
	}

	public void setUpdating(boolean updating)
	{
		this.updating = updating;
	}

	@XmlElement(name = "log-param")
	public String getLogParam()
	{
		return logParam;
	}

	public void setLogParam(String logParam)
	{
		this.logParam = logParam;
	}

	/**
	 * Returns the groups selected for shunting.
	 */
	@XmlElement
	public Set<String> getGroups()
	{
		return groups;
	}

	public void setGroups(Set<String> groups)
	{
		this.groups = EX.assertn(groups);
	}

	@XmlElement(name = "object-param")
	@XmlElementWrapper(name = "object-params")
	public ObjectParamView[] getParams()
	{
		return params;
	}

	public void setParams(ObjectParamView[] params)
	{
		this.params = EX.assertn(params);
	}


	/* private: encapsulated data */

	private boolean           updating;
	private String            logParam;
	private Set<String>       groups = new HashSet<String>();
	private ObjectParamView[] params = new ObjectParamView[0];


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		o.writeBoolean(updating);
		IO.str(o, logParam);
		IO.obj(o, groups);
		IO.obj(o, params);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		updating = i.readBoolean();
		logParam = IO.str(i);
		groups   = IO.obj(i, Set.class);
		params   = IO.obj(i, ObjectParamView[].class);
	}
}