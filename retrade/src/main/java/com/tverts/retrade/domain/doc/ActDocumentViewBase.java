package com.tverts.retrade.domain.doc;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: endure (core) */

import com.tverts.endure.United;
import com.tverts.endure.core.GetUnity;

/* com.tverts: retrade domain core */

import com.tverts.retrade.domain.ActionBuilderReTrade;


/**
 * Action builder basic implementation handling
 * {@link DocumentView} instances.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActDocumentViewBase
       extends ActionBuilderReTrade
{
	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		//?: {is that view type} dispatch the building
		if(isThatDocumentView(abr))
			selectBuildActions(abr);
	}


	/* protected: action build dispatching */

	protected abstract void selectBuildActions(ActionBuildRec abr);

	protected boolean       isThatDocumentView(ActionBuildRec abr)
	{
		return DocumentView.class.equals(targetClass(abr)) &&
		  isThatViewOwner(abr);
	}

	protected boolean       isThatViewOwner(ActionBuildRec abr)
	{
		return true;
	}


	/* protected: misc support */

	protected DocumentView  documentView(ActionBuildRec abr)
	{
		return target(abr, DocumentView.class);
	}

	protected United        viewOwner(ActionBuildRec abr)
	{
		United res = bean(GetUnity.class).getUnited(
		  documentView(abr).getViewOwner().getPrimaryKey());

		if(res == null) throw new IllegalStateException(String.format(
		  "No Document View owner instance found by the key [%d]!",
		  documentView(abr).getViewOwner().getPrimaryKey()
		));

		return res;
	}

	@SuppressWarnings("unchecked")
	protected <O extends United> O
	                        viewOwner(ActionBuildRec abr, Class<O> ownerClass)
	{
		United res = viewOwner(abr);

		if(!ownerClass.isAssignableFrom(ownerClass))
			throw new IllegalStateException(String.format(
			  "The owner of the Document View [%d] has class '%s', " +
			  "but not the class expected '%s'!",

			  documentView(abr).getViewOwner().getPrimaryKey(),
			  res.getClass().getName(), ownerClass.getName()
			));

		return (O)res;
	}

	protected Class         viewOwnerClass(ActionBuildRec abr)
	{
		return documentView(abr).getViewOwner().getUnityType().getTypeClass();
	}
}