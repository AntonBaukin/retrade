package com.tverts.retrade.domain.auth;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure (core + auth + tree) */

import com.tverts.endure.UnityTypes;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.tree.TreeDomain;

/* com.tverts: retrade domain (firms + goods + selection sets) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.selset.ActSelSet;
import com.tverts.retrade.domain.selset.SelSet;

/* com.tverts: support */

import com.tverts.support.EX;


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

		//~: ensure the goods tree
		ensureClientFirmGoodsTree(abr);

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

	protected void ensureClientFirmGoodsTree(ActionBuildRec abr)
	{
		AuthLogin al = target(abr, AuthLogin.class);

		//?: {not a person login}
		if(al.getPerson() == null)
			return;

		//?: {person without a firm}
		if(al.getPerson().getFirm() == null)
			return;

		//~: find the contractor
		Contractor c = bean(GetContractor.class).
		  getContractorFirmStrict(al.getPerson().getFirm());

		//!: ensure the goods tree
		TreeDomain tree = new TreeDomain();

		//=: domain
		tree.setDomain(al.getDomain());

		//=: tree type
		tree.setTreeType(UnityTypes.unityType(
		  TreeDomain.class, Goods.TYPE_GOODS_TREE
		));

		//=: owner unity (contractor)
		tree.setOwner(EX.assertn(c.getUnity()));

		//!: ensure
		actionRun(ActionType.ENSURE, tree);
	}
}