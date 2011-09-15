package com.tverts.actions;

/* standard Java classes */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* com.tverts: support */

import com.tverts.support.OU;

/**
 * TODO comment ActionsPoint
 *
 * @author anton.baukin@gmail.com
 */
public class ActionsPoint
{
	/* public: Singleton */

	public static ActionsPoint getInstance()
	{
		return INSTANCE;
	}

	private static final ActionsPoint INSTANCE =
	  new ActionsPoint();

	protected ActionsPoint()
	{}

	/* public: actions build system  */

	public ActionTrigger        action(ActionTask task)
	{
		ActionTrigger trigger = ActionsPoint.getInstance().
		  actionOrNull(task);

		if(trigger == null) throw new IllegalStateException(
		  "Action point couldn't build an action for " + logsig(task)
		);

		return trigger;
	}

	public ActionTrigger        actionOrNull(ActionTask task)
	{
		if(task == null) throw new IllegalArgumentException(
		  "Can't create action on the task undefined!"
		);

		ActionBuildRec abr = new ActionBuildRec(task);

		//!: invoke the root builder
		getRootBuilder().buildAction(abr);

		return abr.isComplete()?(abr.getTrigger()):(null);
	}

	public ActionTrigger        action(ActionType atype, Object target)
	{
		return action(new ActionTaskStruct(atype).setTarget(target));
	}

	public static ActionTrigger actionRun(ActionType atype, Object target)
	{
		ActionTrigger trigger = getInstance().
		  action(new ActionTaskStruct(atype).setTarget(target));

		trigger.run();
		return trigger;
	}

	public ActionTrigger        actionOrNull(ActionType atype, Object target)
	{
		return actionOrNull(new ActionTaskStruct(atype).setTarget(target));
	}

	public ActionTrigger        action
	  (ActionType atype, Object target, Object... params)
	{
		return action(new ActionTaskStruct(atype).
		  setTarget(target).setParams(collectParams(params)));
	}

	public static ActionTrigger actionRun
	  (ActionType atype, Object target, Object... params)
	{
		ActionTrigger trigger = getInstance().
		  action(atype, target, params);

		trigger.run();
		return trigger;
	}

	public ActionTrigger        actionOrNull
	  (ActionType atype, Object target, Object... params)
	{
		return actionOrNull(new ActionTaskStruct(atype).
		  setTarget(target).setParams(collectParams(params)));
	}

	public static ActionTrigger actionOrNullRun
	  (ActionType atype, Object target, Object... params)
	{
		ActionTrigger trigger = getInstance().
		  actionOrNull(atype, target, params);

		if(trigger != null)
			trigger.run();
		return trigger;
	}


	/* public: ActionsPoint bean interface */

	/**
	 * The (delegating composite) builder used to create all
	 * the actions in the system.
	 */
	public ActionBuilder getRootBuilder()
	{
		return rootBuilder;
	}

	public void          setRootBuilder(ActionBuilder builder)
	{
		if(builder == null) throw new IllegalArgumentException();
		this.rootBuilder = builder;
	}


	/* public static: logging support */

	public static final String LOG_ACTION =
	  Action.class.getName();

	public static String logsig(Action action)
	{
		if(action == null)
			return "action undefined";

		return String.format(
		  "action %s with %s", OU.sig(action), logsig(action.getTask())
		);
	}

	public static String logsig(ActionTask task)
	{
		if(task == null)
			return "action task undefined";

		ActionType atype = task.getActionType();
		String     stype = (atype == null)?(null):(atype.toString());
		if(stype == null) stype = "undefined";

		return String.format(
		  "action task of type [%s] on target [%s]",
		  stype, OU.sig(task.getTarget())
		);
	}

	public static String logsig(ActionContext context)
	{
		if(context == null)
			return "action context undefined";

		ActionChain achain = context.getChain();

		return String.format(
		  "action context %s with initial %s with queue size %s",

		  OU.sig(context), logsig(context.getTask()),
		  (achain == null)?("undefined"):("" + achain.size())
		);
	}

	public static String logsig(ActionChain chain)
	{
		if(chain == null)
			return "action chain undefined";

		if(chain.empty())
			return "action chain empty";

		StringBuilder     sb = new StringBuilder(128).
		  append("action chain ").append(OU.sig(chain)).
		  append(" with size ").append(chain.size());

		Iterator<Action>  ai = chain.iterator();

		//?: {the chain is small} print it in one line
		sb.append((chain.size() <= 4)?(" {"):(" {\n"));

		//~: print the first 12 actions
		for(int al = 12;(ai.hasNext() && (al >= 0));al--)//<-- actions limit
			sb.append((al == 12)?(""):(", ")).
			   append(OU.sig(ai.next()));

		//?: {has more actions left}
		if(ai.hasNext())
			sb.append(" ... (more) ...");

		sb.append("}");

		return sb.toString();
	}


	/* public static: misc helpers  */

	@SuppressWarnings("unchecked")
	public static Map    collectParams(Object... params)
	{
		HashMap pmap = new HashMap(params.length / 2);

		for(int i = 0;(i < params.length);i += 2)
			pmap.put(params[i], (i + 1 < params.length)?(params[i + 1]):(null));

		return pmap;
	}

	/* private: root builder reference */

	private ActionBuilder rootBuilder =
	  new ActionBuildersRoot();
}