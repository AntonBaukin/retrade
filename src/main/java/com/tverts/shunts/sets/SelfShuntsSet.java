package com.tverts.shunts.sets;

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
 * shunt (ususlly, the shunt class simple name,
 * or a name defined by the annotation), that
 * may be combined with the string to make the
 * name unique (some sort of index).
 *
 * A set instance must be thread-safe.
 *
 * @author anton baukin (abaukin@mail.ru)
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