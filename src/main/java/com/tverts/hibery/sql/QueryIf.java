package com.tverts.hibery.sql;

/* Java */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* Java DOM */

import org.jdom2.Element;

/* com.tverts: support */

import com.tverts.support.SU;


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
	}

	protected void readIf(Element n)
	{
		int i = ifs.size();

		ifs.add(SU.s2s(n.getText()));

		if("true".equals(n.getAttributeValue("empty")))
			empties.add(i);
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


	/* protected: task configuration */

	protected List<String> ifs =
	  new ArrayList<String>(1);

	protected Set<Integer> empties =
	  new HashSet<Integer>(1);
}