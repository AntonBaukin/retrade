package com.tverts.endure.tree;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.logic.Predicate;


/**
 * Actions builder on a {@link TreeDomain} entities.
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


	/* parameters */

	/**
	 * Parameter of Tree Domain Type.
	 */
	public static final String PARAM_TYPE =
	  ActTreeDomain.class.getName() + ": type";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveTreeDomain(abr, false);

		if(ENSURE.equals(actionType(abr)))
			saveTreeDomain(abr, true);
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


	/* protected static: existing predicate */

	protected class TreeDomainMissing implements Predicate
	{
		public TreeDomainMissing(UnityType treeType)
		{
			this.treeType = treeType;
		}

		public boolean evalPredicate(Object ctx)
		{
			TreeDomain tf = (TreeDomain) ((Action) ctx).getTask().getTarget();

			return (bean(GetTree.class).
			  getDomain(tf.getDomain().getPrimaryKey(), treeType) == null);
		}

		protected final UnityType treeType;
	}
}