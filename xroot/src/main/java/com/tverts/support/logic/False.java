package com.tverts.support.logic;

/**
 * Predicate that always returns {@code false}.
 *
 * @author anton.baukin@gmail.com
 */
public class False implements Predicate
{
	/*  Singleton */

	private static False INSTANCE =
	  new False();

	public static False getInstance()
	{
		return INSTANCE;
	}

	protected False()
	{}

	/* public: Predicate interface */

	public boolean evalPredicate(Object ctx)
	{
		return false;
	}
}
