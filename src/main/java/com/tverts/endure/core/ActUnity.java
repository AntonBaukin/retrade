package com.tverts.endure.core;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.DeleteEntity;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: endure */

import com.tverts.actions.DelayedInstance;
import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.NumericIdentity;
import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Default builder of {@link Unity} instances.
 *
 * @author anton.baukin@gmail.com
 */
public class ActUnity extends ActionBuilderXRoot
{
	/* action types */

	/**
	 * Send task with this type to create new {@link Unity} instance.
	 *
	 * Note that the Unity is created for the target instance
	 * that is a {@link United}.
	 */
	public static final ActionType CREATE =
	  new ActionType("create", Unity.class);

	/**
	 * Deletes the {@link Unity} instance of the given
	 * as the action builder task target {@link United}
	 * instance. The reference to the {@link Unity} is
	 * set to {@code null}.
	 *
	 * Note that this action MAY have no meanings of how
	 * to chain removing the entities still referring
	 * the Unity is being deleted. You MAY need to cascade
	 * all the references present manually.
	 */
	public static final ActionType DELETE =
	  new ActionType("delete", Unity.class);


	/* parameters of the action task */

	/**
	 * Assign an instance of UnityType by this task parameter
	 * to create Unity instance with this type.
	 *
	 * NOTE that you have to define this parameter always
	 * when the type may not be found automatically. When:
	 *
	 *  · the type is not system or not registered
	 *    in {@link UnityTypes};
	 *
	 *  · there are several system types registered
	 *    for the same class instance, and it is not
	 *    possible to select a distinct one.
	 */
	public static final Class  UNITY_TYPE =
	  UnityType.class;

	/**
	 * If this Boolean flag set, and a new Unity instance
	 * is created, it would be saved.
	 *
	 * Note that even if the unity assigned is not saved
	 * in the action explicitly, it would be saved.
	 *
	 * By default the new Unity instance is not saved.
	 */
	public static final String FORCE_SAVE =
	  ActUnity.class.getName() + ": force save";

	/**
	 * This boolean flag tells not to assign the
	 * new instance to the United target.
	 *
	 * By default the action will set the unity
	 * only when the target has no unity assigned.
	 */
	public static final String NOT_ASSIGN =
	  ActUnity.class.getName() + ": not assign";


	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		if(CREATE.equals(actionType(abr)))
			createUnityAction(abr);

		if(DELETE.equals(actionType(abr)))
			deleteUnityAction(abr);
	}


	/* protected: action methods */

	protected void createUnityAction(ActionBuildRec abr)
	{
		//?: {the target is not a United | instance creator}
		checkTargetClass(abr, United.class, DelayedInstance.class);

		//~: add action to the chain
		chain(abr).first(new CreateUnityAction(task(abr)).
		  setPredicate(predicate(abr)));

		//!: complete the build
		complete(abr);
	}

	protected void deleteUnityAction(ActionBuildRec abr)
	{
		//?: {the target is not a united}
		checkTargetClass(abr, United.class);

		//~: add action to the chain
		chain(abr).first(new DeleteUnityAction(task(abr)).
		  setPredicate(predicate(abr)));

		//!: complete the build
		complete(abr);
	}


	/* set unity action */

	public static class CreateUnityAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public CreateUnityAction(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public Unity getResult()
		{
			return this.result;
		}


		/* protected: action base interface */

		protected void      execute()
		  throws Throwable
		{
			//~: automatically find the type
			this.unityType = obtainType();

			//?: {unity type could not be found}
			if(this.unityType == null)
				throw new IllegalStateException(String.format(
				  "Default Unity Builder could not found distinct system Unity " +
				  "Type for the United instance of class [%s]", LU.cls(xtarget())
				));

			//~: create the unity instance
			this.result = createUnity();

			//?: {do assign}
			if(isAssignUnity())
				xtarget(United.class).setUnity(this.result);

			//?: {do save unity}
			if(isSaveUnity())
				doSaveUnity();
		}

		protected Unity     createUnity()
		{
			Unity           res = new Unity();
			NumericIdentity uni = xtarget(NumericIdentity.class);

			//?: {target has no primary key yet} create it here
			if(uni.getPrimaryKey() == null)
				setPrimaryKey(session(), uni);

			//~: share the same primary key
			res.setPrimaryKey(uni.getPrimaryKey());

			//~: assign the unity type
			res.setUnityType(getUnityType());

			return res;
		}

		protected UnityType getUnityType()
		{
			return unityType;
		}

		protected UnityType obtainType()
		{
			//~: check for user defined type
			UnityType res = getUserDefinedType();

			//?: {the type is defined} return it
			if(res != null) return res;

			//~: search for the type in the system registry
			return searchUnityType();
		}

		protected UnityType getUserDefinedType()
		{
			return param(UNITY_TYPE, UnityType.class);
		}

		protected UnityType searchUnityType()
		{
			return UnityTypes.getInstance().
			  getDistinctType(xtarget().getClass());
		}

		protected boolean   isAssignUnity()
		{
			return !flag(NOT_ASSIGN);
		}

		protected boolean   isSaveUnity()
		{
			return flag(FORCE_SAVE);
		}

		protected void      doSaveUnity()
		{
			session().save(this.result);
		}

		/* private: resulting unity */

		private Unity     result;
		private UnityType unityType;
	}


	/* delete unity action */

	public static class DeleteUnityAction
	       extends      DeleteEntity
	{
		/* public: constructor */

		public DeleteUnityAction(ActionTask task)
		{
			super(task);

			if(!(target() instanceof United))
				throw new IllegalArgumentException();
		}


		/* public: DeleteEntity (access the parameters) */

		public Object getDeleteTarget()
		{
			return (this.unity != null)?(this.unity):
			  (target(United.class).getUnity());
		}

		public Unity  getResult()
		{
			return this.unity;
		}


		/* protected: DeleteEntity (execution) */

		protected void doDelete()
		{
			//~: get the unity to delete
			this.unity = target(United.class).getUnity();
			if(this.unity == null) return;

			//~: clear the reference
			target(United.class).setUnity(null);

			//!: do actual delete
			doDelete();
		}


		/* protected: the unity reference */

		protected Unity unity;
	}
}