package com.tverts.endure.person;

/* com.tverts: api */

import com.tverts.api.clients.Firm;

/* com.tverts: endure (core) */

import com.tverts.endure.OxSearch;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.OxCatEntity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Entity that represents an organization.
 *
 * @author anton.baukin@gmail.com
 */
public class      FirmEntity
       extends    OxCatEntity
       implements DomainEntity, OxSearch
{
	/* Object Extraction */

	public Firm   getOx()
	{
		Firm f = (Firm) super.getOx();
		if(f == null) setOx(f = new Firm());
		return f;
	}

	public void   setOx(Object ox)
	{
		EX.assertx(ox instanceof Firm);
		super.setOx(ox);
	}

	public String getOxSearch()
	{
		Firm f = getOx();

		return SU.catx(
		  f.getCode(), f.getName(), f.getFullName(),
		  f.getTaxCode(), f.getTaxNumber(), f.getPhones(),
		  Addresses.a2s(f.getContactAddress()),
		  Addresses.a2s(f.getRegistryAddress())
		);
	}
}