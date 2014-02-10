package com.tverts.hibery.sql;

/* standard Java classes */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

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

	@SuppressWarnings("unchecked")
	protected boolean required(Session session)
	{
		if(!super.required(session))
			return false;

		for(int i = 0;(i < ifs.size());i++)
		{
			//~: true when the result must be empty
			boolean  empty  = empties.contains(i);

			//~: execute select
			List result = session.createSQLQuery(ifs.get(i)).list();

			//?: {not that emptiness}
			if(result.isEmpty() != empty)
				return false;
		}

		return true;
	}

	protected void act(Session session)
	{
		for(String q : queries)
			session.createSQLQuery(q).executeUpdate();
	}


	/* private: conditions and queries */

	private List<String> ifs     =
	  new ArrayList<String>(1);

	private List<String> queries =
	  new ArrayList<String>(1);

	private Set<Integer> empties =
	  new HashSet<Integer>(1);
}