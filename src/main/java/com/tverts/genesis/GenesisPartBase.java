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
	/* public: GenesisReference interface */

	public List<Genesis> dereferObjects()
	{
		return Collections.<Genesis> singletonList(this);
	}


	/* protected: generation support */

	/**
	 * Waits execution of single Self Shunt Request with the name given.
	 *
	 * WARNING: see {@link #waitShuntWeb(GenCtx, SeShRequestInitial)}.
	 */
	protected SelfShuntReport waitShuntWebSingle(GenCtx ctx, String shuntName)
	  throws InterruptedException
	{
		return this.waitShuntWeb(
		  ctx, new SeShRequestSingle(shuntName));
	}

	/**
	 * Waits execution of the named group of Self Shunt Requests.
	 *
	 * WARNING: see {@link #waitShuntWeb(GenCtx, SeShRequestInitial)}.
	 */
	protected SelfShuntReport waitShuntWebGroups(GenCtx ctx, String... groups)
	  throws InterruptedException
	{
		SeShRequestGroups request = new SeShRequestGroups();

		request.setGroups(groups);
		return this.waitShuntWeb(ctx, request);
	}

	/**
	 * Waits until Self Shunt Request is executed by Self Shunt Service.
	 *
	 * WARNING: you may call this method only from genesis units
	 *   that are not within the initial units of the Genesis Service.
	 *   In default configuration Shunt Service waits the initial
	 *   generation to complete and is not operating!
	 */
	protected SelfShuntReport waitShuntWeb(GenCtx ctx, SeShRequestInitial request)
	  throws InterruptedException
	{
		final WaitShuntFinish finish = new WaitShuntFinish();

		logWaitShuntStart(ctx, request);

		synchronized(finish)
		{
			SelfShuntPoint.getInstance().
			  executeSelfShuntWeb(request, finish);

			//!: wait for the shunt to finish
			finish.wait();
		}

		logWaitShuntDone(ctx);
		return finish.getReport();
	}


	/* protected: wait shunt finish */

	protected class WaitShuntFinish implements SeShProtocolFinish
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

	protected void   logWaitShuntStart(GenCtx ctx, SeShRequestInitial request)
	{
		if(!LU.isI(log(ctx))) return;

		LU.I(log(ctx), logsig(),
		  " waiting for Shunt Unit to finish: ",
		  request.getSelfShuntKey(), "...");
	}

	protected void   logWaitShuntDone(GenCtx ctx)
	{
		if(!LU.isI(log(ctx))) return;

		LU.I(log(ctx), logsig(), "... done waiting shunt!");
	}
}