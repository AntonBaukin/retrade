package com.tverts.retrade.domain.firm;

/* com.tverts: endure (core + catalogues + persons) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;
import com.tverts.endure.cats.CatItem;
import com.tverts.endure.person.Firm;


/**
 * Contractor of the domain is a legal entity
 * of some kind that has deals with the client
 * (firm) owning the domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class Contractor extends Entity implements CatItem
{
	/* public: Contractor bean interface */

	public Domain  getDomain()
	{
		return domain;
	}

	public void    setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String  getCode()
	{
		return code;
	}

	public void    setCode(String code)
	{
		this.code = code;
	}

	public String  getName()
	{
		return name;
	}

	public void    setName(String name)
	{
		//?: {the name is changing}
		if((this.name != null) && !this.name.equals(name))
			this.nameProc = null;

		this.name = name;
	}

	public String  getNameProc()
	{
		if(nameProc != null)
			return nameProc;

		if(getName() == null)
			return null;

		String s = getName().toLowerCase();
		s = s.replaceAll("[^0-9a-zа-я]", " ");
		s = s.replaceAll("\\s+", " ");

		return nameProc = s;
	}

	public void    setNameProc(String nameProc)
	{
		this.nameProc = nameProc;
	}

	public Firm    getFirm()
	{
		return firm;
	}

	public void    setFirm(Firm firm)
	{
		this.firm = firm;
	}


	/* private: persisted attributes */

	private Domain domain;
	private String code;
	private String name;
	private String nameProc;
	private Firm   firm;
}