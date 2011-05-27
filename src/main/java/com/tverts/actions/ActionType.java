package com.tverts.actions;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

/**
 * Action type is introduced to find out what actualy do
 * with the target object given to the execution sybsystem.
 *
 * Type is the pair of the user-defined name and the reference
 * class. The latter may be {@code Object.class} when the
 * action type is for any persistent class.
 *
 * User-defined names must have prefix with ':'.
 * System types have no prefix.
 *
 * @author anton.baukin@gmail.com
 */
public class ActionType
{
	/* public: system types */

	public static final ActionType S =
	  new ActionType("save",   Object.class);

	public static final ActionType U =
	  new ActionType("update", Object.class);

	public static final ActionType D =
	  new ActionType("delete", Object.class);


	/* public: constructors */

	public ActionType(String typeName, Class refClass)
	{
		if((typeName = s2s(typeName)) == null)
			throw new IllegalArgumentException();

		if(refClass == null)
			throw new IllegalArgumentException();

		this.typeName = typeName;
		this.refClass = refClass;
	}

	/* public: ActionType interface */

	public final String getTypeName()
	{
		return typeName;
	}

	public final Class  getRefClass()
	{
		return refClass;
	}

	/* public: Object interface */

	public boolean equals(Object o)
	{
		return (o == this) || (

		  (o instanceof ActionType) &&
		  ((ActionType)o).typeName.equals(typeName) &&
		  ((ActionType)o).refClass.equals(refClass)
		);
	}

	public int     hashCode()
	{
		return typeName.hashCode() ^ refClass.hashCode();
	}

	public String  toString()
	{
		return String.format(
		  "action type '%s' on class %s",
		  typeName, refClass.getName()
		);
	}

	/* private: type name */

	private String typeName;
	private Class  refClass;
}