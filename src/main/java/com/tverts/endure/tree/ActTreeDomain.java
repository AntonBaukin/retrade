package com.tverts.endure.tree;

/* standard Java classes */

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.logic.Predicate;


/**
 * Actions builder on {@link TreeDomain} entities.
 *
 * @author anton.baukin@gmail.com
 */
public class ActTreeDomain extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType ENSURE =
	  ActionType.ENSURE;

	/**
	 * Updates the Folders tree (not the Items!)
	 * using {@link #PARAM_FOLDERS} parameter.
	 *
	 * This action must has parameter
	 * {@link ActTreeFolder#PARAM_TYPE}
	 * with type of new folders.
	 */
	public static final ActionType UPDATE_FOLDERS =
	  new ActionType(TreeDomain.class, "update-folders");


	/* parameters */

	/**
	 * Parameter of Tree Domain Type.
	 */
	public static final String PARAM_TYPE    =
	  ActTreeDomain.class.getName() + ": type";

	/**
	 * Collection with {@link TreeNodeView} objects
	 * to update the Folders Tree. Note that only
	 * the Folders are supported!
	 */
	public static final String PARAM_FOLDERS =
	  ActTreeFolder.class.getName() + ": folders";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveTreeDomain(abr, false);

		if(ENSURE.equals(actionType(abr)))
			saveTreeDomain(abr, true);

		if(UPDATE_FOLDERS.equals(actionType(abr)))
			updateTreeFolders(abr);
	}


	/* protected: action methods */

	protected void saveTreeDomain(ActionBuildRec abr, boolean ensure)
	{
		//?: {target is not a Tree Domain}
		checkTargetClass(abr, TreeDomain.class);

		//~: domain missing predicate
		Predicate p = !ensure?(null):(new TreeDomainMissing(getUnityType(abr)));

		//~: save the domain
		chain(abr).first(new SaveNumericIdentified(task(abr)).setPredicate(p));

		//~: set tree domain unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  PREDICATE, p, ActUnity.UNITY_TYPE, getUnityType(abr)
		);

		complete(abr);
	}

	@SuppressWarnings("unchecked")
	protected void updateTreeFolders(ActionBuildRec abr)
	{
		//?: {target is not a Tree Domain}
		checkTargetClass(abr, TreeDomain.class);

		//~: get the folders
		Collection folders = param(abr, PARAM_FOLDERS, Collection.class);
		if((folders == null) || folders.isEmpty())
			throw EX.arg("No (or empty) Folders collection (PARAM_FOLDERS) given!");

		//?: {has no folder type parameter}
		if(param(abr, ActTreeFolder.PARAM_TYPE) == null)
			throw EX.arg("No Folders Type parameter!");

		//~: update the domain
		chain(abr).first(new UpdateTreeFoldersAction(task(abr)).
		  setNodes((Collection<TreeNodeView>) folders).
		  setFolderType(param(abr, ActTreeFolder.PARAM_TYPE))
		);

		complete(abr);
	}


	/* protected: actions build support */

	protected UnityType getUnityType(ActionBuildRec abr)
	{
		TreeDomain tree = target(abr, TreeDomain.class);
		if(tree.getTreeType() != null)
			return tree.getTreeType();

		Object     type = param(abr, PARAM_TYPE);

		if(type instanceof UnityType)
			return (UnityType) type;

		if(type instanceof String)
			return UnityTypes.unityType(TreeDomain.class, (String) type);

		throw EX.state(getClass().getSimpleName(),
		  " has no Tree Domain Type parameter!");
	}


	/* existing predicate */

	protected class TreeDomainMissing implements Predicate
	{
		public TreeDomainMissing(UnityType treeType)
		{
			this.treeType = treeType;
		}

		public boolean evalPredicate(Object ctx)
		{
			TreeDomain td = (TreeDomain) ((Action) ctx).getTask().getTarget();

			return (bean(GetTree.class).
			  getDomain(td.getDomain().getPrimaryKey(), treeType) == null);
		}

		protected final UnityType treeType;
	}


	/* existing predicate */

	protected static class UpdateTreeFoldersAction
	          extends      ActionWithTxBase
	{
		/* constrictor */

		public UpdateTreeFoldersAction(ActionTask task)
		{
			super(task);
		}


		/* public: bean interface */

		public UpdateTreeFoldersAction setNodes(Collection<TreeNodeView> nodes)
		{
			this.nodes = nodes;
			return this;
		}

		public UpdateTreeFoldersAction setFolderType(Object folderType)
		{
			this.folderType = folderType;
			return this;
		}


		/* public: action interface */

		public Object getResult()
		{
			return target();
		}


		/* protected: ActionBase interface */

		@SuppressWarnings("unchecked")
		protected void   execute()
		  throws Throwable
		{
			TreeDomain domain = target(TreeDomain.class);

			//~: load all the folders of tree domain
			List<TreeFolder> flist = bean(GetTree.class).
			  selectFolders(domain);

			//~: map them by the keys (as strings)
			HashMap<String, TreeFolder> fkeys =
			  new HashMap<String, TreeFolder>(flist.size());
			for(TreeFolder f : flist)
				fkeys.put(f.getPrimaryKey().toString(), f);

			//~: map them by the codes
			HashMap<String, TreeFolder> fcodes =
			  new HashMap<String, TreeFolder>(flist.size());
			for(TreeFolder f : flist)
				fcodes.put(f.getCode(), f);

			//~: flush session updates
			session().flush();

			//0: set temporary codes for all the updated folders (with codes changed)
			for(TreeNodeView n : nodes)
			{
				//~: skip new folders
				if(n.getObjectKey().startsWith("$"))
					continue;

				TreeFolder f = fkeys.get(n.getObjectKey());
				if(f == null) throw EX.state(
				  "Tree Folder [", n.getObjectKey(), "] not found!");

				//?: {code is changed}
				if(!f.getCode().equals(n.getCode()))
				{
					fcodes.remove(f.getCode());
					f.setCode(genTempCode());
				}
			}

			//~: flush temp codes to db
			session().flush();

			//1: reset codes back
			for(TreeNodeView n : nodes)
			{
				//~: skip new folders
				if(n.getObjectKey().startsWith("$"))
					continue;

				TreeFolder f = fkeys.get(n.getObjectKey());
				if(f == null) throw EX.state(
				  "Tree Folder [", n.getObjectKey(), "] not found!");

				//?: {the code is not changed} do nothing
				if(f == fcodes.get(n.getCode()))
					continue;

				//?: {this code was written}
				if(fcodes.containsKey(n.getCode()))
					throw EX.arg("Duplicated Folder code [", n.getCode(), "]!");

				f.setCode(n.getCode());
				fcodes.put(n.getCode(), f);
			}

			//~: flush new codes to db (without parents, for now)
			session().flush();

			//2: insert new folders
			for(TreeNodeView n : nodes)
			{
				//~: skip existing folders
				if(!n.getObjectKey().startsWith("$"))
					continue;

				//?: {the code already exists}
				if(fcodes.containsKey(n.getCode()))
					throw EX.arg("Duplicated Folder code [", n.getCode(), "]!");

				//~: create new Folder
				TreeFolder f = new TreeFolder();

				//~: primary key
				setPrimaryKey(session(), f, isTestInstance(domain));

				//~: tree domain
				f.setDomain(domain);

				//~: code
				f.setCode(n.getCode());

				//~: name
				f.setName(n.getName());

				//!: save it
				actionRun(ActTreeFolder.SAVE, f,
				  ActTreeFolder.PARAM_TYPE, folderType
				);

				fkeys.put(f.getPrimaryKey().toString(), f);
				fkeys.put(n.getObjectKey(), f); //<-- also it's $-key
				fcodes.put(f.getCode(), f);
				flist.add(f);
			}

			//~: flush new folders to db
			session().flush();

/*

 delete from TreeCross tc0 where tc0.item.id in (
   select tc1.item.id from TreeCross tc1 where
     (tc1.folder.id = :folder))

*/
			Query query = session().createQuery(

"delete from TreeCross tc0 where tc0.item.id in (\n" +
"  select tc1.item.id from TreeCross tc1 where\n" +
"    (tc1.folder.id = :folder))"

			);


			//3: process the parents
			for(TreeNodeView n : nodes)
			{
				TreeFolder f = fkeys.get(n.getObjectKey());
				boolean    e;

				if(n.getParentKey() == null)
				{
					e = (f.getParent() != null);
					f.setParent(null);
				}
				else
				{
					TreeFolder p = f.getParent();

					//~: lookup by the key
					f.setParent(fkeys.get(n.getParentKey()));

					//?: {it not found}
					if(f.getParent() == null) throw EX.arg(
					  "Parent Folder [", n.getParentKey(), "] not found!");

					e = (p == null) || !p.equals(f.getParent());
				}

				//~: execute query to delete crosses coming through this folder
				if(e && !n.getObjectKey().startsWith("$"))
					query.setLong("folder", f.getPrimaryKey()).executeUpdate();
			}

			//4: inspect cyclic dependencies
			for(TreeFolder f : flist)
			{
				HashSet<Long> ancs = new HashSet<Long>(5);

				while(f != null)
				{
					if(ancs.contains(f.getPrimaryKey())) throw EX.state(
					  "Cyclic dependency in Tree Folder [", f.getPrimaryKey(), "]!");

					ancs.add(f.getPrimaryKey());
					f = f.getParent();
				}
			}

			//5: update the names
			for(TreeNodeView n : nodes)
				fkeys.get(n.getObjectKey()).setName(n.getName());

			//~: flush the last updates
			session().flush();

			//<6: create cross-references (for items without them)

/*

 select ti from TreeCross tc right outer join tc.item ti
   where (ti.folder.domain = :domain)
 group by ti having (count(tc) = 0)

*/
			List<TreeItem> items = (List<TreeItem>) session().createQuery(

"select ti from TreeCross tc right outer join tc.item ti\n" +
"  where (ti.folder.domain = :domain)\n" +
"group by ti having (count(tc) = 0)"

			).
			  setParameter("domain", domain).
			  list();

			for(TreeItem i : items)
			{
				TreeFolder f = i.getFolder();

				while(f != null)
				{
					TreeCross c = new TreeCross();

					//~: primary key
					setPrimaryKey(session(), c, isTestInstance(domain));

					//~: item
					c.setItem(i);

					//~: folder
					c.setFolder(f);

					//!: save it
					session().save(c);

					f = f.getParent(); //<-- next parent
				}
			}

			//>6: create cross-references
		}

		private Random tempCodeGen;

		protected String genTempCode()
		  throws Exception
		{
			if(tempCodeGen == null)
				tempCodeGen = new SecureRandom();

			byte[] code = new byte[127];
			tempCodeGen.nextBytes(code);

			return new String(SU.bytes2hex(code));
		}


		/* the views parameter */

		private Collection<TreeNodeView> nodes;
		private Object folderType;
	}
}