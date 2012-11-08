package com.tverts.endure.auth;

/* standard Java classes */

import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;



/**
 * Actions builder on a {@link Computer} entities.
 *
 * @author anton.baukin@gmail.com
 */
public class ActComputer extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType UPDATE =
	  ActionType.UPDATE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveComputer(abr);

		if(UPDATE.equals(actionType(abr)))
			updateComputer(abr);
	}


	/* protected: action methods */

	protected void saveComputer(ActionBuildRec abr)
	{
		//?: {target is not a Computer}
		checkTargetClass(abr, Computer.class);

		//~: save the store
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set store unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, getUnityType());

		complete(abr);
	}

	protected void updateComputer(ActionBuildRec abr)
	{
		//?: {target is not a Computer}
		checkTargetClass(abr, Computer.class);

		//~: add update action
		chain(abr).first(new UpdateComputerAction(task(abr)));

		complete(abr);
	}


	/* protected: actions build support */

	protected UnityType getUnityType()
	{
		return UnityTypes.unityType(
		  Computer.class, Auth.TYPE_COMPUTER);
	}


	/* update action */

	public static class UpdateComputerAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public UpdateComputerAction(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public Computer getResult()
		{
			return target(Computer.class);
		}

		protected void  execute()
		  throws Throwable
		{
			//~: update the logins
			updateLogins();
		}


		/* protected: execution parts */

		protected void updateLogins()
		{
			List<AuthLogin> logins = bean(GetAuthLogin.class).
			  getLogins(target(Computer.class));

			for(AuthLogin login : logins)
				//~: clear the name, then update it on save
				login.setName(null);
		}
	}
}