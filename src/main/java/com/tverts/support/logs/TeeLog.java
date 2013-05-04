package com.tverts.support.logs;

/**
 * Log strategy that sends logging messages
 * to two logging strategies simultaneously.
 *
 *
 * @author anton.baukin@gmail.com
 */
public final class TeeLog implements LogStrategy
{
	/* public: constructor */

	public TeeLog(LogStrategy a, LogStrategy b)
	{
		if(a == null) throw new IllegalArgumentException();
		if(b == null) throw new IllegalArgumentException();

		this.a = a;
		this.b = b;
	}


	/* public: TeeLog interface */

	public LogStrategy a()
	{
		return a;
	}

	public LogStrategy b()
	{
		return b;
	}


	/* public: LogStrategy interface */

	public boolean  isLevel(LogLevel l, String d)
	{
		return a.isLevel(l, d) || b.isLevel(l, d);
	}

	public void     logMsg(LogLevel l, String d, CharSequence m)
	{
		a.logMsg(l, d, m);
		b.logMsg(l, d, m);
	}

	public void     logErr(LogLevel l, String d, CharSequence m, Throwable e)
	{
		a.logErr(l, d, m, e);
		b.logErr(l, d, m, e);
	}

	public LogLevel getMinLevel(String d)
	{
		LogLevel la = a.getMinLevel(d);
		LogLevel lb = b.getMinLevel(d);

		return (la.level() <= lb.level())?(la):(lb);
	}


	/* private: two logging ways */

	private LogStrategy a;
	private LogStrategy b;
}