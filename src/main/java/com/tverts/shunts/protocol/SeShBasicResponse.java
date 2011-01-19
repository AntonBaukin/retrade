package com.tverts.shunts.protocol;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntUnitReport;

/**
 * A {@link SeShResponse} structure.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      SeShBasicResponse
       implements SeShResponse
{
	public static final long serialVersionUID = 0L;

	/* public: constructor */

	public SeShBasicResponse(SeShRequest thisRequest)
	{
		this.thisRequest = thisRequest;
	}

	/* public: SeShResponse interface */

	public SeShRequest         getThisRequest()
	{
		return thisRequest;
	}

	public SeShRequest         getNextRequest()
	{
		return nextRequest;
	}

	public SelfShuntUnitReport getReport()
	{
		return report;
	}

	public Throwable           getSystemError()
	{
		return systemError;
	}

	/* public: SeShBasicResponse (bean access) */

	public void setNextRequest(SeShRequest nextRequest)
	{
		this.nextRequest = nextRequest;
	}

	public void setReport(SelfShuntUnitReport report)
	{
		this.report = report;
	}

	public void setSystemError(Throwable e)
	{
		this.systemError = e;
	}

	/* private: fields of the response */

	private SeShRequest         thisRequest;
	private SeShRequest         nextRequest;
	private SelfShuntUnitReport report;
	private Throwable           systemError;
}