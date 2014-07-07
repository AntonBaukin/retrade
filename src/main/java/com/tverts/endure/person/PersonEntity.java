package com.tverts.endure.person;

/* com.tverts: api */

import com.tverts.api.clients.Person;

/* com.tverts: endure (core) */

import com.tverts.endure.OxSearch;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.OxEntity;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * A physical person registered in the system.
 *
 * @author anton.baukin@gmail.com
 */
public class      PersonEntity
       extends    OxEntity
       implements DomainEntity, OxSearch
{
	/* Object Extraction */

	public Person getOx()
	{
		Person p = (Person) super.getOx();
		if(p == null) setOx(p = new Person());
		return p;
	}

	public void setOx(Person ox)
	{
		super.setOx(ox);
		this.oxSearch = null;
	}

	public void updateOx()
	{
		super.updateOx();
		this.oxSearch = null;
	}

	public String getOxSearch()
	{
		if(oxSearch == null)
		{
			Person p = getOx();

			oxSearch = SU.catx(
			  p.getLastName(), p.getFirstName(), p.getMiddleName(),
			  p.getEmail(), p.getPhoneMobile(), p.getPhoneWork()
			);
		}

		return oxSearch;
	}

	private String oxSearch;

	public void setOxSearch(String oxSearch)
	{
		this.oxSearch = oxSearch;
	}


	/* Person Entity */

	public Domain getDomain()
	{
		return domain;
	}

	private Domain domain;

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}
}