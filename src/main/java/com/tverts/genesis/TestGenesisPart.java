package com.tverts.genesis;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Tests coupling with the Self Shunt Service.
 *
 * @author anton.baukin@gmail.com
 */
public class   TestGenesisPart
       extends GenesisPartBase
{
	/* public: Genesis interface */

	public void      generate(GenCtx ctx)
	  throws GenesisError
	{
		LU.I(log(ctx), logsig(), ": generated!");

		if(getWaitSingleShunt() != null) try
		{
			waitShuntWebSingle(ctx, getWaitSingleShunt());
		}
		catch(InterruptedException e)
		{
			throw new GenesisError(e, this, ctx);
		}

		if(getWaitShuntGroups() != null) try
		{
			waitShuntWebGroups(ctx, getWaitShuntGroups());
		}
		catch(InterruptedException e)
		{
			throw new GenesisError(e, this, ctx);
		}
	}


	/* public: TestGenesisPart properties */

	public String    getWaitSingleShunt()
	{
		return waitSingleShunt;
	}

	/**
	 * WARNING: see {@link #waitShuntWeb(GenCtx,
	 *   com.tverts.shunts.protocol.SeShRequestInitial)}.
	 */
	public void      setWaitSingleShunt(String s)
	{
		this.waitSingleShunt = SU.s2s(s);
	}

	public String[]  getWaitShuntGroups()
	{
		return waitShuntGroups;
	}

	/**
	 * WARNING: see {@link #waitShuntWeb(GenCtx,
	 *   com.tverts.shunts.protocol.SeShRequestInitial)}.
	 */
	public void      setWaitShuntGroupsStr(String g)
	{
		this.waitShuntGroups = SU.s2a(g);
	}


	/* protected: logging */

	protected String logsig()
	{
		if(getClass().getSimpleName().equalsIgnoreCase(getName()))
			return getClass().getSimpleName();

		return String.format("%s '%s'",
		  getClass().getSimpleName(), getName());
	}


	/* private: parameters of the test unit */

	private String   waitSingleShunt;
	private String[] waitShuntGroups;
}