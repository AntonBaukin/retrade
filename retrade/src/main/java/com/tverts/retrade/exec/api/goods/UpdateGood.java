package com.tverts.retrade.exec.api.goods;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.MeasureUnit;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Good;


/**
 * Updates {@link GoodUnit} with
 * API {@link Good} instance.
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateGood extends UpdateEntityBase
{
	/* protected: UpdateEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Good);
	}

	protected Class   getUnityClass(Holder holder)
	{
		return GoodUnit.class;
	}

	protected void    update(Object entity, Object source)
	{
		GoodUnit gu = (GoodUnit) entity;
		Good     g  = (Good) source;

		//~: code
		gu.setCode(g.getCode());

		//~: name
		gu.setName(g.getName());

		//?: {no measure}
		if(g.getMeasure() == null)
			throw new IllegalArgumentException(String.format(
			  "No Measure specified for Good [%d]!", gu.getPrimaryKey()
			));


		//~: measure
		if(!gu.getMeasure().getPrimaryKey().equals(g.getMeasure()))
		{
			MeasureUnit mu = bean(GetGoods.class).
			  getMeasureUnit(g.getMeasure());

			//?: {Measure Unit not found}
			if(mu == null) throw new IllegalArgumentException(String.format(
			  "Measure with key [%d] set for Good [%d] doesn't exist!",
			  g.getMeasure(), gu.getPrimaryKey()
			));

			//sec: check the domain
			checkDomain(mu);

			gu.setMeasure(mu);
		}

		//!: update action
		ActionsPoint.actionRun(ActionType.UPDATE, gu);
	}
}