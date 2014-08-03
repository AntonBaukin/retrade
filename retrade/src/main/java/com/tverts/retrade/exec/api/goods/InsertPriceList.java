package com.tverts.retrade.exec.api.goods;

/* Java */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: retrade api */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.prices.PriceList;

/* com.tverts: api execution */

import com.tverts.exec.api.InsertEntityBase;
import com.tverts.exec.api.InsertHolder;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.PriceListEntity;


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
		  PriceList
		);
	}

	protected Long    insert(InsertHolder h)
	{
		PriceList       s = (PriceList) h.getHolder().getEntity();;
		PriceListEntity d = new PriceListEntity();

		//=: domain
		d.setDomain(domain());

		//=: ox-entity
		d.setOx(s);

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