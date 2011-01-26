package com.tverts.shunts.reports;

/* standard Java classes */

import java.io.IOException;
import java.io.Writer;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;

/**
 * A strategy that prints reports for the logging level
 * that this trategy is designed for.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public interface SeShReportWriteMethod
{
	/* public: SeShReportWriteMethod interface */

	public void writeSeShReport(SelfShuntReport r, Writer o)
	  throws IOException;
}