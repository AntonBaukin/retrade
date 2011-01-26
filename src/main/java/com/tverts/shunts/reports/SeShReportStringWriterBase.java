package com.tverts.shunts.reports;

/* standard Java classes */

import java.io.StringWriter;
import java.io.Writer;

/**
 * Extends writer strategy base to print the
 * report text into string stream (buffer).
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public abstract class SeShReportStringWriterBase
       extends        SeShReportWriterBase
{
	/* protected: printing stream */

	protected abstract void writeTrace(StringBuffer text)
	  throws Exception;

	protected abstract void writeDebug(StringBuffer text)
	  throws Exception;

	protected abstract void writeInfo(StringBuffer text)
	  throws Exception;

	protected abstract void writeWarn(StringBuffer text)
	  throws Exception;

	protected abstract void writeError(StringBuffer text)
	  throws Exception;

	/**
	 * Creates {@link StringWriter} instance.
	 */
	protected Writer        openOut()
	  throws Exception
	{
		return new StringWriter(1024);
	}

	protected void          closeOut(Writer out)
	  throws Exception
	{
		StringBuffer s;

		if(!(out instanceof StringWriter))
			throw new IllegalArgumentException();

		out.close();
		s = ((StringWriter)out).getBuffer();

		if     (isDebugLevel())
			writeDebug(s);
		else if(isTraceLevel())
			writeTrace(s);
		else if(isInfoLevel())
			writeInfo(s);
		else if(isWarnLevel())
			writeWarn(s);
		else
			writeError(s);
	}
}