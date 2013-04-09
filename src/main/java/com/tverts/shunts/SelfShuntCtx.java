package com.tverts.shunts;

/**
 * The context of Self-Shunt invocation.
 *
 * @author anton.baukin@gmail.com
 */
public class SelfShuntCtx implements java.io.Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: constructor */

	public SelfShuntCtx(
	    String uid,  SelfShuntUnitReport report,
	    Long domain, boolean readonly
	  )
	{
		this.uid      = uid;
		this.report   = report;
		this.domain   = domain;
		this.readonly = readonly;
	}


	/* public: SelfShuntCtx interface */

	/**
	 * Returns ID of this Shunt Context unique
	 * within the system single run.
	 */
	public String getUID()
	{
		return uid;
	}

	public SelfShuntUnitReport getReport()
	{
		return report;
	}

	/**
	 * Defines the Domain the tests must be done for.
	 */
	public Long getDomain()
	{
		return domain;
	}

	/**
	 * If this flag set, Self-Shunt MUST not update
	 * the data of the Domain, buy only read them!
	 */
	public boolean isReadonly()
	{
		return readonly;
	}


	/* private: context state */

	private String              uid;
	private SelfShuntUnitReport report;
	private Long                domain;
	private boolean             readonly;
}