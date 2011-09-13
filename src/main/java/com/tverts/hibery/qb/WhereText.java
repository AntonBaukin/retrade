package com.tverts.hibery.qb;

/**
 * A leaf part of a 'where' composite clause.
 *
 * It contains the expression of the restriction.
 * The parameters are written as always for HQL,
 * with ':' prefixes. You are not to concern the
 * problems of translating local names used to
 * the names unique within the whole result query.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class WhereText extends WherePartBase
{
	/* public: constructors */

	public WhereText()
	{}

	public WhereText(String query)
	{
		this.query = query;
	}


	/* public: WhereText interface */

	public String    getQuery()
	{
		return query;
	}

	public WhereText setQuery(String query)
	{
		this.query = query;
		return this;
	}


	/* public: WherePart interface */

	public String    buildText()
	{
		return getQuery();
	}

	public WhereText setParamsPrefix(String prefix)
	{
		super.setParamsPrefix(prefix);
		return this;
	}


	/* private: query text */

	private String query;
}
