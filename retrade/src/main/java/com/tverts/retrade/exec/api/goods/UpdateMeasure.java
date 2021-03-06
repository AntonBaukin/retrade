package com.tverts.retrade.exec.api.goods;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.MeasureUnit;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Measure;


/**
 * Updates {@link MeasureUnit} with
 * API {@link Measure} instance.
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateMeasure extends UpdateEntityBase
{
	/* protected: UpdateEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Measure);
	}

	protected Class   getUnityClass(Holder holder)
	{
		return MeasureUnit.class;
	}

	protected void    update(Object entity, Object source)
	{
		MeasureUnit mu = (MeasureUnit) entity;
		Measure     m  = (Measure) source;

		//=: ox-measure
		mu.setOx(m);

		//!: update action
		mu.updateOx();
		ActionsPoint.actionRun(ActionType.UPDATE, mu);
	}
}