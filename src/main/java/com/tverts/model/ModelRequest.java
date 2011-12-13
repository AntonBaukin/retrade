package com.tverts.model;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * While model contains all the parameters of
 * interaction, and model data contains all
 * the state inferred from that parameters,
 * model request single instance stores the
 * runtime-defined request key telling what
 * part of the state data is needed for the
 * pending request.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ModelRequest
{
	/* ModelRequest Singleton */

	public static ModelRequest getInstance()
	{
		return INSTANCE;
	}

	private static final ModelRequest INSTANCE =
	  new ModelRequest();

	protected ModelRequest()
	{}


	/* public: ModelRequest interface */

	/**
	 * The key of the model request is thread-bound
	 * string value. It is always defined, and empty
	 * string by default.
	 */
	public String getKey()
	{
		String key = s2s(this.key.get());
		return (key == null)?(""):(key);
	}
	
	public void   setKey(String key)
	{
		if((key = s2s(key)) == null)
			this.key.remove();
		else
			this.key.set(key);
	}

	public void   clear()
	{
		this.setKey(null);
	}


	/* public: ModelRequest (support) interface */

	public static boolean isKey(String key)
	{
		if((key = s2s(key)) == null) key = "";
		return getInstance().getKey().equals(key);
	}


	/* private: thread local state */

	private volatile ThreadLocal<String> key;
}