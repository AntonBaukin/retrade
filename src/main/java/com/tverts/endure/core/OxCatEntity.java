package com.tverts.endure.core;

/* com.tverts: api */

import com.tverts.api.core.CatItem;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Ox-Entity that is also a catalogue item.
 * The Ox-Object must be a {@link CatItem}.
 *
 * @author anton.baukin@gmail.com.
 */
public abstract class OxCatEntity
       extends        OxEntity
       implements     com.tverts.endure.cats.CatItem
{
	/* Catalogue Item */

	public Domain  getDomain()
	{
		return domain;
	}

	private Domain domain;

	public void    setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String  getCode()
	{
		return code;
	}

	private String code;

	public void    setCode(String code)
	{
		this.code = code;
	}

	public String  getName()
	{
		return name;
	}

	private String name;

	public void    setName(String name)
	{
		this.name = name;
	}


	/* Object Extraction */

	public CatItem getOx()
	{
		return (CatItem) super.getOx();
	}

	public void    setOx(Object ox)
	{
		EX.assertx(ox instanceof CatItem);
		super.setOx(ox);
	}

	public void    updateOx()
	{
		super.updateOx();

		//~: assign code+name from the ox-item
		CatItem i; if((i = this.getOx()) != null)
		{
			this.code = i.getCode();
			this.name = i.getName();
		}
	}

	public String  getOxSearch()
	{
		CatItem i = getOx();
		return SU.catx(i.getCode(), i.getName());
	}
}