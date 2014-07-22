package com.tverts.retrade.exec.api.goods;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.MeasureUnit;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Good;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Saves new {@link GoodUnit} from the given
 * API {@link Good}.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertGood extends InsertEntityBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return Good.class.equals(holder.getEntity().getClass());
	}

	protected Long    insert(Object source)
	{
		return insert((Good) source).getPrimaryKey();
	}


	/* protected: support */

	protected GoodUnit    insert(Good g)
	{
		GoodUnit gu = createGood(g);

		//!: do save
		ActionsPoint.actionRun(ActionType.SAVE, gu);

		return gu;
	}

	protected GoodUnit    createGood(Good g)
	{
		GoodUnit gu = new GoodUnit();

		//=: domain
		gu.setDomain(domain());

		//=: ox-good
		gu.setOx(g);

		//~: measure unit
		gu.setMeasure(loadMeasure(g));

		return gu;
	}

	protected MeasureUnit loadMeasure(Good g)
	{
		//?: {has no measure}
		EX.assertn(g.getMeasure(), "Good with xkey [",
		  g.getXkey(), "] has no Measure reference!");

		MeasureUnit mu = bean(GetGoods.class).
		  getMeasureUnit(g.getMeasure());

		//?: {not exists}
		EX.assertn(mu, "Good with xkey [", g.getXkey(),
		  "] refers Measure xkey [", g.getXMeasure(),
		  "] pkey [", g.getMeasure(), "] that doesn't exist!"
		);

		//sec: check the domain
		checkDomain(mu);

		return mu;
	}
}