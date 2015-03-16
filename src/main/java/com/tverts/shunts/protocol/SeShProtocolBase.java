package com.tverts.shunts.protocol;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntCtx;
import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.SelfShuntReport;
import com.tverts.shunts.SelfShuntTaskReport;
import com.tverts.shunts.SelfShuntUnitReport;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Some general issues of Shunt Protocol.
 *
 * The implementation is not coupled with
 * the actual method of invoking the shunts.
 *
 * Two methods are expected to implement:
 * HTTP and JMS.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SeShProtocolBase
       implements     SeShProtocol
{
	/* public static: additional errors */

	/**
	 * Means that the server is not available
	 * by the TCP ports provided by the config.
	 */
	public static class SeShConnectionFailed
	       extends      SeShProtocolError
	{}


	/**
	 * General failure when invoking HTTP request
	 * to the self shunting servlet.
	 */
	public static class SeShServletFailure
	       extends      SeShProtocolError
	{
		/* public: constructors */

		public SeShServletFailure(String message)
		{
			super(message);
		}

		public SeShServletFailure(String message, Throwable cause)
		{
			super(message, cause);
		}
	}


	/**
	 * Denotes a system error on the self shunting
	 * execution mechanism.
	 */
	public static class SeShSystemFailure
	       extends      SeShProtocolError
	{
		/* public: constructor */

		public SeShSystemFailure(SeShResponse response)
		{
			super(response.getSystemError());
			this.response = response;
		}


		/* public: SeShSystemFailure interface */

		public SeShResponse getResponse()
		{
			return response;
		}


		/* private: the response */

		private SeShResponse response;
	}


	/* public: constructor */

	public SeShProtocolBase()
	{
		this.prototolLog = new StringBuilder(2048);
		this.unitReports = createUnitReports();
	}


	/* public: protocol interface */

	public void openProtocol(SelfShuntCtx ctx)
	  throws SeShProtocolError, InterruptedException
	{
		this.context = ctx;
	}

	public SelfShuntReport closeProtocol()
	  throws SeShProtocolError, InterruptedException
	{
		return createShuntReport();
	}

	public Throwable       getSystemError()
	{
		return this.systemError;
	}

	public StringBuilder   getPrototolLog()
	{
		return prototolLog;
	}


	/* protected: protocol conversation */

	/**
	 * Creates the initial request to the shunt system.
	 * This request defines what actually be tested.
	 */
	protected abstract SeShRequestInitial createInitialRequest();


	/* protected: reports handling */

	protected SeShRequest     processResponse(SeShResponse r)
	  throws SeShProtocolError
	{
		//?: {response has log text} add it
		if(r.getLogText() != null)
			getPrototolLog().append(r.getLogText());

		//?: {has system error}
		if(r.getSystemError() != null)
		{
			this.systemError = r.getSystemError();
			logResponseSystemError(r);
			throw new SeShSystemFailure(r);
		}

		//?: {has no report}
		if(r.getReport() == null)
		{
			logResponseWithoutReport(r);
			return r.getNextRequest();
		}

		//~: has the report, process it
		return processUnitReport(r);
	}

	/**
	 * Handles the unit report of the request given and returns
	 * the next shunt request instance. This method is invoked
	 * only when the report instance is defined.
	 */
	protected SeShRequest     processUnitReport(SeShResponse r)
	{
		//~: (always) remember the report
		unitReports.add(r.getReport());

		//?: {has critical error} stop handling
		for(SelfShuntTaskReport tr : r.getReport().getTaskReports())
			if(!tr.isSuccess() && tr.isCritical())
			{
				logCriticalError(r, tr);
				return null;
			}

		//!: seems all right, send the next
		logNextRequest(r);
		return r.getNextRequest();
	}

	protected SelfShuntReport createShuntReport()
	{
		SelfShuntReport report = new SelfShuntReport();

		report.setUnitReports(unitReports.toArray(
		  new SelfShuntUnitReport[unitReports.size()]));
		report.setSystemError(systemError);

		return report;
	}

	protected List<SelfShuntUnitReport>
	                          createUnitReports()
	{
		return new ArrayList<SelfShuntUnitReport>(8);
	}


	/* protected: logging */

	protected String getLog()
	{
		return (log != null)?(log):
		  (log = LU.LB(SelfShuntPoint.LOG_SYSTEM, this.getClass()));
	}

	private String log;

	protected String logsig()
	{
		return String.format("SeSh-Protocol '%s'",
		  getClass().getSimpleName());
	}

	protected String logsig(SeShRequest r)
	{
		Object rk = r.getSelfShuntKey();
		if(rk != null) rk = SU.s2s(rk.toString());
		if(rk == null) rk = "UNKNOWN";

		return String.format("SeSh-Request [~>%s]", rk);
	}

	protected String logsig(SeShResponse r)
	{
		Object rk = (r.getThisRequest() == null)?(null)
		  :(r.getThisRequest().getSelfShuntKey());
		if(rk != null) rk = SU.s2s(rk.toString());
		if(rk == null) rk = "UNKNOWN";

		return String.format("SeSh-Response [<~%s]", rk);
	}

	protected String logsig(SelfShuntTaskReport tr)
	{
		String d = SU.s2s(tr.getDescriptionEn());

		return (d == null)
		  ?String.format("SeSh-Task '%s'", tr.getTaskName())
		  :String.format("SeSh-Task '%s' (%s)", tr.getTaskName(), d);
	}

	protected void   logResponseSystemError(SeShResponse r)
	{
		LU.E(getLog(), logsig(r), " reports SYSTEM ERROR: \n",
		  EX.e2en(r.getSystemError()));
	}

	protected void   logResponseWithoutReport(SeShResponse r)
	{
		if(!LU.isI(getLog())) return;

		LU.I(getLog(), logsig(), " got " , logsig(r),
		  "without a Unit Report!");
	}

	protected void   logCriticalError(SeShResponse r, SelfShuntTaskReport tr)
	{
		LU.E(getLog(), " CRITICAL ERROR within ",
		  logsig(r), " caused by ", logsig(tr));
	}

	protected void   logInitialRequest(SeShRequestInitial r)
	{
		if(!LU.isI(getLog())) return;

		LU.I(getLog(), logsig(), " created Initial SeSh-Request. \n" +
		  "Request key: [", r.getSelfShuntKey(), "]");
	}

	protected void   logNextRequest(SeShResponse r)
	{
		if(!LU.isD(getLog())) return;

		if(r.getNextRequest() == null) LU.I(getLog(),
		  logsig(), " found NO further requests, finising.");

		if(r.getNextRequest() != null) LU.I(getLog(),
		  logsig(), " got next ", logsig(r.getNextRequest()));
	}


	/* protected: protocol state */

	protected SelfShuntCtx  context;

	/**
	 * The system error of the client side that was
	 * remembered while executing the protocol.
	 */
	protected Throwable     systemError;

	protected StringBuilder prototolLog;

	protected final List<SelfShuntUnitReport> unitReports;


}