package com.tverts.actions;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.collectParams;

/* com.tverts: support */

import com.tverts.support.OU;


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


	/* protected: nested actions invocation */

	protected ActionBuildRec nest(ActionBuildRec abr, ActionTaskNested task)
	{
		//?: {has no nested builder}
		if(abr.getNestedBuilder() == null)
			throw new IllegalStateException(String.format(
			  "Action Builder got record without Nested Builder strategy " +
			  "callback and can't build action task [%s]!",
			  ActionsPoint.logsig(task)
			));

		//!: invoke the nested builder
		return abr.getNestedBuilder().nestAction(abr, task);
	}

	/**
	 * Nests the task as {@link #nest(ActionBuildRec, ActionTaskNested)}
	 * does, and checks whether the action build record is complete.
	 * If not, {@link IllegalStateException} is raised.
	 */
	protected ActionBuildRec xnest(ActionBuildRec abr, ActionTaskNested task)
	{
		ActionBuildRec nabr = nest(abr, task);

		//?: {the action build is not complete}
		if(!nabr.isComplete())
			throw new IllegalStateException(String.format(
			  "Action Builder had invoked Nested Builder strategy on the " +
			  "task [%s], but the build with not complete as required!",
			  ActionsPoint.logsig(task)
			));

		return nabr;
	}

	protected ActionBuildRec nest
	  (ActionBuildRec abr, ActionType atype, Object target)
	{
		return this.nest(abr, new ActionTaskNestedWrapped(abr.getTask(),
		  new ActionTaskStruct(atype).setTarget(target).
		    setTx(abr.getTask().getTx())
		));
	}

	protected ActionBuildRec xnest
	  (ActionBuildRec abr, ActionType atype, Object target)
	{
		return this.xnest(abr, new ActionTaskNestedWrapped(abr.getTask(),
		  new ActionTaskStruct(atype).setTarget(target).
		    setTx(abr.getTask().getTx())
		));
	}

	protected ActionBuildRec nest
	  (ActionBuildRec abr, ActionType atype, Object target, Object... params)
	{
		return this.nest(abr, new ActionTaskNestedWrapped(abr.getTask(),
		  new ActionTaskStruct(atype).setTarget(target).
		    setParams(collectParams(params)).setTx(abr.getTask().getTx())
		));
	}

	protected ActionBuildRec xnest
	  (ActionBuildRec abr, ActionType atype, Object target, Object... params)
	{
		return this.xnest(abr, new ActionTaskNestedWrapped(abr.getTask(),
		  new ActionTaskStruct(atype).setTarget(target).
		    setParams(collectParams(params)).setTx(abr.getTask().getTx())
		));
	}

	/* protected: action system support */

	protected void          complete(ActionBuildRec abr)
	{
		//~: mark the record as completed
		abr.setComplete();

		//?: {has no action trigger} crete it
		if(abr.getTrigger() == null)
			createActionTrigger(abr);
	}

	protected void          createActionTrigger(ActionBuildRec abr)
	{
		if(abr.getTriggerCreator() != null)
			abr.setTrigger(abr.getTriggerCreator().createTrigger(abr));
	}

	/**
	 * Returns the action context recorded, or creates and sets
	 * the new one if it does not exist.
	 */
	protected ActionContext context(ActionBuildRec abr)
	{
		ActionContext res = abr.getContext();

		//?: {the context is not installed} create it
		if(res == null)
			abr.setContext(res = createContext(abr));

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

	@SuppressWarnings("unchecked")
	protected void          checkTargetClass(ActionBuildRec abr, Class tclass)
	{
		if(tclass == null) throw new IllegalArgumentException();

		Object target = targetOrNull(abr);
		Class  tarcls = (target == null)?(null):(target.getClass());

		//?: {the target is not a requested class}
		if((tarcls == null) || !tclass.isAssignableFrom(tarcls))
			throw new IllegalStateException(String.format(
			  "Action Builder expects (defined) target of class '%s', " +
			  "but not of the class '%s'", tclass.getName(), OU.cls(tarcls)
			));
	}

	/* protected: various build helpers */

	protected ActionType actionType(ActionBuildRec abr)
	{
		return abr.getTask().getActionType();
	}
}