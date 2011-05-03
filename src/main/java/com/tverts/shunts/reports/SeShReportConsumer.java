package com.tverts.shunts.reports;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;

/**
 * Processes the report generated as the result
 * of self shunts invocation.
 *
 * Some implementations of report consumers
 * may be thread-safe, but this is required
 * if single instance woukd be set for
 * several shunt services.
 *
 * @author anton.baukin@gmail.com
 */
public interface SeShReportConsumer
{
	/* public: SeShReportConsumer interface */

	public void consumeReport(SelfShuntReport report);
}