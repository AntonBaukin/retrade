package com.tverts.hibery.sql;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* Java DOM */

import org.jdom2.Element;

/* com.tverts: hibery (system) */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Basic implementation of SQL Tasks.
 * Supports filtering by database dialects
 * obtained via Hibernate configuration.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class SQLTaskBase implements SQLTask
{
	/* public: SQLTaskBase interface */

	public String     getDialect()
	{
		return dialect;
	}


	/* public: SQLTask interface */

	public void       configure(Element node)
	{
		this.dialect = SU.s2s(node.getAttributeValue("dialect"));
	}

	public void       execute(Session session)
	{
		if(required(session))
			act(session);
	}


	/* protected: execution */

	protected boolean required(Session session)
	{
		return isSameDialect();
	}

	protected void    act(Session session)
	{}

	protected boolean isSameDialect()
	{
		return (getDialect() == null) || HiberSystem.dialect().
		  getClass().getSimpleName().equalsIgnoreCase(getDialect());
	}


	/* private: dialect attribute */

	private String dialect;
}
