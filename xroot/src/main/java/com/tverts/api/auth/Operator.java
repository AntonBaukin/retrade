package com.tverts.api.auth;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.clients.Person;


/**
 * Operator is a person working in the firm
 * (the domain) and having roles and the
 * related security attributes.
 */
@XmlRootElement(name = "operator")
@XmlType(name = "operator")
public class Operator extends Person
{
	@XmlAttribute(name = "removed")
	public Boolean getRemoved()
	{
		return (removed)?(Boolean.TRUE):(null);
	}

	private boolean removed;

	public void setRemoved(Boolean removed)
	{
		this.removed = Boolean.TRUE.equals(removed);
	}

	@XmlElement(name = "login")
	public String getLogin()
	{
		return login;
	}

	private String login;

	public void setLogin(String login)
	{
		this.login = login;
	}
}