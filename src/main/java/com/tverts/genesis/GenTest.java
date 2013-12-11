package com.tverts.genesis;

/**
 * Strategy of testing an logging a genesis results.
 *
 * Note that strategy is not allowed to generate
 * random values from the context!
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface GenTest
{
	/* public: GenTest interface */

	public void testGenesis(GenCtx ctx);
}
