package com.tverts.shunts.reports;

/* standard Java classes */

import java.io.Writer;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;


/**
 * Prints the report as a text. Supports four
 * logging levels: DEBUG, TRACE, INFO and WARN.
 *
 * For each log lever there is a write strategy
 * {@link SeShReportWriteMethod}. If no defined,
 * the one from the upper log level is selected.
 *
 * This basic implementation is thread-safe if
 * the installed report write methods and the
 * writer stream are safe. The writer stream
 * must be a string buffer to prevent mess of
 * text parts printed for reports consumed
 * at the same time.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SeShReportWriterBase
       implements     SeShReportConsumer
{
	/* public: SeShReportConsumer interface */

	public void consumeReport(SelfShuntReport report)
	{
		try
		{
			//~: open the writer
			Writer out = openOut();

			//~: select the write method & do write
			selectWriteMethod().writeSeShReport(report, out);

			//!: close the writer
			closeOut(out);
		}
		catch(Exception e)
		{
			handleOutError(e);
		}
	}


	/* protected: log levels */

	protected abstract boolean isDebugLevel();

	protected abstract boolean isTraceLevel();

	protected abstract boolean isInfoLevel();

	protected abstract boolean isWarnLevel();


	/* protected: printing stream */

	protected abstract Writer  openOut()
	  throws Exception;

	protected abstract void    closeOut(Writer out)
	  throws Exception;

	protected void             handleOutError(Exception e)
	{
		throw new RuntimeException(e);
	}


	/* protected: printing method selection */

	protected SeShReportWriteMethod getWriteMethodDebug()
	{
		return SeShReportWriteMethodDebug.getInstance();
	}

	protected SeShReportWriteMethod getWriteMethodTrace()
	{
		return SeShReportWriteMethodTrace.getInstance();
	}

	protected SeShReportWriteMethod getWriteMethodInfo()
	{
		return SeShReportWriteMethodInfo.getInstance();
	}

	protected SeShReportWriteMethod getWriteMethodWarn()
	{
		return SeShReportWriteMethodWarn.getInstance();
	}

	protected SeShReportWriteMethod selectWriteMethod()
	{
		SeShReportWriteMethod m = null;
		boolean               x;

		//?: {debug level}
		if(x = isDebugLevel())
			m = getWriteMethodDebug();
		if(m != null) return m;

		//?: {debug | trace level}
		if(x || (x = isTraceLevel()))
			m = getWriteMethodTrace();
		if(m != null) return m;

		//?: {debug | trace | info level}
		if(x || (x = isInfoLevel()))
			m = getWriteMethodInfo();
		if(m != null) return m;

		//?: {debug | trace | info | warn level}
		if(x || isWarnLevel())
			m = getWriteMethodWarn();

		return (m != null)?(m):
		  SeShReportWriteMethodError.getInstance();
	}
}