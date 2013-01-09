package com.tverts.api.term;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Operator of a sell terminal.
 */
@XmlType(name = "operator")
public class Operator
{
	/**
	 * Primary key from the source database.
	 */
	@XmlAttribute(name = "key", required = true)
	public long getKey()
	{
		return key;
	}

	public void setKey(long key)
	{
		this.key = key;
	}

	@XmlAttribute(name = "removed")
	public Boolean getRemoved()
	{
		return Boolean.TRUE.equals(removed)?(Boolean.TRUE):(null);
	}

	public void setRemoved(Boolean removed)
	{
		this.removed = removed;
	}

	@XmlElement(name = "login")
	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	@XmlElement(name = "passhash")
	public String getPasshash()
	{
		return passhash;
	}

	public void setPasshash(String passhash)
	{
		this.passhash = passhash;
	}

	@XmlElement(name = "first-name")
	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	@XmlElement(name = "middle-name")
	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	@XmlElement(name = "last-name")
	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	@XmlElement(name = "phone")
	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}


	/* Object interface */

	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(o == null || getClass() != o.getClass())
			return false;

		Operator operator = (Operator)o;
		return (key == operator.key);
	}

	public int hashCode()
	{
		return (int)(key ^ (key >>> 32));
	}


	/* attributes */

	private long    key;
	private Boolean removed;
	private String  login;
	private String  passhash;
	private String  firstName;
	private String  middleName;
	private String  lastName;
	private String  phone;
}