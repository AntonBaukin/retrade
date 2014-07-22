package com.tverts.retrade.exec.api.goods;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.api.support.EX;
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

		//=: ox-good
		gu.setOx(g);

		//?: {no measure}
		EX.assertn(g.getMeasure(),
		  "No Measure specified for Good [", gu.getPrimaryKey(), "]!"
		);

		//~: measure
		if(!gu.getMeasure().getPrimaryKey().equals(g.getMeasure()))
		{
			MeasureUnit mu = bean(GetGoods.class).
			  getMeasureUnit(g.getMeasure());

			//?: {Measure Unit not found}
			EX.assertn(mu, "Measure with key [", g.getMeasure(),
			  "] set for Good [", gu.getPrimaryKey(), "] doesn't exist!"
			);

			//sec: check the domain
			checkDomain(mu);

			gu.setMeasure(mu);
		}

		//!: update action
		gu.updateOx();
		ActionsPoint.actionRun(ActionType.UPDATE, gu);
	}
}