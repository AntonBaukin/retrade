package com.tverts.servlet.filters;

public class   FilterPoint
       extends FiltersList
{
	/*  Singleton */

	private static FilterPoint INSTANCE =
	  new FilterPoint();

	public static FilterPoint getInstance()
	{
		return INSTANCE;
	}

	protected FilterPoint()
	{}
}
