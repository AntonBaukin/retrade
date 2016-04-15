package com.tverts.shunts.reports;

/* standard Java classes */

import java.io.IOException;
import java.io.Writer;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;
import com.tverts.shunts.SelfShuntTaskReport;
import com.tverts.shunts.SelfShuntUnitReport;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Self Shunt Report printing strategy for DEBUG level.
 *
 * On the debug level all the methods (with the name
 * and descriptions) of all the shunt units, with
 * all the result stuff are printed.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   SeShReportWriteMethodDebug
       extends SeShReportWriteMethodTrace
{
	/* public: Singleton */

	public static SeShReportWriteMethodDebug getInstance()
	{
		return INSTANCE;
	}

	private static final SeShReportWriteMethodDebug INSTANCE =
	  new SeShReportWriteMethodDebug();

	protected SeShReportWriteMethodDebug()
	{}

	/* protected: SeShReportWriteMethodBase interface */

	protected void closeShuntReport(Writer o, SelfShuntReport r)
	  throws IOException
	{
		writeShuntStat(o, r);

		L(o, "=====================================");
		N(o);
		N(o);
	}

	protected void writeTaskReportSuccess
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		String c = tr.isCritical()?(" [critical]"):("");

		N(o);
		L(o, "  succeed  : ", tr.getTaskName(), c);
		writeTaskReportDescr(o, tr);
	}

	protected void writeTaskReportAssertionError
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		N(o);
		L(o, "  failed   : ", tr.getTaskName());
		writeTaskReportDescr(o, tr);
		N(o);
		P(o, "  ~~> "); X(o, tr);
		N(o);
	}

	protected void writeTaskReportCriticalError
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		N(o);
		L(o, "  critical : ", tr.getTaskName());
		writeTaskReportDescr(o, tr);
		N(o);
		P(o, "  ~~> "); E(o, tr);
		N(o);
	}


	/* protected: writing sub-methods */

	protected void writeTaskReportDescr(Writer o, SelfShuntTaskReport tr)
	  throws IOException
	{
		String d = s2s(tr.getDescriptionEn());
		if(d == null) d = s2s(tr.getDescriptionLo());
		if(d == null) return;

		L(o, "             (", d, ")");
	}
}