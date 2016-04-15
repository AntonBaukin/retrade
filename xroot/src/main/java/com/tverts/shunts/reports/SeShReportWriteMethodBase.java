package com.tverts.shunts.reports;

/* standard Java classes */

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;
import com.tverts.shunts.SelfShuntTaskReport;
import com.tverts.shunts.SelfShuntUnitReport;

/* com.tverts: support */

import static com.tverts.support.EX.e2en;
import static com.tverts.support.SU.s2s;


/**
 * Self Shunt Report printing strategy basics.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SeShReportWriteMethodBase
       implements     SeShReportWriteMethod
{
	/* public: SeShReportWriteMethod interface */

	public void writeSeShReport(SelfShuntReport r, Writer o)
	  throws IOException
	{
		//~: open the report
		openShuntReport(o, r);

		//?: {has Shunt system error}
		if(r.getSystemError() != null)
			writeSystemError(o, r);

		//c: write the unit reports
		for(SelfShuntUnitReport ur : r.getUnitReports())
			writeUnitReport(o, ur);

		//~: close the report
		closeShuntReport(o, r);
	}


	/* protected: writing sub-methods */

	protected void writeSystemError(Writer o, SelfShuntReport r)
	  throws IOException
	{
		L(o, "/! Self Shunt System Error !/");
		E(o, r.getSystemError());
	}

	protected void openShuntReport(Writer o, SelfShuntReport r)
	  throws IOException
	{}

	protected void writeShuntStat(Writer o, SelfShuntReport r)
	  throws IOException
	{}

	protected void writeShuntCriticalErrors(Writer o, SelfShuntReport r)
	  throws IOException
	{
		for(SelfShuntUnitReport ur : r.getUnitReports())
			for(SelfShuntTaskReport tr : ur.getTaskReports())
				if(!tr.isSuccess() && tr.isCritical())
					writeShuntReportCriticalError(o, ur, tr);
	}

	protected void writeShuntReportCriticalError
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		writeTaskReportCriticalErrorDefault(o, ur, tr);
	}

	protected void closeShuntReport(Writer o, SelfShuntReport r)
	  throws IOException
	{
		writeShuntStat(o, r);

		L(o, "=====================================");
		N(o);
		N(o);

		writeShuntCriticalErrors(o, r);
		N(o);
		N(o);
	}

	protected void writeUnitReport(Writer o, SelfShuntUnitReport ur)
	  throws IOException
	{
		openUnitReport(o, ur);
		for(SelfShuntTaskReport tr : ur.getTaskReports())
		   writeTaskReport(o, ur, tr);
		closeUnitReport(o, ur);
	}

	protected void openUnitReport(Writer o, SelfShuntUnitReport ur)
	  throws IOException
	{}

	protected void closeUnitReport(Writer o, SelfShuntUnitReport ur)
	  throws IOException
	{}

	protected void writeTaskReport
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		if(tr.isSuccess())
			writeTaskReportSuccess(o, ur, tr);
		else if(tr.isCritical())
			writeTaskReportCriticalError(o, ur, tr);
		else
			writeTaskReportAssertionError(o, ur, tr);
	}

	protected void writeTaskReportSuccess
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{}

	protected void writeTaskReportAssertionError
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{}

	protected void writeTaskReportCriticalError
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		writeTaskReportCriticalErrorDefault(o, ur, tr);
	}

	protected void writeTaskReportCriticalErrorDefault
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		N(o);
		N(o);
		L(o, "/! Self Shunt Task Critical Error !/");
		N(o);
		L(o, "==== unit: ", ur.getUnitName(),
		     ", ~> task ",  tr.getTaskName(),
		     ": "
		);
		N(o);
		E(o, tr);
		N(o);
	}


	/* protected: printing helpers */

	/**
	 * Writes the objects converting them with
	 * {@link Object#toString()}.
	 *
	 * Omits {@code null} instances.
	 */
	protected void P(Writer o, Object... objs)
	  throws IOException
	{
		for(Object obj : objs)
		{
			String s = (obj == null)?(null):(obj.toString());
			if((s != null) && (s.length() != 0))
				o.write(s);
		}
	}

	/**
	 * Adds new-line character(s).
	 */
	protected void N(Writer o)
	  throws IOException
	{
		o.write('\n');
	}

	/**
	 * Writes the objects converting them with
	 * {@link Object#toString()}.
	 *
	 * Appends single new-line character(s)
	 * at the very end, but not after each object.
	 *
	 * Omits {@code null} instances. If no output
	 * done, new line character is also not printed.
	 */
	protected void L(Writer o, Object... objs)
	  throws IOException
	{
		boolean x = false;

		for(Object obj : objs)
		{
			String s = (obj == null)?(null):(obj.toString());
			if((s != null) && (s.length() != 0))
			{
				x = true;
				o.write(s);
			}
		}

		if(x) N(o);
	}

	protected void E(Writer o, Throwable e)
	  throws IOException
	{
		if(e == null) return;

		L(o, e2en(e));

		StringWriter sw = new StringWriter(1024);
		PrintWriter  pw = new PrintWriter(sw);

		e.printStackTrace(pw);
		pw.close();
		L(o, sw.getBuffer());
	}

	/**
	 * Writes the text of the task report's error.
	 * Skips the reports without an error.
	 * Ends the line, but not inserts leading new lines.
	 */
	protected void X(Writer o, SelfShuntTaskReport tr)
	  throws IOException
	{
		//print the error text
		String       ms = s2s(tr.getErrorTextEn());
		if((ms == null) && (tr.getError() != null))
			ms = e2en(tr.getError());

		if(ms != null)
			L(o, ms);
	}

	/**
	 * Works as {@link #X(Writer, SelfShuntTaskReport)},
	 * but also appends the error stack trace.
	 */
	protected void E(Writer o, SelfShuntTaskReport tr)
	  throws IOException
	{
		X(o, tr);

		//?: {has no error object} skip it
		if(tr.getError() == null) return;

		//print the exception stack
		StringWriter sw = new StringWriter(1024);
		PrintWriter  pw = new PrintWriter(sw);

		tr.getError().printStackTrace(pw);
		pw.close();
		L(o, sw.getBuffer());
	}


	/* protected: statistics calculation */

	protected int  statShuntUnits(SelfShuntReport r)
	{
		return r.getUnitReports().length;
	}

	protected int  statShuntUnitsSucceed(SelfShuntReport r)
	{
		int n = 0;

		for(SelfShuntUnitReport ur : r.getUnitReports())
			if(ur.isSuccess())
				n++;

		return n;
	}

	/**
	 * Total number of failed units, both assertion and critical.
	 */
	protected int  statShuntUnitsFailed(SelfShuntReport r)
	{
		int n = 0;

		for(SelfShuntUnitReport ur : r.getUnitReports())
			if(!ur.isSuccess())
				n++;

		return n;
	}

	protected int  statShuntUnitsCritical(SelfShuntReport r)
	{
		int n = 0;

		for(SelfShuntUnitReport ur : r.getUnitReports())
			if(!ur.isSuccess() && ur.isCritical())
				n++;

		return n;
	}

	protected int  statShuntTasks(SelfShuntReport r)
	{
		int n = 0;

		for(SelfShuntUnitReport ur : r.getUnitReports())
			n += ur.getTaskReports().size();
		return n;
	}

	protected int  statShuntTasks(SelfShuntUnitReport ur)
	{
		return ur.getTaskReports().size();
	}

	protected int  statShuntTasksSucceed(SelfShuntReport r)
	{
		int n = 0;

		for(SelfShuntUnitReport ur : r.getUnitReports())
			for(SelfShuntTaskReport tr : ur.getTaskReports())
				if(tr.isSuccess()) n++;

		return n;
	}

	protected int  statShuntTasksSucceed(SelfShuntUnitReport ur)
	{
		int n = 0;

		for(SelfShuntTaskReport tr : ur.getTaskReports())
			if(tr.isSuccess()) n++;
		return n;
	}

	protected int  statShuntTasksFailed(SelfShuntReport r)
	{
		int n = 0;

		for(SelfShuntUnitReport ur : r.getUnitReports())
			for(SelfShuntTaskReport tr : ur.getTaskReports())
				if(!tr.isSuccess()) n++;

		return n;
	}

	protected int  statShuntTasksFailed(SelfShuntUnitReport ur)
	{
		int n = 0;

		for(SelfShuntTaskReport tr : ur.getTaskReports())
			if(!tr.isSuccess()) n++;
		return n;
	}

	protected int  statShuntTasksCritical(SelfShuntReport r)
	{
		int n = 0;

		for(SelfShuntUnitReport ur : r.getUnitReports())
			for(SelfShuntTaskReport tr : ur.getTaskReports())
				if(!tr.isSuccess() && tr.isCritical()) n++;

		return n;
	}

	protected int  statShuntTasksCritical(SelfShuntUnitReport ur)
	{
		int n = 0;

		for(SelfShuntTaskReport tr : ur.getTaskReports())
			if(!tr.isSuccess() && tr.isCritical()) n++;
		return n;
	}
}