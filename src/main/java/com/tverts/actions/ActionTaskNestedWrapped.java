package com.tverts.actions;

/**
 * Compines two tasks: first to be the new nested
 * task (wrapped), second to be the outer task.
 *
 * @author anton.baukin@gmail.com
 */
public class      ActionTaskNestedWrapped
       extends    ActionTaskWrapper
       implements ActionTaskNested
{
	/* public: constructor */

	public ActionTaskNestedWrapped(ActionTask task, ActionTask outerTask)
	{
		super(task);

		if(outerTask == null) throw new IllegalArgumentException();
		this.outerTask = outerTask;
	}

	/* public: ActionTaskNested interface */

	public ActionTask getOuterTask()
	{
		return outerTask;
	}

	/* private: outer task reference */

	private ActionTask outerTask;
}