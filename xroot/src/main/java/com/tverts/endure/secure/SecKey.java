package com.tverts.endure.secure;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;


/**
 * Security Key is a name of some action.
 * The name is unique within the system.
 * Keys are not domain-related objects.
 *
 * @author anton.baukin@gmail.com
 */
public class SecKey extends NumericBase
{
	/* public: SecKey (bean) interface */

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	/* the name */

	private String name;
}