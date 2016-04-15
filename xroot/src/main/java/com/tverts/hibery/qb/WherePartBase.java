package com.tverts.hibery.qb;

/* Java */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Implementation base of {@link WherePart} interface.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class WherePartBase
       implements     WherePart
{
	/* public: WherePart interface */

	public Map<String, Object> getParams()
	{
		return (params != null)?(params):
		  (params = new HashMap<String, Object>(5));
	}

	public String getParamsPrefix()
	{
		return prefix;
	}


	/* public: WherePartBase interface */

	public WherePartBase setParamsPrefix(String prefix)
	{
		this.prefix = s2s(prefix);
		return this;
	}

	/**
	 * Add, overwrites and removes the parameter assignment.
	 * To remove pass {@code null}.
	 */
	public WherePartBase param(String name, Object val)
	{
		if(val == null)
			getParams().remove(name);
		else
			getParams().put(name, val);

		return this;
	}


	/* private: parameters + the prefix */

	private Map<String, Object> params;
	private String              prefix;
}
