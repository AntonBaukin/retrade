package com.tverts.shunts.protocol;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;
import com.tverts.shunts.SelfShuntUnitReport;

/**
 * Some general issues of shunt protocol.
 *
 * The implementation is not coupled with
 * the actual method of invoking the shunts.
 *
 * Two methods are expected: HTTP and JMS.
 *
 * TODO: handle critical shunt errors here
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public abstract class SeShProtocolBase
       implements     SeShProtocol
{
	/* public: additional errors */

	/**
	 * Means that the server is not available
	 * by the TCP ports provided by the config.
	 *
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

		/* private: the request */

		private SeShResponse response;
	}

	/* public: protocol interface */

	public SelfShuntReport closeProtocol()
	  throws SeShProtocolError, InterruptedException
	{
		return createShuntReport();
	}

	/* protected: reports handling */

	protected List<SelfShuntUnitReport> getUnitReports()
	{
		return (unitReports != null)?(unitReports)
		  :(unitReports = createUnitReports());
	}

	protected List<SelfShuntUnitReport> createUnitReports()
	{
		return new ArrayList<SelfShuntUnitReport>(4);
	}

	protected SelfShuntReport           createShuntReport()
	{
		List<SelfShuntUnitReport> reports =
		  getUnitReports();
		SelfShuntReport           result  =
		  new SelfShuntReport();

		result.setUnitReports(reports.toArray(
		  new SelfShuntUnitReport[reports.size()]));
		result.setSystemError(getSystemError());

		return result;
	}

	/* protected: error storing */

	/**
	 * The system error of the client side that was
	 * remembered while executing the protocol.
	 */
	protected Throwable getSystemError()
	{
		return systemError;
	}

	protected void      setSystemError(Throwable systemError)
	{
		this.systemError = systemError;
	}

	/* private: accumulated reports */

	private List<SelfShuntUnitReport> unitReports;

	private Throwable                 systemError;
}