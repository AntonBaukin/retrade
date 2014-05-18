package com.tverts.retrade.domain.auth;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: endure (auth) */

import com.tverts.endure.auth.AuthLogin;

/* com.tverts: retrade domain (selection sets) */

import com.tverts.retrade.domain.selset.ActSelSet;
import com.tverts.retrade.domain.selset.SelSet;


/**
 * Extends default Auth Logins actions
 * builder with tasks specific for ReTrade.
 *
 * @author anton.baukin@gmail.com
 */
public class ActLogin extends com.tverts.endure.auth.ActLogin
{
	/* protected: action methods */

	protected void saveLogin(ActionBuildRec abr)
	{
		//?: {target is not a login}
		checkTargetClass(abr, AuthLogin.class);

		//~: save default selection set
		saveDefaultSelSet(abr);

		//~: save the login
		super.saveLogin(abr);
	}

	protected void saveDefaultSelSet(ActionBuildRec abr)
	{
		SelSet sel = new SelSet();

		//~: target login
		sel.setLogin(target(abr, AuthLogin.class));

		//~: default set name
		sel.setName("");

		//!: ensure the selection set
		xnest(abr, ActSelSet.ENSURE, sel);
	}
}