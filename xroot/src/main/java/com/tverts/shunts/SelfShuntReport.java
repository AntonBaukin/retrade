package com.tverts.shunts;

/**
 * Collects results of Self Shunts
 * invocations sequence.
 *
 * @author anton.baukin@gmail.com
 */
public class      SelfShuntReport
       implements java.io.Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: report properties access */

	/**
	 * Reports on each Self Shunt invocation.
	 */
	public SelfShuntUnitReport[] getUnitReports()
	{
		return unitReports;
	}

	public void setUnitReports(SelfShuntUnitReport[] reports)
	{
		this.unitReports = reports;
	}

	/**
	 * A system error that had caused the shunts
	 * invocation to be halted.
	 *
	 * Note that this exception is of that system
	 * errors that are generated by the shunts
	 * executing service.
	 */
	public Throwable getSystemError()
	{
		return systemError;
	}

	public void setSystemError(Throwable systemError)
	{
		this.systemError = systemError;
	}


	/* private: the results */

	private SelfShuntUnitReport[] unitReports;
	private Throwable             systemError;
}