package com.tverts.retrade.exec.api.goods;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.prices.PriceList;

/* com.tverts: support */

import com.tverts.support.EX;


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

		//~: parent price list
		if(s.getParent() == null)
			d.setParent(null);
		else
			d.setParent(loadParentList(s));


		//!: do update price list
		ActionsPoint.actionRun(ActionType.UPDATE, d);
	}


	/* protected: support */

	protected PriceList loadParentList(com.tverts.api.retrade.goods.PriceList s)
	{
		PriceList pl = EX.assertn(bean(GetGoods.class).getPriceList(s.getParent()),
		  "Parent Price List with p-key [", s.getParent(), "] and x-key [",
		  s.getXParent(), "] of Price List x-key [", s.getXkey(), "] not found!"
		);

		//sec: check the domain
		checkDomain(pl);

		return pl;
	}
}