package com.tverts.data;

/* com.tverts: services */

import com.tverts.system.services.events.EventBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Event to send to {@link ReportsService} to
 * make the defined Report Request.
 *
 * @author anton.baukin@gmail.com.
 */
public class MakeReportEvent extends EventBase
{
	public static final long serialVersionUID = 0L;

	/* public: constructors */

	public MakeReportEvent()
	{}

	public MakeReportEvent(Long reportRequest)
	{
		this.reportRequest = EX.assertn(reportRequest);
	}


	/* public: bean interface */

	public Long getReportRequest()
	{
		return reportRequest;
	}

	public void setReportRequest(Long reportRequest)
	{
		this.reportRequest = reportRequest;
	}


	/* private: event parameters */

	private Long reportRequest;
}