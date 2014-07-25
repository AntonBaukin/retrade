package com.tverts.retrade.domain.core;

/* Java */

import java.util.Arrays;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core + trees) */

import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GenCoreDomain;
import com.tverts.endure.core.GetProps;
import com.tverts.endure.core.Property;
import com.tverts.endure.tree.TreeDomain;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: object (parameters) */

import com.tverts.objects.ObjectParam;
import com.tverts.objects.ObjectParams;


/**
 * Extends Domain Genesis with ReTrade
 * application specific issues.
 *
 * @author anton.baukin@gmail.com
 */
public class GenReTradeDomain extends GenCoreDomain
{
	/* Genesis */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: generate the domain
		super.generate(ctx);

		//~: ensure the properties
		ensureProperties(ctx);

		//?: {not a system domain}
		if(!system)
		{
			//~: ensure the goods tree
			ensureGoodsTreeDomain(ctx);
		}
	}

	public void parameters(List<ObjectParam> params)
	{
		//~: add this genesis parameters
		super.parameters(params);

		//~: add domain properties-parameters
		addOwnParameters(params,
		  Arrays.asList(ObjectParams.find(getProps()))
		);
	}


	/* Generate ReTrade Domain */

	public DomainProps getProps()
	{
		return props;
	}

	private DomainProps props = new DomainProps();

	public void setProps(DomainProps props)
	{
		if(props == null) throw new IllegalArgumentException();
		this.props = props;
	}

	public boolean isSystem()
	{
		return system;
	}

	private boolean system;

	public void setSystem(boolean system)
	{
		this.system = system;
	}

	/* protected: the generation */

	protected void ensureProperties(GenCtx ctx)
	{
		//~: find & extends the parameters
		ObjectParam[] params = ObjectParams.find(getProps());
		ObjectParams.extendProps(Arrays.asList(params));

		//~: load and set the properties
		GetProps get = bean(GetProps.class);

		for(ObjectParam param : params)
		{
			//~: get property extension as the source
			Property p = (Property)param.extensions().get(Property.class);
			if(p == null) continue;

			//~: domain
			p.setDomain(ctx.get(Domain.class));

			//~: get or create the database property
			p = get.goc(p);

			//~: update the property value
			get.set(p, param.getString());
		}
	}

	protected void ensureGoodsTreeDomain(GenCtx ctx)
	{
		//~: ensure goods tree
		TreeDomain tree = new TreeDomain();

		//~: domain
		tree.setDomain(ctx.get(Domain.class));

		//~: tree type
		tree.setTreeType(UnityTypes.unityType(
			 TreeDomain.class, Goods.TYPE_GOODS_TREE
		  )
		);

		//!: ensure
		actionRun(ActionType.ENSURE, tree);
	}
}