package com.tverts.retrade.domain.invoice.gen;

/* standard Java classes */

import java.security.MessageDigest;
import java.util.List;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenTest;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Loads all the Invoices of Buy-Sell Order and
 * calculates SHA1 digest on their primary keys.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestInvoices implements GenTest
{
	/* public: GenTest interface */

	public void    testGenesis(GenCtx ctx)
	{
		//~: log order of buy-sell invoices
		try
		{
			logBuySellOrder(ctx);
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}
	}


	/* protected: test parts */

	@SuppressWarnings("unchecked")
	protected void logBuySellOrder(GenCtx ctx)
	  throws Exception
	{

/*

 select i.id from Invoice i where
   (i.domain = :domain) and (i.orderType = :otype)
 order by i.orderIndex

 */
		List invoices = ctx.session().createQuery(

		  "select i.id from Invoice i where\n" +
		  "  (i.domain = :domain) and (i.orderType = :otype)\n" +
		  "order by i.orderIndex"

		).
		  setParameter("domain", ctx.get(Domain.class)).
		  setParameter("otype",  Invoices.typeInvoiceBuySellOrder()).
		  list();

		if(invoices.isEmpty())
		{
			LU.W(ctx.log(), "Invoices Generation Test found no Buy-Sell Invoices!");
			return;
		}

		//~: calculate the digest
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[]        lv = new byte[8];

		for(Object id : invoices)
		{
			long l = (Long)id;
			for(int i = 0;(i < 8);i++)
				lv[i] = (byte)( (l >> 8*i) & 0xFF );
			md.update(lv);
		}

		String        dg = new String(SU.bytes2hex(md.digest()));

		LU.W(ctx.log(), "found [", invoices.size(),
		  "] Invoices with Buy-Sell order type; digest = [",
		  dg.toUpperCase(), "]"
		);
	}
}