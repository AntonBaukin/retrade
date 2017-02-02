package com.tverts.actions;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

/**
 * Action type is introduced to find out what actually do
 * with the target object given to the execution subsystem.
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

	/**
	 * Save action type is used to save the instance
	 * already created. Action builder may also create
	 * and save automatically related instances.
	 */
	public static final ActionType SAVE   =
	  new ActionType("save", Object.class);

	/**
	 * Does the same as save type, but the target
	 * object here is not that to save, but a cause
	 * object to create, initialize and save the instance
	 * of the class this action builder is for.
	 */
	public static final ActionType CREATE =
	  new ActionType("create", Object.class);

	public static final ActionType UPDATE =
	  new ActionType("update", Object.class);

	public static final ActionType DELETE =
	  new ActionType("delete", Object.class);

	/**
	 * This action type means check-update
	 * with create on demand.
	 */
	public static final ActionType ENSURE =
	  new ActionType("ensure", Object.class);

	/**
	 * Depending on the actual type of the target
	 * entity this action type means to update
	 * the related views. For some targets is are
	 * to update the views of the related entities.
	 */
	public static final ActionType REVIEW =
	  new ActionType("review", Object.class);


	/* shared parameters of the actions */

	/**
	 * Send this parameter to defines the predicate
	 * of the actions. See {@link Action#getPredicate()}.
	 *
	 * Note that this parameter has no default support,
	 * and each builder may or may not check and use it.
	 */
	public static final String PREDICATE =
	  ActionType.class.getName() + ": predicate";


	/* public: constructors */

	public ActionType(String actionName, Class goalClass)
	{
		if((actionName = s2s(actionName)) == null)
			throw new IllegalArgumentException();

		if(goalClass == null)
			throw new IllegalArgumentException();

		this.actionName = actionName;
		this.goalClass  = goalClass;
	}

	public ActionType(Class goalClass, String actionName)
	{
		this(actionName, goalClass);
	}


	/* public: ActionType interface */

	public final String getActionName()
	{
		return actionName;
	}

	public final Class  getGoalClass()
	{
		return goalClass;
	}


	/* public: Object interface */

	public boolean equals(Object o)
	{
		return (o == this) || (

		  (o instanceof ActionType) &&
		  ((ActionType)o).actionName.equals(actionName) &&
		  ((ActionType)o).goalClass.equals(goalClass)
		);
	}

	public int     hashCode()
	{
		return actionName.hashCode() ^ goalClass.hashCode();
	}

	public String  toString()
	{
		return String.format(
		  "'%s' on class %s",
		  actionName, goalClass.getSimpleName()
		);
	}


	/* private: type name */

	private String actionName;
	private Class  goalClass;
}