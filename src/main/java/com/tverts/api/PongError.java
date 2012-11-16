package com.tverts.api;

/**
 * Exception thrown when the server returns
 * request execution error in the {@link Pong}
 * resulting object.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class PongError extends RuntimeException
{
	/* public: constructor */

	public PongError(Ping ping, Pong pong)
	{
		super(pong.getError());

		this.ping = ping;
		this.pong = pong;
	}


	/* public: PongError interface */

	public Ping getPing()
	{
		return ping;
	}

	public Pong getPong()
	{
		return pong;
	}


	/* private: ping & pong */

	private Ping ping;
	private Pong pong;
}
