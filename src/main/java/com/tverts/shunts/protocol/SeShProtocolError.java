package com.tverts.shunts.protocol;

/**
 * An exception that had occurred while running
 * the shunt protocol.
 *
 * Note that this exception is of that system
 * errors that are generated by the responding
 * side while executing the shunts.
 *
 * @author anton.baukin@gmail.com
 */
public class   SeShProtocolError
       extends Exception
{
	/* public: constructors */

	public SeShProtocolError()
	{}

	public SeShProtocolError(String message)
	{
		super(message);
	}

	public SeShProtocolError(Throwable cause)
	{
		super(cause);
	}

	public SeShProtocolError(String message, Throwable cause)
	{
		super(message, cause);
	}
}