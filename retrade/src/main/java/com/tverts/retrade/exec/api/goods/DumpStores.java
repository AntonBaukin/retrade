package com.tverts.retrade.exec.api.goods;

/* com.tverts: retrade domain (store) */

import com.tverts.retrade.domain.store.Stores;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Store;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps {@link TradeStore} as {@link Store}.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpStores extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		TradeStore ts = (TradeStore)src;
		Store      s  = new Store();

		s.setPkey(ts.getPrimaryKey());
		s.setTx(ts.getTxn());
		s.setCode(ts.getCode());
		s.setName(ts.getName());

		return s;
	}

	protected Class  getUnityClass()
	{
		return TradeStore.class;
	}

	protected Class  getEntityClass()
	{
		return Store.class;
	}

	public String    getUnityType()
	{
		return Stores.TYPE_TRADE_STORE;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}
}