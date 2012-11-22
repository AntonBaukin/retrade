package com.tverts.hibery.sql;

/* standard Java classes */

import java.sql.Connection;
import java.sql.SQLException;

/* Java DOM */

import org.jdom2.Element;

/* com.tverts: hibery (system) */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Basic implementation of SQL Tasks.
 * Supports filtering by database dialects
 * obtained via Hibernate configuration.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SQLTaskBase implements SQLTask
{
	/* public: SQLTaskBase interface */

	public String     getDialect()
	{
		return dialect;
	}


	/* public: SQLTask interface */

	public void       configure(Element node)
	{
		this.dialect = s2s(
		  node.getAttributeValue("dialect"));
	}

	public void       execute(Connection connection)
	  throws SQLException
	{
		if(required(connection))
			act(connection);
	}


	/* protected: execution */

	protected boolean required(Connection connection)
	  throws SQLException
	{
		return isSameDialect();
	}

	protected void    act(Connection connection)
	  throws SQLException
	{}

	protected boolean isSameDialect()
	{
		return (getDialect() == null) || HiberSystem.dialect().
		  getClass().getSimpleName().equalsIgnoreCase(getDialect());
	}


	/* private: dialect attribute */

	private String dialect;
}
