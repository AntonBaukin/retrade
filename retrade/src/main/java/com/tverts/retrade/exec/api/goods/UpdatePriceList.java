package com.tverts.retrade.exec.api.goods;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.goods.PriceList;
import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.prices.PriceListEntity;


/**
 * Updates Price List entity from the API object given.
 *
 * @author anton.baukin@gmail.com
 */
public class UpdatePriceList extends UpdateEntityBase
{
	/* protected: InsertEntityBase interface */

	protected boolean   isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof
		  com.tverts.api.retrade.goods.PriceList
		);
	}

	protected Class     getUnityClass(Holder holder)
	{
		return PriceListEntity.class;
	}

	protected void      update(Object entity, Object source)
	{
		PriceList       s = (PriceList) source;
		PriceListEntity d = (PriceListEntity) entity;

		//~: update ox-entity
		d.setOx(s);

		//!: do update price list
		ActionsPoint.actionRun(ActionType.UPDATE, d);
	}
}