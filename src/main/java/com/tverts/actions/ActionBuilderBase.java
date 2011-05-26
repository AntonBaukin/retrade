package com.tverts.actions;

/* standard Java classes */

import java.util.Collections;
import java.util.List;


/**
 * TODO comment ActionBuilderBase
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
}