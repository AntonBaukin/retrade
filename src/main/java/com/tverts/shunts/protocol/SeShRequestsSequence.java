package com.tverts.shunts.protocol;

/* standard Java classes */

import java.util.List;

/**
 * While it is not expected that a request
 * to self shunting service to be modified,
 * nobody told that it is not possible to
 * clone the original request before sending
 * it as the next one.
 *
 * Shunt requests sequence contains the list
 * of all the shunts are left to invoke.
 * The starting list is formed while handling
 * the initial shunting request.
 *
 * The self shunt key of this request is the
 * key of the shunt within the list. This
 * shunt must be invoked by the service.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      SeShRequestsSequence
       implements SeShRequest, Cloneable
{
	public static final long serialVersionUID = 0L;

	/* public: constructor */

	/**
	 * Creates the sequence with the copy of the
	 * shunt unit keys given.
	 */
	public SeShRequestsSequence(List shunts)
	{
		if(shunts == null)
			throw new IllegalArgumentException();
		this.shunts = shunts.toArray();
	}

	/* public: SeShRequest interface */

	public Object getSelfShuntKey()
	{
		return (position >= shunts.length)?(null)
		  :(shunts[position]);
	}

	/* public: SeShRequestsSequence interface */

	/**
	 * Moves the sequence to the next shunt.
	 *
	 * @return  {@code true} if the next shunt is available.
	 */
	public boolean gotoNextShunt()
	{
		return (++position < shunts.length);
	}

	/* public: Cloneable interface */

	/**
	 * Note that the shunt units keys array is
	 * not copied, but it is safe as it is for read only.
	 */
	public SeShRequestsSequence clone()
	{
		try
		{
			return (SeShRequestsSequence)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}

	/* protected: the sequence and the position */

	/**
	 * The read-only array of shunt units keys.
	 */
	protected final Object[] shunts;

	/**
	 * Current position within the shunts array.
	 */
	protected int            position;
}