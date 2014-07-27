package com.tverts.retrade.exec.api.goods;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.prices.PriceList;


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
		return PriceList.class;
	}

	protected void      update(Object entity, Object source)
	{
		com.tverts.api.retrade.goods.PriceList     s  =
		  (com.tverts.api.retrade.goods.PriceList) source;

		com.tverts.retrade.domain.prices.PriceList d =
		  (com.tverts.retrade.domain.prices.PriceList) entity;

		//~: code
		d.setCode(s.getCode());

		//~: name
		d.setName(s.getName());


		//!: do update price list
		ActionsPoint.actionRun(ActionType.UPDATE, d);
	}
}