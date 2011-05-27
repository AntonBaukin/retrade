package com.tverts.actions;

/**
 * Does not build actions by itself. It aggregates
 * a reference to actions and polls them.
 *
 * @author anton.baukin@gmail.com
 */
public class ActionBuildersRoot extends ActionBuilderSystem
{
	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		if(abr == null) throw new IllegalArgumentException();

		if(abr.isComplete() || (getReference() == null))
			return;

		for(ActionBuilder ab : getReference().dereferObjects())
		{
			//!: delegate the building
			ab.buildAction(abr);

			//?: {the action build is complete} quit
			if(abr.isComplete())
				return;
		}
	}

	/* public: ActionBuildersRoot bean interface */

	public ActionBuilderReference
	            getReference()
	{
		return reference;
	}

	public void setReference(ActionBuilderReference reference)
	{
		this.reference = reference;
	}

	/* private: action builders reference */

	private ActionBuilderReference reference;
}