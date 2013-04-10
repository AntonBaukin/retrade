package com.tverts.shunts.protocol;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntCtx;
import com.tverts.shunts.SelfShuntReport;


/**
 * Self shunting protocol abstracts the tasks
 * of sending Self Shunt Requests to the server
 * (this server) and collecting the responses.
 *
 * The transport of and conversation state of
 * invoking the shunts sequence are aggregated
 * in the protocol.
 *
 * Protocol is a stateful object. It is not designed
 * to be thread save. It must be run within a single
 * thread only.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SeShProtocol
{
	/* public: protocol interface */

	/**
	 * Makes the protocol to be ready to
	 * send the requests to the opposite side.
	 *
	 * The first (initial) request is made when
	 * opening the protocol.
	 *
	 * Only the protocol processing thread may
	 * call this method.
	 */
	public void            openProtocol(SelfShuntCtx ctx)
	  throws SeShProtocolError, InterruptedException;

	/**
	 * Sends next shunt request to the service.
	 * This method is invoked in a cycle that
	 * is breaked when the result becomes
	 * {@code false}. The cause of the break
	 * may be either a system error, a critical
	 * shunt error, or just finishing the shunting.
	 *
	 * Only the protocol processing thread may
	 * call this method.
	 */
	public boolean         sendNextRequest()
	  throws SeShProtocolError, InterruptedException;

	/**
	 * Closes the conversation returning the
	 * accumulated reports of the invocation.
	 *
	 * Only the protocol processing thread may
	 * call this method.
	 */
	public SelfShuntReport closeProtocol()
	  throws SeShProtocolError, InterruptedException;

	/**
	 * Interrupts the activity of the protocol.
	 * Drives no effect on a protocol already closed.
	 */
	public void            interruptProtocol()
	  throws SeShProtocolError;

	/**
	 * The (last) system error of the client side
	 * that was saved while executing the protocol.
	 */
	public Throwable       getSystemError();
}