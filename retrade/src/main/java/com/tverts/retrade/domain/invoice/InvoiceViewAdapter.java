package com.tverts.retrade.domain.invoice;

/* com.tverts: objects */

import com.tverts.objects.Adapter;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Adapts Invoice as extended Invoice View.
 *
 * @author anton.baukin@gmail.com
 */
public class InvoiceViewAdapter implements Adapter
{
	/* public: Adaptor interface */

	public Object adapt(Object o)
	{
		if(!(o instanceof Invoice))
			throw EX.arg("Can't adapt not an Invoice!");

		return new InvoiceView().init(o);
	}
}