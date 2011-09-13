package com.tverts.hibery.qb;

/**
 * Logic operators used within {@link WhereClause}.
 *
 * @author anton.baukin@gmail.com
 */
public enum WhereLogic
{
	AND
	{
		public String operator()
		{
			return "and";
		}
	},

	OR
	{
		public String operator()
		{
			return "or";
		}
	},

	NOT
	{
		public String operator()
		{
			return "not";
		}
	};


	/* public: operator */

	public String operator()
	{
		throw new UnsupportedOperationException();
	}

	public String toString()
	{
		return new StringBuilder(2 + operator().length()).
		  append(' ').append(operator()).append(' ').
		  toString();
	}
}
