package com.tverts.genesis;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* com.tverts: self shunts */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.SelfShuntReport;

import com.tverts.shunts.protocol.SeShProtocolFinish;
import com.tverts.shunts.protocol.SeShRequestInitial;
import com.tverts.shunts.protocol.SeShRequestGroups;
import com.tverts.shunts.protocol.SeShRequestSingle;

/* com.tverts: predicates */

import com.tverts.support.logic.False;
import com.tverts.support.logic.Predicate;

/* com.tverts: support */

import com.tverts.support.LU;

/**
 * Provides useful utilities to Genesis
 * implementation.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GenesisPartBase
       extends        GenesisBase
       implements     GenesisReference
{
	/* public: Genesis interface */

	public Predicate     getCondition()
	{
		return (isDisabled())?(False.getInstance()):
		  (super.getCondition());
	}

	public boolean       isTransactional()
	{
		return false;
	}

	/* public: GenesisReference interface */

	public List<Genesis> dereferObjects()
	{
		return Collections.<Genesis> singletonList(this);
	}

	/* public: GenesisPartBase properties */

	/**
	 * Disable the unit not to run
	 * it in any condition.
	 */
	public boolean       isDisabled()
	{
		return disabled;
	}

	public void          setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}

	/* protected: generation support */

	/**
	 * Waits execution of single Self Shunt Request with the name given.
	 *
	 * WARNING: see {@link #waitShuntWeb(SeShRequestInitial)}.
	 */
	protected SelfShuntReport waitShuntWebSingle(String shuntName)
	  throws InterruptedException
	{
		return this.waitShuntWeb(
		  new SeShRequestSingle(shuntName));
	}

	/**
	 * Waits execution of the named group of Self Shunt Requests.
	 *
	 * WARNING: see {@link #waitShuntWeb(SeShRequestInitial)}.
	 */
	protected SelfShuntReport waitShuntWebGroups(String... groups)
	  throws InterruptedException
	{
		SeShRequestGroups request = new SeShRequestGroups();

		request.setGroups(groups);
		return this.waitShuntWeb(request);
	}

	/**
	 * Waits until Self Shunt Request is executed by Self Shunt Servive.
	 *
	 * WARNING: you may call this method only from genesis units
	 *   that are not within the initial units of the Genesis Service.
	 *   In default configuration Shunt Service waits the initial
	 *   generation to complete and is not operating!
	 */
	protected SelfShuntReport waitShuntWeb(SeShRequestInitial request)
	  throws InterruptedException
	{
		final WaitShuntFinish finish = new WaitShuntFinish();

		logWaitShuntStart(request);

		synchronized(finish)
		{
			SelfShuntPoint.getInstance().
			  enqueueSelfShuntWeb(request, finish);

			//!: wait for the shunt to finish
			finish.wait();
		}

		logWaitShuntDone();
		return finish.getReport();
	}

	/* protected: wait shunt finish */

	protected class      WaitShuntFinish
	          implements SeShProtocolFinish
	{
		/* public: SeShProtocolFinish interface */

		public synchronized void finishProtocol(SelfShuntReport report)
		{
			this.report = report;
			this.notify();
		}

		/* public: WaitShuntFinish interface */

		public SelfShuntReport getReport()
		{
			return report;
		}

		/* private: report reference */

		private SelfShuntReport report;
	}

	/* protected: logging */

	protected void   logWaitShuntStart(SeShRequestInitial request)
	{
		if(!LU.isI(getLog())) return;

		LU.I(getLog(), logsig(),
		  " WAITING for Shunt Unit to finish: [",
		  request.getSelfShuntKey(), "]...");
	}

	protected void   logWaitShuntDone()
	{
		if(!LU.isI(getLog())) return;

		LU.I(getLog(), logsig(), ": DONE waiting");
	}

	/* private: the state of the unit */

	private boolean disabled;
}