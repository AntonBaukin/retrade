package com.tverts.shunts.protocol;

/* standard Java classes */

import java.util.Set;
import java.util.TreeSet;

/* com.tverts: support */

import static com.tverts.support.SU.a2s;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.s2a;

/**
 * Initial request to call a sequence of self shunt
 * units that belong to the same groups as the array
 * parameter of this request.
 *
 * Note that the units within the group are invoked
 * in the order globally set for all the shunts of
 * the system. Also, the order of group names has
 * no effect.
 *
 * The key of this request is the string with the ordered
 * names of the groups separated by {@code ", "}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public final class SeShRequestGroups
       implements  SeShRequestInitial
{
	public static final long serialVersionUID = 0L;

	/* public: SeShRequest interface */

	public Object   getSelfShuntKey()
	{
		return a2s(this.groups);
	}

	/* public: SeShRequestGroups interface */

	public String[] getGroups()
	{
		return groups;
	}

	public void     setGroups(String[] groups)
	{
		Set<String> gset = new TreeSet<String>();

		if(groups != null) for(String g : groups)
			gset.add(s2s(g));
		gset.remove(null);

		this.groups = gset.toArray(new String[gset.size()]);
	}

	public void     setGroups(String groups)
	{
		this.setGroups(s2a(groups));
	}

	/* private: the group name */

	private String[] groups = new String[0];
}