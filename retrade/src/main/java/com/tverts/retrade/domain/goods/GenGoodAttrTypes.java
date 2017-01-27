package com.tverts.retrade.domain.goods;

/* com.tverts: actions */

import java.util.ArrayList;
import java.util.List;

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: scripting */

import com.tverts.jsx.JsX;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: endure (core) */

import com.tverts.endure.core.AttrType;
import com.tverts.endure.core.Domain;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.GoodAttr;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Generates predefined attribute
 * types for Good Units.
 *
 * @author anton.baukin@gmail.com
 */
public class GenGoodAttrTypes extends GenesisHiberPartBase
{
	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: invoke GenGoodAttrTypes.js
		JsX.apply("domain/goods/GenGoodAttrTypes.js",
		  "genGoodTypes", ctx, this
		);

		//~: log the results
		logResults(ctx);
	}


	/* public: JS generation callbacks */

	public void takeGoodType(GenCtx ctx, AttrType type)
	{
		//?: {name}
		EX.asserts(type.getName());

		//?: {attribute type}
		EX.assertn(type.getAttrType());

		//?: {ox-object}
		EX.assertx(type.getOx() instanceof GoodAttr);

		//=: domain
		type.setDomain(ctx.get(Domain.class));

		//!: ensure the type
		actionRun(ActionType.ENSURE, type);

		//~: save name to the logs
		if(type.getPrimaryKey() == null)
			logFound.add(type.getName());
		else
			logCreated.add(type.getName());
	}


	/* protected: support */

	protected void logResults(GenCtx ctx)
	{
		logFound.sort(String::compareTo);
		logCreated.sort(String::compareTo);

		LU.I(log(ctx), logsig(), " found: ",
		  logFound.isEmpty()?("none"):SU.scats(", ", logFound)
		);

		LU.I(log(ctx), logsig(), " created: ",
		  logCreated.isEmpty()?("none"):SU.scats(", ", logCreated)
		);

		logFound.clear();
		logCreated.clear();
	}

	protected List<String> logFound = new ArrayList<>();
	protected List<String> logCreated = new ArrayList<>();
}