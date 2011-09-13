package com.tverts.hibery.qb;

/**
 * A 'where' clause part: exists restriction.
 *
 * @author anton.baukin@gmail.com
 */
public class WhereExists extends WhereSubqueryBase
{
	/* protected: SelectQuery interface */

	protected StringBuilder buildQueryText(StringBuilder text)
	{
		text = super.buildQueryText(text);

		//?: {has the nested query} wrap it in the clause
		if(text.length() != 0)
			text.insert(0, "exists (").append(')');

		return text;
	}
}