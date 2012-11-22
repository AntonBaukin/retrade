package com.tverts.hibery.sql;

/* standard Java classes */

import java.sql.Connection;
import java.sql.SQLException;

/* Java DOM */

import org.jdom2.Element;


/**
 * SQL Task defines an action executed
 * directly on the database level.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SQLTask
{
	/* public: HiberSQLTask interface */

	public void configure(Element node);

	public void execute(Connection connection)
	  throws SQLException;
}