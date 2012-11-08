package com.tverts.endure.auth;

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

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Builder for actions related to the
 * authentication and the logins.
 *
 * @author anton.baukin@gmail.com
 */
public class ActLogin extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType CLOSE  =
	  new ActionType(AuthLogin.class, "close");


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveLogin(abr);

		if(CLOSE.equals(actionType(abr)))
			closeLogin(abr);
	}


	/* protected: action methods */

	protected void saveLogin(ActionBuildRec abr)
	{
		//?: {target is not a login}
		checkTargetClass(abr, AuthLogin.class);

		//~: save the store
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set store unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, getUnityType());

		complete(abr);
	}

	protected void closeLogin(ActionBuildRec abr)
	{
		//?: {target is not a login}
		checkTargetClass(abr, AuthLogin.class);

		AuthLogin login = target(abr, AuthLogin.class);

		//?: {the login is already closed}
		if(login.getCloseTime() != null)
			throw new IllegalStateException(String.format(
			  "Authentication Login [%d] '%s' is already closed!",
			  login.getPrimaryKey(), login.getCode()
			));



		complete(abr);
	}


	/* protected: actions build support */

	protected UnityType getUnityType()
	{
		return UnityTypes.unityType(
		  AuthLogin.class, Auth.TYPE_LOGIN);
	}


	/* login close action */

	public static class CloseAuthLoginAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public CloseAuthLoginAction(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public AuthLogin getResult()
		{
			return target(AuthLogin.class);
		}

		protected void   execute()
		  throws Throwable
		{
			AuthLogin login = target(AuthLogin.class);

			//~: set close time
			login.setCloseTime(new java.util.Date());

			//~: update the login code
			login.setCode(String.format(
			  "%s (closed at %s)", login.getCode(),
			  DU.datetime2str(login.getCloseTime())
			));
		}
	}

}