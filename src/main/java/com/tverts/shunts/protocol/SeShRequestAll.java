package com.tverts.shunts.protocol;

/**
 * This request to the shunts invocation mechanism
 * tells to run all the tests discovered in the system
 * and allowed to run.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public final class SeShRequestAll
       implements  SeShRequestInitial
{
	public static final long serialVersionUID = 0L;

	/* public: SeShRequest interface */

	/**
	 * Returns the FQN of this class
	 * {@link SeShRequestAll}.
	 */
	public Object getSelfShuntKey()
	{
		return SeShRequestAll.class.getName();
	}
}