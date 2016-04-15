package com.tverts.hibery.sql;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

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

	public void execute(Session session);
}