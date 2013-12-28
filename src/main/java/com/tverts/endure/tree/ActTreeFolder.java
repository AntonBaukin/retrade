package com.tverts.endure.tree;

/* standard Java classes */

import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: transactions */

import com.tverts.system.tx.TxPoint;

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
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;

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
	 * Tree Domain unless {@link #PARAM_SINGLE}
	 * flag is set to true.
	 *
	 * Also note that if item already exists in
	 * the folder, no other items would be removed.
	 */
	public static final ActionType ADD    =
	  new ActionType(TreeFolder.class, "add");


	/* parameters */

	/**
	 * Unity Type of the Folder to add.
	 */
	public static final String PARAM_TYPE   =
	  ActTreeFolder.class.getName() + ": type";

	/**
	 * Parameter with {@link United} instance to
	 * add to this Folder.
	 */
	public static final String PARAM_ITEM   =
	  ActTreeFolder.class.getName() + ": item";

	/**
	 * Flag of add item to the folder action.
	 * If set, before adding all items to the
	 * same entity are first removed from all
	 * the folders of the tree.
	 */
	public static final String PARAM_SINGLE =
	  ActTreeFolder.class.getName() + ": single item only";


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

		//?: {set test primary key}
		TreeFolder f = target(abr, TreeFolder.class);
		if((f.getPrimaryKey() == null))
			if(isTestInstance(f.getDomain()))
				setPrimaryKey(session(abr), f, true);

		//~: folder missing predicate
		Predicate p = !ensure?(null):(new TreeFolderMissing());

		//~: save the folder
		chain(abr).first(new SaveNumericIdentified(task(abr)).setPredicate(p));

		//~: set tree domain unity (is executed first!)
		xnest(abr, ActUnity.CREATE, f,
		  PREDICATE, p, ActUnity.UNITY_TYPE, getUnityType(abr)
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
		chain(abr).first(new AddToFolderAction(task(abr), u).
		  setSingle(flag(abr, PARAM_SINGLE)));

		complete(abr);
	}


	/* protected: actions build support */

	protected UnityType getUnityType(ActionBuildRec abr)
	{
		Object type = param(abr, PARAM_TYPE);

		if(type instanceof UnityType)
			return (UnityType) type;

		if(type instanceof String)
			return UnityTypes.unityType(TreeFolder.class, (String)type);

		throw EX.state(getClass().getSimpleName(),
		  " has no Tree Folder Type parameter!");
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


	/* action to add United to the folder */

	protected static class AddToFolderAction
	          extends      ActionWithTxBase
	{
		/* public: constructor */

		public AddToFolderAction(ActionTask task, United unity)
		{
			super(task);
			this.unity = unity;
		}


		/* public: AddToFolderAction interface */

		public AddToFolderAction setSingle(boolean single)
		{
			this.single = single;
			return this;
		}


		/* public: Action interface */

		protected TreeItem item;

		/**
		 * Returns the item was created or found existing.
		 */
		public Object getResult()
		{
			return item;
		}


		/* public: ActionBase interface */

		protected void execute()
		{
			TreeFolder folder = target(TreeFolder.class);
			boolean    testit = isTestInstance(folder);

			//~: search the item already existing in the folder
			item = bean(GetTree.class).getTreeItem(
			  folder.getPrimaryKey(), unity.getPrimaryKey()
			);

			//?: {found it} nothing to do
			if(item != null) return;

			//?: {single item instance only}
			if(single) makeSingle();

			//~: create new item
			item = new TreeItem();

			//~: primary key
			setPrimaryKey(session(), item, testit);

			//~: folder
			item.setFolder(folder);

			//~: item unity
			item.setItem(unity.getUnity());

			//~: transaction number
			TxPoint.txn(tx(), item);

			//!: do save the item
			session().save(item);


			//~: save cross-links
			TreeFolder f = folder;

			while(f != null)
			{
				TreeCross c = new TreeCross();

				//~: primary key
				setPrimaryKey(session(), c, testit);

				//~: folder
				c.setFolder(f);

				//~: item
				c.setItem(item);

				//!: save the cross
				session().save(c);

				//~: go to the upper folder
				f = f.getParent();
			}
		}

		protected void makeSingle()
		{
			TreeFolder      folder  = target(TreeFolder.class);

			//~: remove the cross items
			List<TreeCross> crosses = bean(GetTree.class).
			  getCrossItems(folder.getDomain(), unity.getPrimaryKey());

			for(TreeCross c : crosses)
				session().delete(c);

			//~: remove the items
			List<TreeItem>  items   = bean(GetTree.class).
			  getTreeItems(folder.getDomain(), unity.getPrimaryKey());

			for(TreeItem i : items)
				session().delete(i);
		}


		/* the unity to add to the folder */

		protected final United unity;
		protected boolean      single;
	}
}