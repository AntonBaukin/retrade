package com.tverts.shunts.reports;

/* standard Java classes */

import java.io.IOException;
import java.io.Writer;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;


/**
 * A strategy that prints reports for the logging
 * level this strategy is designed for.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SeShReportWriteMethod
{
	/* public: SeShReportWriteMethod interface */

	public void writeSeShReport(SelfShuntReport r, Writer o)
	  throws IOException;
}