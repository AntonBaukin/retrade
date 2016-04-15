package com.tverts.hibery.qb;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sXl;


/**
 * Represents a select HQL query.
 *
 * @author anton.baukin@gmail.com
 */
public class SelectQuery
{
	/* public: query build procedure */

	/**
	 * Returns the HQL resulting text.
	 */
	public String       buildQueryText()
	{
		return buildQueryText(null).toString();
	}


	/* public: access query clauses */

	/**
	 * The columns clause. Without 'select' prefix.
	 * It is added automatically if the columns list
	 * is not empty.
	 */
	public CharSequence getClauseSelect()
	{
		return clauseSelect;
	}

	/**
	 * The entities list and joins.
	 * Without 'from' prefix.
	 */
	public CharSequence getClauseFrom()
	{
		return clauseFrom;
	}

	/**
	 * The list of Group By expressions.
	 * Has no 'group by' prefix.
	 */
	public CharSequence getClauseGroupBy()
	{
		return clauseGroupBy;
	}

	/**
	 * The list of Having expressions.
	 * Has no 'having' prefix.
	 */
	public CharSequence getClauseHaving()
	{
		return clauseHaving;
	}

	/**
	 * The list of Order By expressions.
	 * Has no 'order by' prefix.
	 */
	public CharSequence getClauseOrderBy()
	{
		return clauseOrderBy;
	}

	public WhereClause  getClauseWhere()
	{
		return (clauseWhere != null)?(clauseWhere):
		  (clauseWhere = new WhereClause());
	}

	public void         setClauseSelect(CharSequence clause)
	{
		this.clauseSelect = clause;
	}

	public void         setClauseFrom(CharSequence clause)
	{
		this.clauseFrom = clause;
	}

	public void         setClauseGroupBy(CharSequence clause)
	{
		this.clauseGroupBy = clause;
	}

	public void         setClauseHaving(CharSequence clause)
	{
		this.clauseHaving = clause;
	}

	public void         setClauseOrderBy(CharSequence clause)
	{
		this.clauseOrderBy = clause;
	}


	/* protected: query building */

	protected StringBuilder buildQueryText(StringBuilder text)
	{
		String select = s2s(getClauseSelect());
		String from   = s2s(getClauseFrom());
		String where  = getClauseWhere().buildText();
		String group  = s2s(getClauseGroupBy());
		String having = s2s(getClauseHaving());
		String order  = s2s(getClauseOrderBy());
		int    length = 32 + sXl(select, from, group, having, order, where);

		//~: prepare the result buffer
		if(text == null)
			text = new StringBuilder(length);
		else
			text.ensureCapacity(length);

		//select clause (may be empty in HQL)
		if(select != null)
			text.append("select ").append(select);

		//?: {from clause is empty} illegal state
		if(from == null) throw new IllegalStateException(
		  "FROM clause of select query may not be empty!");

		//~: from clause
		if(select != null) text.append(' ');
		text.append("from ").append(from);

		//~: where clause
		if(where != null) text.
		  append((text.length() != 0)?(" "):("")).
		  append(where);

		//~: group by clause
		if(group != null) text.
		  append(" group by ").
		  append(group);

		//~: having clause
		if(having != null) text.
		  append(" having ").
		  append(having);

		//~: order by clause
		if(order != null) text.
		  append(" order by ").
		  append(order);

		return text;
	}


	/* private: query clauses */

	private CharSequence clauseSelect;
	private CharSequence clauseFrom;
	private CharSequence clauseGroupBy;
	private CharSequence clauseHaving;
	private CharSequence clauseOrderBy;

	private WhereClause  clauseWhere;
}
