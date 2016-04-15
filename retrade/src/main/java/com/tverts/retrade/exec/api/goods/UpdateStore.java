package com.tverts.retrade.exec.api.goods;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: retrade domain (stores) */

import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Store;


/**
 * Updates {@link TradeStore} with
 * API {@link Store} instance.
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateStore extends UpdateEntityBase
{
	/* protected: UpdateEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Store);
	}

	protected Class   getUnityClass(Holder holder)
	{
		return TradeStore.class;
	}

	protected void    update(Object entity, Object source)
	{
		TradeStore ts = (TradeStore) entity;
		Store      s  = (Store) source;

		//~: code
		ts.setCode(s.getCode());

		//~: name
		ts.setName(s.getName());


		//!: update action
		ActionsPoint.actionRun(ActionType.UPDATE, ts);
	}
}