package com.tverts.endure.secure;

/* com.tverts: endure (core) */

import com.tverts.endure.UnitedBase;
import com.tverts.endure.Unity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;

/* com.tverts: secure (force) */

import com.tverts.secure.force.SecForce;


/**
 * Instance of Security Rule. Stored in
 * the database. Logins may be associated
 * with Rules via {@link SecAble} links.
 *
 * Rules may have (join) subclasses.
 * Rules always have Unified Mirror.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SecRule
       extends    UnitedBase
       implements DomainEntity
{
	/* public: SecRule (bean) interface */

	public Domain getDomain()
	{
		return domain;
	}

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	/**
	 * Object related to this rule. Must be
	 * defined. Top-level Rules refer their Domain.
	 */
	public Unity getRelated()
	{
		return related;
	}

	public void setRelated(Unity related)
	{
		this.related = related;
	}

	/**
	 * Identifier of {@link SecForce} that
	 * maintains this rule instance.
	 */
	public String getForce()
	{
		return force;
	}

	public void setForce(String force)
	{
		this.force = force;
	}

	/**
	 * Data of the rule. At the most cases,
	 * XML represented Java Bean object.
	 */
	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}


	/* rule attributes & links */

	private Domain domain;
	private Unity  related;
	private String force;
	private String data;
}