package com.tverts.shunts.protocol;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;

/**
 * Self shunting protocol abstracts the tasks
 * of sending Self Shunt Requests to the server
 * (mean this server) and collecting the responses.
 *
 * The transport of and conversation state of
 * invoking the shunts sequence are aggregated
 * in the protocol.
 *
 * Protocol is a stateful object. It is not designed
 * to be thread save. It must be run within a single
 * thread only.
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
	 * Only the protocol invocating thread may
	 * call this method.
	 */
	public void            openProtocol()
	  throws SeShProtocolError, InterruptedException;

	/**
	 * Sends next shunt request to the service.
	 * This method is invoked in a cycle that
	 * is breaked when the result becomes
	 * {@code false}. The cause of the break
	 * may be either a system error, a critical
	 * shunt error, or just finishing the shunting.
	 *
	 * Only the protocol invocating thread may
	 * call this method.
	 */
	public boolean         sendNextRequest()
	  throws SeShProtocolError, InterruptedException;

	/**
	 * Closes the conversation returning the
	 * accumulated reports of the invocation.
	 *
	 * Only the protocol invocating thread may
	 * call this method.
	 */
	public SelfShuntReport closeProtocol()
	  throws SeShProtocolError, InterruptedException;

	/**
	 * This method is always invoked when processing
	 * the protocol dependless of whether is was
	 * successfully opened and (or) closed or not.
	 * It is done after {@link #closeProtocol()} when
	 * it is opened, or after {@link #openProtocol()}
	 * having error raised.
	 *
	 * The report argument is always defined. It has
	 * the shunt report when the protocol was closed,
	 * and it has only the system error value otherwise.
	 *
	 * This method is primary indended to notify the
	 * components of the system waiting the shunts
	 * results to come. This call may resume the
	 * threads waiting.
	 */
	public void            finishProtocol(SelfShuntReport report);

	/**
	 * Interrupts the activity of the protocol.
	 * Drives no effect on a protocol already closed.
	 *
	 * This call may be done only from a thread being
	 * NOT a thread invocating the protocol.
	 */
	public void            interruptProtocol()
	  throws SeShProtocolError;

	/**
	 * The (last) system error of the client side
	 * that was saved while executing the protocol.
	 */
	public Throwable       getSystemError();
}