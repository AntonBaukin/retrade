package com.tverts.retrade.exec.api.goods;

/* Java */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.exec.api.InsertHolder;


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

		//!: do save price list
		ActionsPoint.actionRun(ActionType.SAVE, d);

		//~: map the primary key to the context
		getKeysMap(h).put(s.getXkey(), d.getPrimaryKey());

		return d.getPrimaryKey();
	}


	/* protected: support */

	@SuppressWarnings("unchecked")
	protected Map<String, Long> getKeysMap(InsertHolder h)
	{
		Map<String, Long> m = (Map<String, Long>) h.getContext();
		if(m == null) h.setContext(m = new HashMap<String, Long>(17));
		return m;
	}
}