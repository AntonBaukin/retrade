package com.tverts.retrade.exec.api.goods;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.MeasureUnit;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Measure;


/**
 * Saves new {@link MeasureUnit} from the given
 * API {@link Measure}.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertMeasure extends InsertEntityBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Measure);
	}

	protected Long    insert(Object source)
	{
		Measure     m  = (Measure) source;
		MeasureUnit mu = new MeasureUnit();

		//=: domain
		mu.setDomain(domain());

		//=: ox-measure
		mu.setOx(m);

		//!: do save
		mu.updateOx();
		ActionsPoint.actionRun(ActionType.SAVE, mu);

		return mu.getPrimaryKey();
	}
}