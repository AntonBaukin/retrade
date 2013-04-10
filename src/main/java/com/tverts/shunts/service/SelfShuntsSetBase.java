package com.tverts.shunts.service;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShunt;

/* com.tverts: support */

import static com.tverts.support.SU.a2a;
import static com.tverts.support.SU.s2s;


/**
 * Solves the task of naming Shunt Unit instances.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SelfShuntsSetBase
       implements     SelfShuntsSet
{
	/* public: SelfShuntsSet interface */

	public Set<String> enumShunts()
	{
		return getShuntsNames().keySet();
	}

	public Set<String> enumShuntsByGroups(String... groups)
	{
		LinkedHashSet<String> res =
		  new LinkedHashSet<String>(17);

		HashSet<String>       ges =
		  new HashSet<String>(Arrays.asList(a2a(groups)));
		if(ges.isEmpty()) return res;

		next_entry:
		for(Entry<String, SelfShunt> se : getShuntsNames().entrySet())
		{
			//~: get the groups of the shunt
			String[] seg = a2a(se.getValue().getGroups());
			if((seg == null) || (seg.length == 0)) continue;

			for(String g : seg) if(ges.contains(g))
			{
				res.add(se.getKey());
				continue next_entry;
			}
		}

		return res;
	}

	public Set<String> enumShuntsByName(String name)
	{
		if((name = s2s(name)) == null)
			throw new IllegalArgumentException();

		//~: prefix of shunts with same names
		String namei = name + '#';

		LinkedHashSet<String> res   =
		  new LinkedHashSet<String>(3);

		//c: for all the shunts
		for(String key : getShuntsNames().keySet())
			if(key.equals(name) || key.startsWith(namei))
				res.add(key);

		return res;
	}

	public SelfShunt   getShunt(String key)
	{
		return getShuntsNames().get(key);
	}


	/* protected: shunts names mapping */

	protected Map<String, SelfShunt> getShuntsNames()
	{
		if(this.shuntsNames == null)
		{
			List<SelfShunt> shunts = listShunts();

			if(shunts.isEmpty())
				this.shuntsNames = Collections.emptyMap();
			else
				this.shuntsNames = wrapShuntsNames(
				  buildShuntsNames(shunts));
		}

		return shuntsNames;
	}

	protected Map<String, SelfShunt> wrapShuntsNames(Map<String, SelfShunt> names)
	{
		return Collections.unmodifiableMap(
		  Collections.synchronizedMap(names));
	}

	protected Map<String, SelfShunt> buildShuntsNames(List<SelfShunt> shunts)
	{
		//~maps: shunt name -> number of instances with this name
		HashMap<String, Integer> n2i =
		  new HashMap<String, Integer>(1 + shunts.size()*10/100);

		//map: shunt index within the list -> original shunt name
		HashMap<Integer, String> s2n =
		  new HashMap<Integer, String>(shunts.size());

		//~: get original names
		for(int i = 0;(i < shunts.size());i++)
			s2n.put(i, shunts.get(i).getName());

		//map: this names to the number of occasions
		for(String name : s2n.values())
		{
			Integer i = n2i.get(name);
			n2i.put(name, (i == null)?(1):(i + 1));
		}

		//~: remove with 1 occasion
		for(Map.Entry<String,Integer> e : n2i.entrySet())
			if(((Integer)1).equals(e.getValue()))
				e.setValue(null);

		//~: the names
		ArrayList<String> nms = new ArrayList<String>(
		    Collections.nCopies(shunts.size(), (String)null));

		StringBuilder     nWi = new StringBuilder(32);

		//HINT: here we go in the reversed order to make units
		//      with the same name to have indices grow
		//      (from 1) moving forward the shunts list.

		for(int j = shunts.size() - 1;(j >= 0);j--)
		{
			String  n = s2n.get(j); //<-- [j] shunt name
			Integer i = n2i.get(n); //<-- name's occasions

			//?: {this name is NOT unique} append '#i' to the name
			if(i != null)
			{
				//!: decrement i in n2i  <-- that's why we
				//    <-- go from the shunts list bottom
				n2i.put(n, i - 1);

				n = nWi.
				  delete(0, nWi.length()).
				  append(n).append('#').append(i).
				  toString();
			}

			//!: save the built name to the result
			nms.set(j, n);
		}

		//~: create the resulting ordered map
		Map<String, SelfShunt> res =
		  new LinkedHashMap<String, SelfShunt>(shunts.size());

		for(int i = 0;(i < shunts.size());i++)
			res.put(nms.get(i), shunts.get(i));
		return res;
	}


	/* private: shunts mapping */

	private volatile Map<String, SelfShunt> shuntsNames;
}