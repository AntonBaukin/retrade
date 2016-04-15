package com.tverts.endure.auth;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Builder for actions related to the
 * authentication and the logins.
 *
 * @author anton.baukin@gmail.com
 */
public class ActAuthSession extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType CLOSE  =
	  new ActionType(AuthSession.class, "close");


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(CLOSE.equals(actionType(abr)))
			closeSession(abr);
	}


	/* protected: action methods */

	protected void closeSession(ActionBuildRec abr)
	{
		//?: {target is not a session}
		checkTargetClass(abr, AuthSession.class);

		AuthSession session = target(abr, AuthSession.class);

		//?: {the login is already closed}
		if(session.getCloseTime() != null)
			throw new IllegalStateException(String.format(
			  "Authentication Session [%s] is already closed at [%s]!",
			  session.getSessionId(), DU.datetime2str(session.getCloseTime())
			));

		//~: close the session
		chain(abr).first(new CloseAuthSessionAction(task(abr)));

		complete(abr);
	}


	/* protected: actions build support */

	protected UnityType getUnityType()
	{
		return UnityTypes.unityType(
		  AuthLogin.class, Auth.TYPE_LOGIN);
	}


	/* login close action */

	public static class CloseAuthSessionAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public CloseAuthSessionAction(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public AuthSession getResult()
		{
			return target(AuthSession.class);
		}

		protected void     execute()
		  throws Throwable
		{
			AuthSession session = target(AuthSession.class);

			//~: set close time
			session.setCloseTime(new java.util.Date());
		}
	}
}