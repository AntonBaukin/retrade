package com.tverts.retrade.exec.api.goods;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.exec.api.InsertHolder;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.prices.PriceList;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Saves new PriceList from the corresponding API entity.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertPriceList extends InsertEntityBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof
		  com.tverts.api.retrade.goods.PriceList
		);
	}

	protected Long    insert(InsertHolder h)
	{
		com.tverts.api.retrade.goods.PriceList     s  =
		  (com.tverts.api.retrade.goods.PriceList) h.getHolder().getEntity();;

		com.tverts.retrade.domain.prices.PriceList d =
		  new com.tverts.retrade.domain.prices.PriceList();

		//~: domain
		d.setDomain(domain());

		//~: code
		d.setCode(s.getCode());

		//~: name
		d.setName(s.getName());

		//~: parent price list
		if(s.getXParent() != null)
			d.setParent(loadParentList(h));


		//!: do save price list
		ActionsPoint.actionRun(ActionType.SAVE, d);

		//~: map the primary key to the context
		getKeysMap(h).put(s.getXkey(), d.getPrimaryKey());

		return d.getPrimaryKey();
	}


	/* protected: support */

	protected PriceList loadParentList(InsertHolder h)
	{
		//~: obtain key of the parent folder
		com.tverts.api.retrade.goods.PriceList
		     s  = (com.tverts.api.retrade.goods.PriceList) h.getHolder().getEntity();;
		Long k = s.getParent();

		//?: {has no parent key} lookup from the context of just inserted
		if(k == null) k = EX.assertn(
		  getKeysMap(h).get(s.getXParent()),
		  "Price List [", s.getXkey(), "] refers parent List [",
		  s.getXParent(), "] that is not inserted yet!"
		);

		PriceList pl = EX.assertn(bean(GetGoods.class).getPriceList(k),
		  "Parent Price List with p-key [", k, "] and x-key [",
		  s.getXParent(), "] of Price List x-key [", s.getXkey(), "] not found!"
		);

		//sec: check the domain
		checkDomain(pl);

		return pl;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Long> getKeysMap(InsertHolder h)
	{
		Map<String, Long> m = (Map<String, Long>) h.getContext();
		if(m == null) h.setContext(m = new HashMap<String, Long>(17));
		return m;
	}
}