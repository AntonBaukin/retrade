package com.tverts.shunts.reports;

/* standard Java classes */

import java.io.IOException;
import java.io.Writer;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;

/**
 * Self Shunt Report printing strategy for WARN level.
 *
 * On the warn level only the summary information
 * on the results is logged.
 *
 * @author anton.baukin@gmail.com
 */
public class   SeShReportWriteMethodWarn
       extends SeShReportWriteMethodBase
{
	/* public: Singleton */

	public static SeShReportWriteMethodWarn getInstance()
	{
		return INSTANCE;
	}

	private static final SeShReportWriteMethodWarn INSTANCE =
	  new SeShReportWriteMethodWarn();

	protected SeShReportWriteMethodWarn()
	{}

	/* protected: SeShReportWriteMethodBase interface */

	protected void writeShuntStat(Writer o, SelfShuntReport r)
	  throws IOException
	{
		N(o);
		N(o);
		L(o, "===== Self Shunt Results Totals =====");
		N(o);
		L(o, "shunt units  : ", statShuntUnits(r));
		L(o, "  succeed    : ", statShuntUnitsSucceed(r));
		L(o, "  failed     : ", statShuntUnitsFailed(r));
		L(o, "  critical   : ", statShuntUnitsCritical(r));
		N(o);
		L(o, "shunt tasks  : ", statShuntTasks(r));
		L(o, "  succeed    : ", statShuntTasksSucceed(r));
		L(o, "  failed     : ", statShuntTasksFailed(r));
		L(o, "  critical   : ", statShuntTasksCritical(r));
		N(o);
		N(o);
	}
}