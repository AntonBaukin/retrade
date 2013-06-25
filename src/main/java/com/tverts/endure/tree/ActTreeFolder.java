package com.tverts.endure.tree;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.EnsurePredicate;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.NumericIdentity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.logic.Predicate;


/**
 * Actions builder on {@link TreeFolder} entities.
 *
 * @author anton.baukin@gmail.com
 */
public class ActTreeFolder extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType ENSURE =
	  ActionType.ENSURE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveTreeFolder(abr, false);

		if(ENSURE.equals(actionType(abr)))
			saveTreeFolder(abr, true);
	}


	/* protected: action methods */

	protected void saveTreeFolder(ActionBuildRec abr, boolean ensure)
	{
		//?: {target is not a Tree Folder}
		checkTargetClass(abr, TreeFolder.class);

		//~: folder missing predicate
		Predicate p = !ensure?(null):(new TreeFolderMissing());

		//~: save the folder
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setForceTest(isTestInstance(target(abr, TreeFolder.class).getDomain())).
		  setPredicate(p)
		);

		complete(abr);
	}


	/* protected static: existing predicate */

	protected class TreeFolderMissing implements EnsurePredicate
	{
		/* public: Predicate interface */

		public boolean evalPredicate(Object ctx)
		{
			if(searched) return (existing == null); searched = true;

			//~: search for that folder
			TreeFolder tf = (TreeFolder) ((Action) ctx).getTask().getTarget();
			existing = bean(GetTree.class).getFolder(
			  tf.getDomain().getPrimaryKey(), tf.getCode());

			return (existing == null);
		}


		/* public: EnsurePredicate interface */

		public NumericIdentity getExistingInstance()
		{
			if(!searched) throw EX.state();
			return existing;
		}

		private TreeFolder existing;
		private boolean    searched;
	}
}