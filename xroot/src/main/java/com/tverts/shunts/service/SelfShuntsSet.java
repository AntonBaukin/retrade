package com.tverts.shunts.service;

/* standard Java classes */

import java.util.List;
import java.util.Set;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShunt;

/**
 * Represents an ordered set of Self Shunts.
 *
 * Each shunt unit instance has a set-unique key
 * that is formed of a key provided by the
 * shunt (usually, the shunt class simple name,
 * or a name defined by the annotation), that
 * may be combined with the string to make the
 * name unique (some sort of index).
 *
 * A set instance must be thread-safe.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SelfShuntsSet
{
	/* public: SelfShuntsSet interface */

	/**
	 * Returns the ordered set of all the keys
	 * of the shunts registered in the set.
	 *
	 * The set returned is read-only.
	 */
	public Set<String>     enumShunts();

	/**
	 * Returns the ordered set of all the keys
	 * of the shunts registered in the set
	 * having at least one of the groups provided.
	 */
	public Set<String>     enumShuntsByGroups(String... groups);

	/**
	 * Returns all the shunts registered having
	 * the name provided. Note that it is possible
	 * to have more than one shunt unit with the
	 * same name, but they would be registered with
	 * different keys in the key.
	 */
	public Set<String>     enumShuntsByName(String name);

	/**
	 * Returns the shunt registered by the key,
	 * or {@code null} value.
	 */
	public SelfShunt       getShunt(String key);

	/**
	 * Gives all the shunts registered
	 * in the proper order.
	 *
	 * The list returned is read-only.
	 */
	public List<SelfShunt> listShunts();
}