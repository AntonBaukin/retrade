package com.tverts.hibery.sql;

/* standard Java classes */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Java DOM */

import org.jdom2.Element;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Executes one or more SQL queries if each
 * of the condition queries returns results
 * or not results (depends on the flag).
 *
 *
 * @author anton.baukin@gmail.com
 */
public class QueryIf extends SQLTaskBase
{
	/* public: SQLTask interface */

	public void configure(Element node)
	{
		super.configure(node);

		//~: select <if> elements
		for(Element n : node.getChildren("if"))
			readIf(n);

		//~: select <query> elements
		for(Element n : node.getChildren("query"))
			readQuery(n);
	}


	/* protected: configuring */

	protected void readIf(Element n)
	{
		int i = ifs.size();

		ifs.add(s2s(n.getText()));

		if("true".equals(n.getAttributeValue("empty")))
			empties.add(i);
	}

	protected void readQuery(Element n)
	{
		queries.add(s2s(n.getText()));
	}


	/* protected: SQLTaskBase interface */

	protected boolean required(Connection connection)
	  throws SQLException
	{
		if(!super.required(connection))
			return false;

		Statement s = null;

		for(int i = 0;(i < ifs.size());i++) try
		{
			//~: true when the result must be empty
			boolean empty = empties.contains(i);

			if(s == null)
				s = connection.createStatement();
			s.execute(ifs.get(i));

			if(s.getResultSet() == null)
				throw new IllegalStateException(
				  "SQL If-Task condition must be a select query!");

			//~: true when the result is not empty
			boolean gotit = s.getResultSet().next();

			//?: {not that emptiness}
			if(gotit == empty)
				return false;
		}
		finally
		{
			if(s != null)
				s.close();
		}

		return true;
	}

	protected void act(Connection connection)
	  throws SQLException
	{
		Statement s = null;

		for(String q : queries) try
		{
			if(s == null)
				s = connection.createStatement();
			s.execute(q);
		}
		finally
		{
			if(s != null)
				s.close();
		}
	}


	/* private: conditions and queries */

	private List<String> ifs     =
	  new ArrayList<String>(1);

	private List<String> queries =
	  new ArrayList<String>(1);

	private Set<Integer> empties =
	  new HashSet<Integer>(1);
}