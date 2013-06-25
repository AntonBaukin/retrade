package com.tverts.endure.tree;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionWithTxBase;
import com.tverts.actions.ActionsCollection.EnsurePredicate;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.NumericIdentity;
import com.tverts.endure.United;

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

	/**
	 * Adds United instance defined by parameter
	 * {@link #PARAM_ITEM} to this folder.
	 *
	 * Note that the instance is not removed
	 * from any of the Folders of the same
	 * Tree Domain.
	 */
	public static final ActionType ADD    =
	  new ActionType(TreeFolder.class, "add");


	/* parameters */

	/**
	 * Parameter with {@link United} instance to
	 * add to this Folder.
	 */
	public static final String PARAM_ITEM =
	  ActTreeFolder.class.getName() + ": item";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveTreeFolder(abr, false);

		if(ENSURE.equals(actionType(abr)))
			saveTreeFolder(abr, true);

		if(ADD.equals(actionType(abr)))
			addToTreeFolder(abr);
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

	protected void addToTreeFolder(ActionBuildRec abr)
	{
		//?: {target is not a Tree Folder}
		checkTargetClass(abr, TreeFolder.class);

		//~: obtain Unity to add
		United u = param(abr, PARAM_ITEM, United.class);
		if(u == null) throw EX.arg("No Unity (PARAM_ITEM) set!");

		//~: add item to the folder
		chain(abr).first(new AddToFolderAction(task(abr), u));

		complete(abr);
	}


	/* folder exists predicate */

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


	/* folder item exists predicate */

	protected static class AddToFolderAction
	          extends      ActionWithTxBase
	{
		/* public: constructor */

		public AddToFolderAction(ActionTask task, United unity)
		{
			super(task);
			this.unity = unity;
		}


		/* public: Action interface */

		protected TreeItem item;

		public Object getResult()
		{
			return item;
		}


		/* public: ActionBase interface */

		protected void execute()
		  throws Throwable
		{
			//~: search the item already existing in the folder
			item = bean(GetTree.class).getTreeItem(
			  target(TreeFolder.class).getPrimaryKey(),
			  unity.getPrimaryKey()
			);

			//?: {found it} nothing to do
			if(item != null) return;

			//~: create new item
			item = new TreeItem();

			//~: primary key
			setPrimaryKey(session(), item,
			  isTestInstance(target(TreeFolder.class)));

			//~: folder
			item.setFolder(target(TreeFolder.class));

			//~: item unity
			item.setItem(unity.getUnity());


			//!: do save
			session().save(item);
		}


		/* the unity to add to the folder */

		protected final United unity;
	}
}