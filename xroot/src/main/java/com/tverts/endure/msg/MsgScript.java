package com.tverts.endure.msg;

/**
 * Adapting interface of a formatter of
 * JavaScript associated with the message.
 *
 * @author anton.baukin@gmail.com
 */
public interface MsgScript
{
	/* Message Script */

	public String makeScript(Message msg);
}