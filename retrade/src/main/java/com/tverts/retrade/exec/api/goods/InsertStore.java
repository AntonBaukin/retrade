package com.tverts.retrade.exec.api.goods;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: retrade domain (stores) */

import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Store;


/**
 * Saves new {@link TradeStore} from the given
 * API {@link Store}.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertStore extends InsertEntityBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Store);
	}

	protected Long    insert(Object source)
	{
		Store      s  = (Store) source;
		TradeStore ts = new TradeStore();

		//~: domain
		ts.setDomain(domain());

		//~: code
		ts.setCode(s.getCode());

		//~: name
		ts.setName(s.getName());


		//!: do save
		ActionsPoint.actionRun(ActionType.SAVE, ts);

		return ts.getPrimaryKey();
	}
}