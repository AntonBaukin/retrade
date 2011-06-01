package com.tverts.actions;

/* standard Java class */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* Apache Commons Collections */

import org.apache.commons.collections.iterators.ReverseListIterator;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.TxContext;
import com.tverts.system.tx.TxContextWrapper;
import com.tverts.system.tx.TxPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec.ContextCreator;
import com.tverts.actions.ActionBuildRec.TriggerCreator;


/**
 * Does no action building, but provides system level
 * implementations related with actions.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionBuilderSystem
       implements     ActionBuilder
{
	/* protected: action build record processing */

	protected void init(ActionBuildRec abr)
	{
		//?: {has no context creator strategy}
		if(abr.getContextCreator() == null)
			abr.setContextCreator(getContextCreator());


		//?: {has no trigger creator strategy}
		if(abr.getTriggerCreator() == null)
			abr.setTriggerCreator(getTriggerCreator());
	}


	/* protected: context creator strategy */

	protected class ContextCreatorInternal implements ContextCreator
	{
		/* public: ContextCreator interface */

		public ActionContext createContext(ActionBuildRec abr)
		{
			return createActionContext(abr);
		}
	}

	protected ContextCreator getContextCreator()
	{
		return (contextCreator != null)?(contextCreator):
		  (contextCreator = createContextCreator());
	}

	protected ContextCreator createContextCreator()
	{
		return new ContextCreatorInternal();
	}

	private ContextCreator contextCreator;


	/* protected: trigger creator strategy */

	protected class TriggerCreatorInternal implements TriggerCreator
	{
		/* public: TriggerCreator interface */

		public ActionTrigger createTrigger(ActionBuildRec abr)
		{
			return createActionTrigger(abr);
		}
	}

	protected TriggerCreator getTriggerCreator()
	{
		return (triggerCreator != null)?(triggerCreator):
		  (triggerCreator = createTriggerCreator());
	}

	protected TriggerCreator createTriggerCreator()
	{
		return new TriggerCreatorInternal();
	}

	private TriggerCreator triggerCreator;


	/* protected: factory methods */

	protected ActionTrigger createActionTrigger(ActionBuildRec abr)
	{
		if(abr.getContext() == null) throw new IllegalStateException(
		  "Can't create Action Trigger for undefined Action Context!"
		);

		return new ActionsRunner(abr.getContext());
	}

	protected ActionContext createActionContext(ActionBuildRec abr)
	{
		return createActionContext(abr.getTask());
	}

	protected ActionContext createActionContext(ActionTask task)
	{
		return new ActionContextStruct(task);
	}

	protected ActionChain   createActionChain(ActionTask task)
	{
		return new ActionChainAsList();
	}

	protected ActionTx      selectActionTx(ActionTask task)
	{
		return (task.getTx() != null)?(task.getTx()):
		  (createActionTx(task));
	}

	protected ActionTx      createActionTx(ActionTask task)
	{
		return new ActionTxContext(TxPoint.txContext());
	}

	protected Map           createActionContextMap(ActionTask task)
	{
		return new HashMap(3);
	}


	/* protected: action build support */

	protected boolean isComplete(ActionBuildRec abr)
	{
		return abr.isComplete();
	}

	protected boolean complete(ActionBuildRec abr)
	{
		//?: {the action is still not copleted}
		if(!isComplete(abr))
			return false;

		//?: {there is no action trigger created} create it here
		if(abr.getTrigger() == null)
			abr.setTrigger(createActionTrigger(abr));

		//~: check the completed action validity
		checkCompleted(abr);

		return true;
	}

	protected void    checkActionBuildRec(ActionBuildRec abr)
	{
		if(abr == null) throw new IllegalArgumentException(
		  "Action build record is not defined!"
		);

		checkActionTask(abr.getTask());
	}

	protected void    checkActionTask(ActionTask task)
	{
		if(task == null) throw new IllegalArgumentException(
		  "Initial Action Task is not defined in the build record!"
		);

		if(task.getActionType() == null)
			throw new IllegalArgumentException(
			  "Action task type is not defined!"
			);
	}

	protected void    checkCompleted(ActionBuildRec abr)
	{
		//?: {has no action trigger created}
		if(abr.getTrigger() == null) throw new IllegalStateException(
		  "Action Build may not ne completed without " +
		  "Action Trigger instance created!");
	}


	/* protected: action context implementation */

	protected class   ActionContextStruct
	       implements ActionContext
	{
		/* public: constructor */

		public ActionContextStruct(ActionTask task)
		{
			this.task     = task;
			this.chain    = createActionChain(task);
			this.actionTx = createActionTx(task);
		}

		/* public: ActionContext interface */

		public ActionTask  getTask()
		{
			return task;
		}

		public ActionChain getChain()
		{
			return chain;
		}

		public ActionTx    getActionTx()
		{
			return actionTx;
		}

		public Map         getContext()
		{
			return (context != null)?(context):
			  (context = createActionContextMap(getTask()));
		}

		public ActionError getError()
		{
			return error;
		}

		public void        setError(ActionError error)
		{
			this.error = error;
		}

		/* private: context structures */

		private ActionTask  task;
		private ActionChain chain;
		private ActionTx    actionTx;
		private Map         context;
		private ActionError error;
	}


	/* protected static: action chain implementation */

	/**
	 * Stores the chain in the array in reversed order.
	 * It is good when placing new actions as the first
	 * elements of the chain.
	 */
	public static final class ActionChainAsList implements ActionChain
	{
		/* public: ActionChain interface */

		public Action      first()
		{
			return (actions.isEmpty())?(null):
			  (actions.get(actions.size() - 1));
		}

		public ActionChain first(Action obj)
		{
			actions.add(obj);
			return this;
		}

		public Action      last()
		{
			return (actions.isEmpty())?(null):
			  (actions.get(0));
		}

		public ActionChain last(Action obj)
		{
			actions.add(0, obj);
			return this;
		}

		public ActionChain insert(Action ref, Action obj)
		{
			//~: find the element
			int i = actions.indexOf(ref);
			if(i == -1) throw new IllegalStateException();

			//HINT: in the reversed order 'before' means 'after'
			actions.add(i + 1, obj);

			return this;
		}

		public ActionChain append(Action ref, Action obj)
		{
			//~: find the element
			int i = actions.indexOf(ref);
			if(i == -1) throw new IllegalStateException();

			//HINT: in the reversed order 'after' means 'before'
			actions.add(i, obj);

			return this;
		}

		public ActionChain remove(Action obj)
		{
			actions.remove(obj);
			return this;
		}

		public boolean     empty()
		{
			return actions.isEmpty();
		}

		public int         size()
		{
			return actions.size();
		}

		/* public: Iterable interface */

		@SuppressWarnings("unchecked")
		public Iterator<Action> iterator()
		{
			return new ReverseListIterator(actions);
		}

		/* private: actions collection */

		private ArrayList<Action> actions =
		  new ArrayList<Action>(8);
	}


	/* protected static: action transaction context */

	protected static class ActionTxContext
	          extends      TxContextWrapper
	          implements   ActionTx
	{
		/* public: constructor */

		public ActionTxContext(TxContext tx)
		{
			super(tx);
		}
	}
}