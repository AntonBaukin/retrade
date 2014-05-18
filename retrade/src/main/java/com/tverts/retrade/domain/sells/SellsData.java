package com.tverts.retrade.domain.sells;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.SellData;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Special version of Invoice Data for Sells Invoices
 * originated by Sells Sessions.
 *
 * @author anton.baukin@gmail.com
 */
public class SellsData extends SellData
{
	/* public: bean interface */

	public SellsSession getSession()
	{
		return session;
	}

	public void         setSession(SellsSession session)
	{
		this.session = session;
	}

	/**
	 * Sells Invoice is always altered with fixed sub-type 'A'.
	 */
	public Character    getSubType()
	{
		return 'A';
	}

	public void         setSubType(Character s)
	{
		EX.assertx(CMP.eq('A', s), "Sells Invoice Data has fixed sub-type 'A'!");
	}


	/* sells session reference */

	private SellsSession session;
}