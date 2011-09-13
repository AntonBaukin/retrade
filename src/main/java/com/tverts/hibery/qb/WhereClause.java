package com.tverts.hibery.qb;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * A collection of 'where' clause restrictions
 * connected with logic operators.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class WhereClause extends WherePartLogic
{
	/* public: WherePart interface */

	public String    buildText()
	{
		String text = s2s(buildWhereText());
		if(text == null) return "";

		return new StringBuilder(6 + text.length()).
		  append("where ").append(text).
		  toString();
	}


	/* protected: query building */

	protected String buildWhereText()
	{
		return super.buildText();
	}
}