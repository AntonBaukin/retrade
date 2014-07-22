package com.tverts.endure.auth;

/* com.tverts: api */

import com.tverts.api.clients.Computer;

/* com.tverts: endure (core) */

import com.tverts.endure.OxSearch;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.OxCatEntity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * As the application is not for humans only,
 * external computer systems cooperating do
 * login as a Computers.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      ComputerEntity
       extends    OxCatEntity
       implements OxSearch
{
	/* Object Extraction */

	public Computer getOx()
	{
		Computer c = (Computer) super.getOx();
		if(c == null) setOx(c = new Computer());
		return c;
	}

	public void     setOx(Object ox)
	{
		EX.assertx(ox instanceof Computer);
		super.setOx(ox);
	}

	public String   getOxSearch()
	{
		Computer c = getOx();

		return SU.catx(
		  c.getCode(), c.getName(), c.getComment()
		);
	}
}
