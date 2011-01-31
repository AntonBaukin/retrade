package com.tverts.shunts.protocol;

/**
 * This protocol implementation runs all the tests
 * registered in the system.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SeShProtocolWebAll
       extends SeShProtocolWebBase
{
	/* protected: SeShProtocolBase interface */

	protected SeShRequestInitial createInitialRequest()
	{
		return new SeShRequestAll();
	}
}