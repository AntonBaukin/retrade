package com.tverts.shunts.reports;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;


/**
 * Processes the report generated as the result
 * of Self-Shunts invocation.
 *
 * Some implementations of report consumers
 * may be thread-safe, but this is required
 * if single instance would be set for
 * several Shunt Services.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SeShReportConsumer
{
	/* public: SeShReportConsumer interface */

	public void consumeReport(SelfShuntReport report);
}