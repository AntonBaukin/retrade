package com.tverts.shunts;

/* standard Java classes */

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


/**
 * Model bean to Self-Shunt Domain instance.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(propOrder = {
  "domain", "updating", "logParam", "groups", "params"
})
public class SelfShuntGroupsModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: SelfShuntGroupsModelBean (bean) interface */

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
		if(groups == null) throw new IllegalArgumentException();
		this.groups = groups;
	}

	@XmlElement(name = "object-param")
	@XmlElementWrapper(name = "object-params")
	public ObjectParamView[] getParams()
	{
		return params;
	}

	public void setParams(ObjectParamView[] params)
	{
		if(params == null) throw new IllegalArgumentException();
		this.params = params;
	}


	/* state of the model */

	private boolean           updating;
	private String            logParam;
	private Set<String>       groups = new HashSet<String>();
	private ObjectParamView[] params = new ObjectParamView[0];
}