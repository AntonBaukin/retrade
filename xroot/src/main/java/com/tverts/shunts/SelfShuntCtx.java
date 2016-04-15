package com.tverts.shunts;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;


/**
 * The context of Self-Shunt invocation.
 *
 * @author anton.baukin@gmail.com
 */
public class SelfShuntCtx implements java.io.Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: constructor */

	public SelfShuntCtx(String uid)
	{
		this.uid = uid;
	}


	/* public: SelfShuntCtx interface */

	/**
	 * Returns ID of this Shunt Context unique
	 * within the system at the runtime.
	 */
	public final String        getUID()
	{
		return uid;
	}

	/**
	 * Defines the Domain the tests must be done for.
	 */
	public Long                getDomain()
	{
		return domain;
	}

	public SelfShuntCtx        setDomain(Long domain)
	{
		this.domain = domain;
		return this;
	}

	/**
	 * If this flag set, Self-Shunt MUST not update
	 * the data of the Domain, buy only read them!
	 */
	public boolean             isReadonly()
	{
		return readonly;
	}

	public SelfShuntCtx        setReadonly()
	{
		this.readonly = true;
		return this;
	}

	/**
	 * Generation context state. It is defined
	 * only when Shunt was activated by Genesis
	 * Done event what is true when the system
	 * starting up via Main Service, but not
	 * valid for Shunts activated from the UI.
	 *
	 * Note that only Serializable parameters
	 * are passed from the genesis.
	 */
	@SuppressWarnings("unchecked")
	public Map                 getGenCtx()
	{
		return (genCtx != null)?(genCtx):
		  (genCtx = new HashMap());
	}

	public void                setGenCtx(Map genCtx)
	{
		this.genCtx = genCtx;
	}

	public SelfShuntUnitReport getReport()
	{
		return report;
	}

	public SelfShuntCtx        setReport(SelfShuntUnitReport report)
	{
		this.report = report;
		return this;
	}


	/* private: context parameters */

	private String  uid;
	private Long    domain;
	private boolean readonly;
	private Map genCtx;


	/* private: context state */

	private SelfShuntUnitReport report;
}