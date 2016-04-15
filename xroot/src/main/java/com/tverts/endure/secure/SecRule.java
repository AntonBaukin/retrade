package com.tverts.endure.secure;

/* com.tverts: endure (core) */

import com.tverts.endure.Unity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.Entity;

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
       extends    Entity
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
	 * Hidden rules are not visible in UI
	 * and maintained by Forces for their
	 * special needs.
	 *
	 * Hidden rules has system information titles.
	 */
	public boolean isHidden()
	{
		return hidden;
	}

	public void setHidden(boolean hidden)
	{
		this.hidden = hidden;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
		this.titleLo = null;
	}

	public String getTitleLo()
	{
		return (titleLo != null)?(titleLo):
		  (title == null)?(null):(titleLo = title.toLowerCase());
	}

	public void setTitleLo(String titleLo)
	{
		this.titleLo = titleLo;
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

	private Domain  domain;
	private Unity   related;
	private String  force;
	private boolean hidden;

	private String  title;
	private String  titleLo;
	private String  data;
}