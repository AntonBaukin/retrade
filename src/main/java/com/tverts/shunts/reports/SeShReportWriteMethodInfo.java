package com.tverts.shunts.reports;

/* standard Java classes */

import java.io.IOException;
import java.io.Writer;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;
import com.tverts.shunts.SelfShuntTaskReport;
import com.tverts.shunts.SelfShuntUnitReport;

/**
 * Self Shunt Report printing strategy for INFO level.
 *
 * The info level is as the trace, but the results
 * are displayed shorter, without shunt unit descriptions.
 * Shunt tasks (methods) are printed only for failed ones.
 *
 * @author anton.baukin@gmail.com
 */
public class   SeShReportWriteMethodInfo
       extends SeShReportWriteMethodWarn
{
	/* public: Singleton */

	public static SeShReportWriteMethodInfo getInstance()
	{
		return INSTANCE;
	}

	private static final SeShReportWriteMethodInfo INSTANCE =
	  new SeShReportWriteMethodInfo();

	protected SeShReportWriteMethodInfo()
	{}

	/* protected: SeShReportWriteMethodBase interface */

	protected void openShuntReport(Writer o, SelfShuntReport r)
	  throws IOException
	{
		N(o);
		N(o);
		L(o, "=======   Self Shunt Report   =======");
		N(o);
	}

	protected void openUnitReport(Writer o, SelfShuntUnitReport ur)
	  throws IOException
	{
		openUnitReportHead(o, ur);
		openUnitReportFoot(o, ur);
	}

	protected void writeTaskReportAssertionError
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		L(o, "  failed   : ", tr.getTaskName());
		P(o, "  ~~> "); X(o, tr);
	}

	protected void writeTaskReportCriticalError
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		L(o, "! critical : ", tr.getTaskName());
		P(o, "  ~~> "); X(o, tr);
	}

	/* protected: writing sub-methods */

	protected void openUnitReportHead(Writer o, SelfShuntUnitReport ur)
	  throws IOException
	{
		N(o);
		L(o, "===== unit : ", ur.getUnitName());

		L(o, "==> tasks  ",  statShuntTasks(ur),
		     ", succ  ",     statShuntTasksSucceed(ur),
		     ", fail ",      statShuntTasksFailed(ur),
		     ", crit ",      statShuntTasksCritical(ur)
		);
	}

	protected void openUnitReportFoot(Writer o, SelfShuntUnitReport ur)
	  throws IOException
	{
		N(o);
	}
}