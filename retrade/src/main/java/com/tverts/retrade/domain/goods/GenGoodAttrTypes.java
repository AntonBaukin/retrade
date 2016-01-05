package com.tverts.retrade.domain.goods;

/* com.tverts: actions */

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


/**
 * Generates predefined attribute
 * types for Good Units.
 *
 * @author anton.baukin@gmail.com.
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
	}


	/* public: JS generation callbacks */

	public void takeGoodType(GenCtx ctx, GoodAttr attr)
	{
		AttrType type = new AttrType();

		//=: domain
		type.setDomain(ctx.get(Domain.class));

		//=: type of attribute
		type.setAttrType(Goods.typeGoodAttr());

		//=: name
		type.setName(EX.asserts(attr.getName()));

		//=: local name
		type.setNameLo(attr.getNameLo());

		//=: is-system
		type.setSystem(attr.isSystem());

		//=: is-array
		type.setArray(attr.isArray());

		//=: is-shared
		type.setShared(attr.isShared());

		//?: {json prototype }
		EX.asserts(attr.getObject());

		//=: ox-object
		type.setOx(attr);

		//!: ensure the type
		actionRun(ActionType.ENSURE, type);

		LU.I(log(ctx), logsig(),
		  (type.getPrimaryKey() == null)?(" found"):(" created"),
		  " good attribute type: ", type.getName()
		);
	}
}