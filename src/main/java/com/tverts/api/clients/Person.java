package com.tverts.api.clients;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: api */

import com.tverts.api.core.TwoKeysObject;
import com.tverts.api.core.XKeyPair;
import com.tverts.api.support.CharAdapter;


/**
 * Data about a Person registered in the system.
 */
@XmlRootElement(name = "person")
@XmlType(name = "person")
public class Person implements TwoKeysObject
{
	@XmlAttribute(name = "pkey")
	public Long getPkey()
	{
		return pkey;
	}

	private Long pkey;

	public void setPkey(Long pkey)
	{
		this.pkey = pkey;
	}

	@XmlElement(name = "xkey")
	public String getXkey()
	{
		return xkey;
	}

	private String xkey;

	public void setXkey(String xkey)
	{
		this.xkey = xkey;
	}

	@XmlElement(name = "last-name")
	public String getLastName()
	{
		return lastName;
	}

	private String lastName;

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	@XmlElement(name = "first-name")
	public String getFirstName()
	{
		return firstName;
	}

	private String firstName;

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	@XmlElement(name = "middle-name")
	public String getMiddleName()
	{
		return middleName;
	}

	private String middleName;

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	@XmlElement(name = "initials")
	public String getInitials()
	{
		return initials;
	}

	private String initials;

	public void setInitials(String initials)
	{
		this.initials = initials;
	}

	@XmlElement(name = "gender")
	@XmlJavaTypeAdapter(CharAdapter.class)
	public Character getGender()
	{
		return gender;
	}

	private Character gender;

	public void setGender(Character gender)
	{
		this.gender = gender;
	}

	@XmlElement(name = "email")
	public String getEmail()
	{
		return email;
	}

	private String email;

	public void setEmail(String email)
	{
		this.email = email;
	}

	@XmlElement(name = "phone-mobile")
	public String getPhoneMobile()
	{
		return phoneMobile;
	}

	private String phoneMobile;

	public void setPhoneMobile(String phoneMobile)
	{
		this.phoneMobile = phoneMobile;
	}

	@XmlElement(name = "phone-work")
	public String getPhoneWork()
	{
		return phoneWork;
	}

	private String phoneWork;

	public void setPhoneWork(String phoneWork)
	{
		this.phoneWork = phoneWork;
	}

	@XmlElement(name = "phone-work")
	//@XKeyPair(type = Firm.class)
	public Long getFirmKey()
	{
		return firmKey;
	}

	private Long firmKey;

	public void setFirmKey(Long firmKey)
	{
		this.firmKey = firmKey;
	}

	public String getFirmXKey()
	{
		return firmXKey;
	}

	private String firmXKey;

	public void setFirmXKey(String firmXKey)
	{
		this.firmXKey = firmXKey;
	}
}