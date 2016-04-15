package com.tverts.api.system;

/* standard Java classes */

import java.io.Serializable;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * System user gets the results of the
 * last self-shunting of the system.
 *
 *
 * @author anton.baukin@gmail.com.
 */
@XmlType(name = "self-shunt-results", propOrder = {
  "logKey", "results"
})
public class SelfShuntResults implements Serializable
{
	public static final long serialVersionUID = 0L;


	/**
	 * Set this key from {@link SelfShuntStart#getLogKey()}
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

	/**
	 * Text with the self-shunting log. Undefined
	 * value means the shunting is not finished yet.
	 */
	@XmlElement(name = "results")
	public String getResults()
	{
		return results;
	}

	public void setResults(String results)
	{
		this.results = results;
	}


	/* request attributes */

	private String logKey;
	private String results;
}