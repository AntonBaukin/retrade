package com.tverts.actions;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

/**
 * TODO comment ActionType
 *
 * @author anton.baukin@gmail.com
 */
public class ActionType
{
	/* public: predefined type */

	public static final ActionType S =
	  new ActionType("save");

	public static final ActionType U =
	  new ActionType("update");

	public static final ActionType D =
	  new ActionType("delete");


	/* public: constructor */

	public ActionType(String typeName)
	{
		if((typeName = s2s(typeName)) == null)
			throw new IllegalArgumentException();

		this.typeName = typeName;
	}

	/* public: ActionType interface */

	public final String getTypeName()
	{
		return typeName;
	}

	/* public: Object interface */

	public boolean equals(Object o)
	{
		return (o == this) || (

		  (o instanceof ActionType) &&
		  ((ActionType)o).getTypeName().equals(getTypeName())
		);
	}

	public int     hashCode()
	{
		return getTypeName().hashCode();
	}

	/* private: type name */

	private String typeName;
}