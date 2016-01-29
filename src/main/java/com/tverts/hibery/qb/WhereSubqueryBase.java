package com.tverts.hibery.qb;

/* standard Java classes */

import java.util.Map;

/**
 * An abstract variant of a part of 'where' clause
 * generating a nested HQL query.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class WhereSubqueryBase
       extends        SelectQuery
       implements     WherePartComposite
{
	/* public: WherePart interface */

	public Map<String, Object>
	              getParams()
	{
		return getClauseWhere().getParams();
	}

	public String getParamsPrefix()
	{
		return getClauseWhere().getParamsPrefix();
	}

	public String buildText()
	{
		return buildQueryText();
	}


	/* public: WherePartComposite interface */

	public WhereSubqueryBase
	               addPart(WherePart part)
	{
		getClauseWhere().addPart(part);
		return this;
	}

	public boolean isEmpty()
	{
		return getClauseWhere().isEmpty();
	}

	public String  lastPartName()
	{
		return getClauseWhere().lastPartName();
	}

	public WhereSubqueryBase
	               addPart(String name, WherePart part)
	{
		getClauseWhere().addPart(name, part);
		return this;
	}

	public void    setGlobalPrefix(String prefix)
	{
		getClauseWhere().setGlobalPrefix(prefix);
	}

	public void    collectParams(Map<String, Object> result)
	{
		getClauseWhere().collectParams(result);
	}
}
