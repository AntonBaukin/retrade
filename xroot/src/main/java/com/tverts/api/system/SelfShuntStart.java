package com.tverts.api.system;

/* standard Java classes */

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * System user may send Self-Shunt request
 * on the Domain logged-in with the shunt
 * groups specified.
 *
 * Note that in present implementation only
 * one request may be issued, or they will mess!
 *
 * Also note that updating shunting is not
 * allowed via this request.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlType(name = "self-shunt-start", propOrder = {
  "logKey", "groups"
})
public class SelfShuntStart implements Serializable
{
	public static final long serialVersionUID = 0L;


	/**
	 * This key is assigned by the server when
	 * processing teh request.
	 */
	@XmlElement(name = "log-key")
	public String getLogKey()
	{
		return logKey;
	}

	public void setLogKey(String logKey)
	{
		this.logKey = logKey;
	}

	@XmlElement(name = "groups")
	public Set<String> getGroups()
	{
		return groups;
	}

	public void setGroups(Set<String> groups)
	{
		if(groups == null)
			this.groups.clear();
		else
			this.groups = groups;
	}


	/* request attributes */

	private String      logKey;

	private Set<String> groups =
	  new HashSet<String>(3);
}