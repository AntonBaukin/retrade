package com.tverts.hibery.sql;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Java DOM */

import org.jdom2.Element;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* com.tverts: hibery (system) */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: support */

import com.tverts.support.LU;
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
		//~: dialect
		this.dialect = SU.s2s(node.getAttributeValue("dialect"));

		//~: select <query> elements
		for(Element n : node.getChildren("query"))
			readQuery(n);
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
	{
		for(String q : queries)
		{
			//~: execute update
			session.createNativeQuery(q).executeUpdate();

			//~: log the query
			logQuery(q);
		}
	}

	protected boolean isSameDialect()
	{
		return (getDialect() == null) || HiberSystem.dialect().
		  getClass().getSimpleName().equalsIgnoreCase(getDialect());
	}


	/* protected: configuring */

	protected void readQuery(Element n)
	{
		queries.add(SU.s2s(n.getText()));
	}


	/* protected: logging */

	protected String  getLog()
	{
		return SQLTaskBase.class.getPackage().getName();
	}

	protected void    logQuery(String q)
	{
		LU.I(getLog(), "\n", q);
	}


	/* protected: task configuration */

	protected String       dialect;

	/**
	 * SQL queries to execute on the action.
	 */
	protected List<String> queries =
	  new ArrayList<String>(1);
}