package com.tverts.support.logs;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;


/**
 * Log strategy that saves INFO, WARN, and
 * ERROR messages into string buffer.
 *
 * This implementation is not thread-safe.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class InfoLogBuffer implements LogStrategy
{
	/* public: InfoLogBuffer interface */

	public StringBuilder getBuffer()
	{
		return buffer;
	}


	/* public: LogStrategy interface */

	public boolean  isLevel(LogLevel l, String d)
	{
		return (l.level() >= getMinLevel(d).level());
	}

	public void     logMsg(LogLevel l, String d, CharSequence m)
	{
		if(!isLevel(l, d)) return;

		String lv = l.toString();

		//~: level
		buffer.append(lv);
		buffer.append((lv.length() == 4)?("  "):(" "));

		//~: timestamp
		DU.timefull2str(buffer, java.util.Calendar.getInstance());
		buffer.append(' ');

		//~: the message
		buffer.append(m);

		//~: new line
		buffer.append('\n');
	}

	public void     logErr(LogLevel l, String d, CharSequence m, Throwable e)
	{
		//~: log as always
		logMsg(l, d, m);

		//~: print error
		EX.print(e, buffer);
	}

	public LogLevel getMinLevel(String d)
	{
		return LogLevel.INFO;
	}


	/* the buffer */

	protected final StringBuilder buffer =
	  new StringBuilder(2048);
}