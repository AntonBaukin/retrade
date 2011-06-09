package com.tverts.endure.core;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionBuilderWithTxBase;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * Default builder of {@link Unity} instances.
 *
 * @author anton.baukin@gmail.com
 */
public class ActUnity extends ActionBuilderWithTxBase
{
	/* action types */

	/**
	 * Send task with this type to create new {@link Unity} instance.
	 *
	 * Note that the Unity is created for the target instance
	 * that is a {@link United}.
	 */
	public static final ActionType CREATE =
	  new ActionType("endure.core.Unity: create", Unity.class);


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
	}


	/* protected: action methods */

	protected void createUnityAction(ActionBuildRec abr)
	{
		//?: {the target is not a united}
		checkTargetClass(abr, United.class);

		//~: add action to the chain
		chain(abr).first(new CreateUnityAction(task(abr)));

		//!: complete the build
		complete(abr);
	}

	/* public: set unity action */

	public static class CreateUnityAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public CreateUnityAction(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public void  open()
		{
			super.open();

			//~: automatically find the
			this.unityType = obtainType();

			//?: {unity type could not be found}
			if(this.unityType == null)
				throw new IllegalStateException(String.format(
				  "Default Unity Builder could not found distinct system Unity " +
				  "Type for the United instance of class [%s]", OU.cls(target())
				));
		}

		public Unity getResult()
		{
			return this.result;
		}


		/* protected: action base interface */

		protected void      execute()
		  throws Throwable
		{
			//~: create the unity instance
			this.result = createUnity();

			//?: {do assign}
			if(isAssignUnity())
				target(United.class).setUnity(getResult());

			//?: {do save unity}
			if(isSaveUnity())
				doSaveUnity();
		}

		protected Unity     createUnity()
		{
			Unity res = new Unity();

			//~: take the primaru key from the target
			res.setPrimaryKey(target(United.class).getPrimaryKey());

			//?: {target has no primary key yet} generate it
			if(res.getPrimaryKey() == null)
				setPrimaryKey(session(), res);

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
			  getDistinctType(target().getClass());
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
			session().save(getResult());
		}

		/* private: resulting unity */

		private Unity     result;
		private UnityType unityType;
	}
}