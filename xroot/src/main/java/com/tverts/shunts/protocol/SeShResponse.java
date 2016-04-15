package com.tverts.shunts.protocol;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntUnitReport;

/**
 * A response from the Self-Shunt Service.
 * It reports the status of invoking request.
 *
 * The organization of shunts invocation
 * mechanism is self-incapsulated. The caller
 * does not need to know what the shunts
 * to invoke: it obtains the initial request
 * and the following request through the same
 * protocol sending special classes of requests.
 *
 * The present implementation does allow to
 * call not all the shunts, but a named subset
 * of the shunts, or named shunts group.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SeShResponse
       extends   java.io.Serializable
{
	/* public: SeShResponse interface */

	/**
	 * Returns the request that had originated this
	 * response object. Note that the serialized
	 * request instance is not modified in the shunts
	 * invocation mechanism.
	 */
	public SeShRequest         getThisRequest();

	/**
	 * Returns the next request to continue the shunts
	 * in
	 */
	public SeShRequest         getNextRequest();

	/**
	 * The report in {@link #getThisRequest()} invocation.
	 * For initial requests is not defined.
	 */
	public SelfShuntUnitReport getReport();

	/**
	 * The error of the invocation of the shunt.
	 * It is not an error of the shunt itself,
	 * but the system-level error of the shunts
	 * servlet.
	 */
	public Throwable           getSystemError();

	public String              getLogText();
	public void                setLogText(String log);
}
