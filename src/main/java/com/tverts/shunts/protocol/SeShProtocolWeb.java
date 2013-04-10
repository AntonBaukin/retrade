package com.tverts.shunts.protocol;

/**
 * Multi Web Protocol allows to send any
 * {@link SeShRequestInitial} provided.
 *
 * Note that it is safe to send the same
 * request instance more than one time
 * as requests are not modified.
 *
 *
 * @author anton.baukin@gmail.com
 */
public final class SeShProtocolWeb
       extends     SeShProtocolWebBase
{
	/* public: constructor */

	public SeShProtocolWeb(SeShRequestInitial request)
	{
		if(request == null)
			throw new IllegalArgumentException();

		this.initialRequest = request;
	}


	/* protected: SeShProtocolBase interface */

	protected SeShRequestInitial createInitialRequest()
	{
		return initialRequest;
	}


	/* private: initial request */

	private SeShRequestInitial initialRequest;
}