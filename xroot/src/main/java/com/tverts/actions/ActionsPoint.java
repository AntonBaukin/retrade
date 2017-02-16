package com.tverts.actions;

/* standard Java classes */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Point to build actions on the application
 * domain, and to run them.
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


	/* Shared Parameters of the Actions */

	public static final String NO_FLUSH   =
	  ActionsPoint.class.getName() + ": skip session flush";

	public static final String UNITY_TYPE =
	  ActionsPoint.class.getName() + ": unity type";


	/* public: actions build system  */

	public ActionTrigger        action(ActionTask task)
	{
		return EX.assertn(
		  ActionsPoint.getInstance().actionOrNull(task),
		  "Action point couldn't build an action for ", logsig(task)
		);
	}

	public ActionTrigger        actionOrNull(ActionTask task)
	{
		ActionBuildRec abr = new ActionBuildRec(EX.assertn(task,
		  "Can't create action on the task undefined!"));

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
		return actionRun(atype, target, true, null);
	}

	public ActionTrigger        actionOrNull(ActionType atype, Object target)
	{
		return actionOrNull(new ActionTaskStruct(atype).setTarget(target));
	}

	public ActionTrigger        action
	  (ActionType atype, Object target, Object... params)
	{
		return action(new ActionTaskStruct(atype).
		  setTarget(target).setParams(collectParams(params))
		);
	}

	public static ActionTrigger actionRun
	  (ActionType atype, Object target, Object... params)
	{
		return actionRun(atype, target, true, collectParams(params));
	}

	@SuppressWarnings("unchecked")
	public static ActionTrigger actionRun(ActionType atype, Object target, Map params)
	{
		return actionRun(atype, target, true, params);
	}

	public ActionTrigger        actionOrNull
	  (ActionType atype, Object target, Object... params)
	{
		return actionOrNull(new ActionTaskStruct(atype).
		  setTarget(target).setParams(collectParams(params))
		);
	}

	public static ActionTrigger actionOrNullRun
	  (ActionType atype, Object target, Object... params)
	{
		return actionRun(atype, target, false, collectParams(params));
	}

	@SuppressWarnings("unchecked")
	public static ActionTrigger actionRun
	  (ActionType atype, Object target, boolean required, Map params)
	{
		//~: build the actions (trigger)
		long          td      = System.currentTimeMillis();
		ActionTask    task    = new ActionTaskStruct(atype).
		  setTarget(target).setParams(params);
		ActionTrigger trigger = getInstance().actionOrNull(task);

		if((System.currentTimeMillis() - td > LU.XTD) && LU.isD(LU.LOGT))
			LU.D(LU.LOGT, "build action [", atype.getActionName(), "] on target [",
			  LU.sig(target), "] took ", LU.td(td), '!');

		//?: {not found}
		if(trigger == null) if(required)
			throw EX.ass("Action point couldn't build an action for ", logsig(task));
		else
			return null;

		//!: execute the trigger
		td = System.currentTimeMillis();
		trigger.run();

		if((System.currentTimeMillis() - td > LU.XTD) && LU.isD(LU.LOGT))
			LU.D(LU.LOGT, "run action [", atype.getActionName(), "] on target [",
			  LU.sig(target), "] took ", LU.td(td), '!');

		return trigger;
	}

	@SuppressWarnings("unchecked")
	public static <O> O         actionResult(Class<O> cls, ActionTrigger trigger)
	{
		for(Action o : trigger.getActionContext().getChain())
		{
			Object obj = o.getResult();

			if((obj != null) && cls.isAssignableFrom(obj.getClass()))
			{
				return (O)obj;
			}
		}

		return null;
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
		this.rootBuilder = EX.assertn(builder);
	}


	/* public static: logging support */

	public static String logsig(Action action)
	{
		if(action == null)
			return "action undefined";

		return String.format(
		  "action %s with %s", LU.sig(action), logsig(action.getTask())
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
		  stype, LU.sig(task.getTarget())
		);
	}

	public static String logsig(ActionContext context)
	{
		if(context == null)
			return "action context undefined";

		ActionChain achain = context.getChain();

		return String.format(
		  "action context %s with initial %s with queue size %s",

		  LU.sig(context), logsig(context.getTask()),
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
		  append("action chain ").append(LU.sig(chain)).
		  append(" with size ").append(chain.size());

		Iterator<Action>  ai = chain.iterator();

		//?: {the chain is small} print it in one line
		sb.append((chain.size() <= 4)?(" {"):(" {\n"));

		//~: print the first 12 actions
		for(int al = 12;(ai.hasNext() && (al >= 0));al--)//<-- actions limit
			sb.append((al == 12)?(""):(", ")).
			   append(LU.sig(ai.next()));

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