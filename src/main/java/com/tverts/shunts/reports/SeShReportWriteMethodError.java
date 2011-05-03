package com.tverts.shunts.reports;

/**
 * This Self Shunt Report printing strategy writes
 * the error text of the failed critical shunt(s).
 *
 * @author anton.baukin@gmail.com
 */
public class   SeShReportWriteMethodError
	    extends SeShReportWriteMethodBase
{
	/* public: Singleton */

	public static SeShReportWriteMethodError getInstance()
	{
		return INSTANCE;
	}

	private static final SeShReportWriteMethodError INSTANCE =
	  new SeShReportWriteMethodError();

	protected SeShReportWriteMethodError()
	{}
}