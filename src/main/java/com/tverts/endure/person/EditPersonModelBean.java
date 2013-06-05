package com.tverts.endure.person;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/* com.tverts: model */

import com.tverts.model.UnityModelBean;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Edit state of a {@link Person}.
 *
 * Note that entity referred by this model
 * may be a Person, or an Auth Login.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "person")
public class EditPersonModelBean extends UnityModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: EditPersonModelBean (bean) interface */

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	@XmlElement(name = "genderMale")
	public Boolean getMale()
	{
		return male;
	}

	public void setMale(Boolean male)
	{
		this.male = male;
	}

	@XmlTransient
	public String getMaleStr()
	{
		return (male == null)?(""):(male.toString());
	}

	public void setMaleStr(String male)
	{
		this.male = ((male = s2s(male)) == null)?(null):Boolean.valueOf(male);
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhoneMob()
	{
		return phoneMob;
	}

	public void setPhoneMob(String phoneMob)
	{
		this.phoneMob = phoneMob;
	}

	public String getPhoneWork()
	{
		return phoneWork;
	}

	public void setPhoneWork(String phoneWork)
	{
		this.phoneWork = phoneWork;
	}


	/* public: initialization */

	public EditPersonModelBean init(Person p)
	{
		lastName = p.getLastName();
		firstName = p.getFirstName();
		middleName = p.getMiddleName();
		male = (p.getGender() == null)?(null):(p.getGender().equals('M'));
		email = p.getEmail();
		phoneMob = p.getPhoneMob();
		phoneWork = p.getPhoneWork();

		return this;
	}

	public EditPersonModelBean copy(Person p)
	{
		p.setLastName(lastName);
		p.setFirstName(firstName);
		p.setMiddleName(middleName);
		p.setGender((male == null)?(null):Boolean.TRUE.equals(male)?('M'):('F'));
		p.setEmail(email);
		p.setPhoneMob(phoneMob);
		p.setPhoneWork(phoneWork);

		return this;
	}


	/* edit state */

	private String  lastName;
	private String  firstName;
	private String  middleName;
	private Boolean male;
	private String  email;
	private String  phoneMob;
	private String  phoneWork;
}