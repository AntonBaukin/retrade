package com.tverts.hibery.system;

/* Hibernate Persistence Layer */

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;


/**
 * Extended support for PostgreSQL 9.x database server.
 *
 * @author anton.baukin@gmail.com.
 */
public class PostgreSQLDialect extends PostgreSQL9Dialect
{
	/* public: constructor */

	public PostgreSQLDialect()
	{
		//~: string_agg()
		registerFunction("string_agg",
		  new SQLFunctionTemplate(StandardBasicTypes.STRING, "string_agg(?1, ?2)"));
	}
}