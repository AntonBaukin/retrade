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

	public Runnable generate()
	  throws GenesisError
	{
		LU.I(getLog(), logsig(), ": generated!");

		if(getWaitSingleShunt() != null) try
		{
			waitShuntWebSingle(getWaitSingleShunt());
		}
		catch(InterruptedException e)
		{
			throw new GenesisError(e, this, null);
		}

		if(getWaitShuntGroups() != null) try
		{
			waitShuntWebGroups(getWaitShuntGroups());
		}
		catch(InterruptedException e)
		{
			throw new GenesisError(e, this, null);
		}

		return null;
	}

	/* public: TestGenesisPart properties */

	public String   getWaitSingleShunt()
	{
		return waitSingleShunt;
	}

	public void     setWaitSingleShunt(String s)
	{
		this.waitSingleShunt = SU.s2s(s);
	}

	public String[] getWaitShuntGroups()
	{
		return waitShuntGroups;
	}

	public void     setWaitShuntGroupsStr(String g)
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