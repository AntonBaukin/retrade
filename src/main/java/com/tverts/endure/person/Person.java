package com.tverts.endure.person;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.Entity;


/**
 * A physical person registered in the system.
 *
 * @author anton.baukin@gmail.com
 */
public class Person extends Entity implements DomainEntity
{
	/* public: Person (bean) properties */

	public Domain getDomain()
	{
		return domain;
	}

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

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

	public Character getGender()
	{
		return gender;
	}

	public void setGender(Character gender)
	{
		if(gender != null)
		{
			gender = Character.toTitleCase(gender);
			if((gender != 'M') && (gender != 'F'))
				throw new IllegalArgumentException();
		}

		this.gender = gender;
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


	/* persisted attributes */

	private Domain    domain;
	private String    lastName;
	private String    firstName;
	private String    middleName;
	private Character gender;
	private String    email;
	private String    phoneMob;
	private String    phoneWork;
}