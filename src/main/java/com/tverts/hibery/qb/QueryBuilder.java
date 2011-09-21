package com.tverts.hibery.qb;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Implements Builder pattern to distribute the
 * creation of a query between the various parts
 * of the application.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class QueryBuilder extends SelectQuery
{
	/* public: build Query */

	public Query buildQuery(Session session)
	{
		//~: create the query
		Query               result =
		  session.createQuery(buildQueryText());

		//~: collect & set the parameters
		Map<String, Object> params =
		  new HashMap<String, Object>(17);

		getClauseWhere().collectParams(params);
		for(Entry<String, Object> pe : params.entrySet())
			result.setParameter(pe.getKey(), pe.getValue());

		//~: apply the limits
		if(getFirstRow() != null)
			result.setFirstResult(getFirstRow());

		if(getLimit() != null)
			result.setMaxResults(getLimit());

		return result;
	}


	/* public: entities naming */

	/**
	 * When naming, it is assumed that the name of the
	 * entity in the query is it's class simple name.
	 */
	public QueryBuilder nameEntity(Class entity)
	{
		return nameEntity(entity.getSimpleName(), entity.getName() );
	}

	public QueryBuilder nameEntity(String queryName, Class entity)
	{
		return nameEntity(queryName, entity.getName());
	}

	/**
	 * Associates the name of the entity in the
	 * query's 'where' clause with the full class name.
	 *
	 * When building the query string, the name of the
	 * entity in the query (surrounded by spaces) is replaced
	 * with the full name.
	 */
	public QueryBuilder nameEntity(String queryName, String fullName)
	{
		getEntityNames().put(queryName, fullName);
		return this;
	}


	/* public: QueryBuilder interface (the limits) */

	public Integer getFirstRow()
	{
		return firstRow;
	}

	public void    setFirstRow(Integer firstRow)
	{
		this.firstRow = firstRow;
	}

	public Integer getLimit()
	{
		return limit;
	}

	public void    setLimit(Integer limit)
	{
		this.limit = limit;
	}


	/* protected: SelectQuery interface */

	protected StringBuilder buildQueryText(StringBuilder text)
	{
		text = super.buildQueryText(text);

		if(!getEntityNames().isEmpty())
			nameEntities(text);

		return text;
	}

	/* protected: entity names support */

	protected Map<String, String>
	                    getEntityNames()
	{
		return (entityNames != null)?(entityNames):
		  (entityNames = new HashMap<String, String>(5));
	}

	protected void      nameEntities(StringBuilder s)
	{
		//c: for all named entities
		for(Entry<String, String> e : getEntityNames().entrySet())
		{
			//~: prepare the entity name
			String en = s2s(e.getKey());
			if(en == null) continue;

			//c: for all occurrences of the name
			for(int i = s.indexOf(en, 0);(i != -1);)
			{
				//?: {the previous character is not a whitespace} skip
				if((i != 0) && !Character.isWhitespace(s.charAt(i - 1)))
				{
					i = s.indexOf(en, i + en.length());
					continue;
				}

				//?: {the next character is not a whitespace} skip
				if((i + en.length() < s.length()) &&
				   !Character.isWhitespace(s.charAt(i + en.length()))
				  )
				{
					i = s.indexOf(en, i + en.length());
					continue;
				}

				//!: do replace this occurrence
				s.replace(i, i + en.length(), e.getValue());

				//~: go to the next occurrence
				i = s.indexOf(en, i + e.getValue().length());
			}
		}
	}


	/* private: entity names */

	// query name -> full class name 
	private Map<String, String> entityNames;

	/* private: limits */

	private Integer firstRow;
	private Integer limit;
}