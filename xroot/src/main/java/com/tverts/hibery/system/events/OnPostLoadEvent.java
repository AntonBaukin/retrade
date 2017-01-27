package com.tverts.hibery.system.events;

/* Hibernate Persistence Layer */

import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;

/* com.tverts: system (tx) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;


/**
 * Translates Post Load Event to the present Transaction Context.
 *
 * @author anton.baukin@gmail.com
 */
public class OnPostLoadEvent implements PostLoadEventListener
{
	public void onPostLoad(PostLoadEvent event)
	{
		Tx           tx = TxPoint.INSTANCE.getTxContext();
		OnHiberEvent on = (tx == null)?(null):(tx.get(OnHiberEvent.class));

		if(on != null)
			on.onHiberEvent(event);
	}
}
