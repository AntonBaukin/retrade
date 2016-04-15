package com.tverts.endure.msg;

/**
 * Message Adapter that writes some data
 * into a message is being adapted.
 *
 * @author anton.baukin@gmail.com.
 */
public interface MsgAdapter
{
	/* Message Adapter */

	/**
	 * Adapts the message with the additional
	 * parameters (optional) given.
	 */
	public void adapt(Message msg, Object... params);
}
