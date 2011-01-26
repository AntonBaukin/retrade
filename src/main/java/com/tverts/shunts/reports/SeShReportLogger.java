package com.tverts.shunts.reports;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntPoint;

/* com.tverts: support */

import com.tverts.support.LU;

/**
 * Writes shunt report as text to the log destination
 * {@link SelfShuntPoint#LOG_SHARED}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SeShReportLogger
       extends SeShReportStringWriterBase
{
	/* protected: SeShReportStringWriterBase innterface */

	protected void writeTrace(StringBuffer text)
	  throws Exception
	{
		LU.T(getLog(), text);
	}

	protected void writeDebug(StringBuffer text)
	  throws Exception
	{
		LU.D(getLog(), text);
	}

	protected void writeInfo(StringBuffer text)
	  throws Exception
	{
		LU.I(getLog(), text);
	}

	protected void writeWarn(StringBuffer text)
	  throws Exception
	{
		LU.W(getLog(), text);
	}

	protected void writeError(StringBuffer text)
	  throws Exception
	{
		LU.E(getLog(), text);
	}

	/* protected: SeShReportWriterBase interface */

	protected boolean isDebugLevel()
	{
		return LU.isD(getLog());
	}

	protected boolean isTraceLevel()
	{
		return LU.isT(getLog());
	}

	protected boolean isInfoLevel()
	{
		return LU.isI(getLog());
	}

	protected boolean isWarnLevel()
	{
		return LU.isW(getLog());
	}

	/* protected: logger */

	protected String  getLog()
	{
		return SelfShuntPoint.LOG_SHARED;
	}
}