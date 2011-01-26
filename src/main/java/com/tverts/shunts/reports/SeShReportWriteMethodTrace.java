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
 * Self Shunt Report printing strategy for TRACE level.
 *
 * On the trace level the results are printed
 * for each shunt unit. The statistics for the
 * methods is aggregated.
 *
 * The full list of shunt methods names is also added.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SeShReportWriteMethodTrace
       extends SeShReportWriteMethodInfo
{
	/* public: Singleton */

	public static SeShReportWriteMethodTrace getInstance()
	{
		return INSTANCE;
	}

	private static final SeShReportWriteMethodTrace INSTANCE =
	  new SeShReportWriteMethodTrace();

	protected SeShReportWriteMethodTrace()
	{}

	/* protected: SeShReportWriteMethodInfo interface */

	protected void openUnitReportFoot(Writer o, SelfShuntUnitReport ur)
	  throws IOException
	{
		String d = s2s(ur.getDescriptionEn());
		if(d == null) d = s2s(ur.getDescriptionLo());

		if(d != null)
			L(o, "(", d, ")");
		N(o);

	}

	/* protected: SeShReportWriteMethodBase interface */

	protected void writeTaskReportSuccess
	  (Writer o, SelfShuntUnitReport ur, SelfShuntTaskReport tr)
	  throws IOException
	{
		L(o, "  succeed  : ", tr.getTaskName());
	}
}