package com.tverts.actions;

/**
 * The task of checking action builder is to test
 * the action task on the number of supported and
 * configured criteria and to invoke the actual
 * action builder referred.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionBuilderCheckerBase
       extends        ActionBuilderBase
{
	/* public: ActionBuilderCheckerBase (bean) interface */

	public ActionBuilderReference getReference()
	{
		return reference;
	}

	public void setReference(ActionBuilderReference reference)
	{
		this.reference = reference;
	}


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		//?: {not that case | no builders}
		if(!isActionBuildPossible(abr) || (getReference() == null))
			return;

		//!: invoke the builders aggregated
		invokeBuilders(abr);
	}


	/* protected: action build dispatching */

	protected abstract boolean
	               isActionBuildPossible(ActionBuildRec abr);

	protected void invokeBuilders(ActionBuildRec abr)
	{
		for(ActionBuilder ab : getReference().dereferObjects())
		{
			//!: invoke the builder referred
			ab.buildAction(abr);

			//?: {the build is complete} exit
			if(abr.isComplete())
				return;
		}
	}


	/* private: action builders reference */

	private ActionBuilderReference reference;
}