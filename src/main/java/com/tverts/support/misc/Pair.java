package com.tverts.support.misc;

/* standard Java classes */

import java.util.Map.Entry;


/**
 * A pair of two keys, or of a key and a value.
 *
 * @author anton.baukin@gmail.com
 */
public final class Pair<K, V> implements Entry<K, V>
{
	/* public: constructors */

	public Pair(K k, V v)
	{
		this.k = k;
		this.v = v;
	}

	public Pair()
	{}


	/* public: Entry interface */

	public K getKey()
	{
		return k;
	}

	public K setKey(K k)
	{
		K r = this.k; this.k = k;
		return r;
	}

	public V getValue()
	{
		return v;
	}

	public V setValue(V v)
	{
		V r = this.v; this.v = v;
		return r;
	}


	/* public: Object interface */

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof Entry)) return false;

		Entry p = (Entry)o;

		if((k != null)?(!k.equals(p.getKey())):(p.getKey() != null))
			return false;

		if((v != null)?(!v.equals(p.getValue())):(p.getValue() != null))
			return false;

		return true;
	}

	public int     hashCode()
	{
		return 31*((k != null)?(k.hashCode()):0) +
		  ((v != null)?(v.hashCode()):0);
	}


	/* private: keys */

	private K k;
	private V v;
}