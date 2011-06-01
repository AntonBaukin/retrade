package com.tverts.actions;

/* standard Java classes */

import java.util.Collections;
import java.util.List;


/**
 * This implementation of {@link ActionBuilder} provides
 * the essentials needed to create actions.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionBuilderBase
       implements     ActionBuilder, ActionBuilderReference
{
	/* public: ActionBuilderReference interface */

	public List<ActionBuilder> dereferObjects()
	{
		return Collections.<ActionBuilder> singletonList(this);
	}

	/* protected: action system support */

	/**
	 * Returns the action context recorded, or creates and sets
	 * the new one if it does not exist.
	 */
	protected ActionContext context(ActionBuildRec abr)
	{
		ActionContext res = abr.getContext();

		//?: {the context is not installed} create it
		if(res == null)
			res = createContext(abr);

		//?: {the context could not be created} error
		if(res == null) throw new IllegalStateException(
		  "Action Builder could not create Action Context instance!"
		);

		return res;
	}

	protected ActionContext createContext(ActionBuildRec abr)
	{
		//?: {the context creator is defined} invoke it
		if(abr.getContextCreator() != null)
			return abr.getContextCreator().createContext(abr);

		return null;
	}

	/**
	 * Returns the Action Chain of the context. If chain
	 * does not exist the context is malformed.
	 */
	protected ActionChain   chain(ActionBuildRec abr)
	{
		ActionContext ctx = context(abr);
		ActionChain   res = ctx.getChain();

		if(res == null) throw new IllegalStateException(
		  "Action Context has no Action Chain installed!"
		);

		return res;
	}

	protected ActionTask    task(ActionBuildRec abr)
	{
		return abr.getTask();
	}

	protected Object        targetOrNull(ActionBuildRec abr)
	{
		return abr.getTask().getTarget();
	}

	protected Object        target(ActionBuildRec abr)
	{
		Object res = abr.getTask().getTarget();

		if(res == null) throw new IllegalStateException(
		  "Action Builder needs action target that is undefined!"
		);

		return res;
	}

	/* protected: various build helpers */

	protected ActionType actionType(ActionBuildRec abr)
	{
		return abr.getTask().getActionType();
	}
}