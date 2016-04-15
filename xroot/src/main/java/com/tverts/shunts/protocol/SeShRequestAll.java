package com.tverts.shunts.protocol;

/**
 * This request to the shunts invocation mechanism
 * tells to run all the tests discovered in the system.
 *
 * @author anton.baukin@gmail.com
 */
public class      SeShRequestAll
       implements SeShRequestInitial
{
	public static final long serialVersionUID = 0L;


	/* public: SeShRequest interface */

	/**
	 * Returns the simple of this class
	 * {@link SeShRequestAll}.
	 */
	public Object getSelfShuntKey()
	{
		return SeShRequestAll.class.getSimpleName();
	}

	public String getContextUID()
	{
		return contextUID;
	}

	public void   setContextUID(String contextUID)
	{
		this.contextUID = contextUID;
	}


	/* private: the state of the protocol */

	private String contextUID;
}