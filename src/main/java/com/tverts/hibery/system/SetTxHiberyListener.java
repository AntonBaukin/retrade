package com.tverts.hibery.system;

/* Hibernate Persistence Layer */

import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;

/* com.tverts: system (tx) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;


/**
 * Listener that installs Transaction number
 * of current {@link Tx} into the saved or
 * updated instances.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SetTxHiberyListener
       implements SaveOrUpdateEventListener
{
	/* public: SaveOrUpdateEventListener interface */

	public void onSaveOrUpdate(SaveOrUpdateEvent event)
	{
		TxPoint.txn(event.getEntity());
	}
}