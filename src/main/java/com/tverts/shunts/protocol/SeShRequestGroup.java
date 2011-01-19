package com.tverts.shunts.protocol;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

/**
 * Initial request to call a sequence of self shunt
 * units that belong to the same group as the
 * parameter of this request.
 *
 * Note that the units within the group are invoked
 * in the order globally set for all the shunts of
 * the system.
 *
 * The key of this request is the name of the group.
 *
 * @author anton baukin (abaukin@mail.ru)
 *
 * TODO provide implementation support for SeShRequestGroup
 */
public class      SeShRequestGroup
       implements SeShRequestInitial
{
	public static final long serialVersionUID = 0L;

	/* public: SeShRequest interface */

	/**
	 * The name of the default group. This value is
	 * assigned to the request initially.
	 */
	public static final String DEF_GROUP = "default";

	public Object getSelfShuntKey()
	{
		return getGroup();
	}

	/* public: SeShRequestGroup interface */

	public String getGroup()
	{
		return group;
	}

	public void   setGroup(String group)
	{
		if((group = s2s(group)) == null)
			throw new IllegalArgumentException();

		this.group = group;
	}

	/* private: the group name */

	private String group = DEF_GROUP;
}