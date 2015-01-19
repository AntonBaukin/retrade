package com.tverts.endure.msg;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;


/**
 * Get queries for the database relate to the Messages.
 *
 * @author anton.baukin@gmail.com.
 */
public class GetMsg extends GetObjectBase
{
	/* Message Boxes and Links */

	public MsgBoxObj getMsgBox(Long login)
	{
		final String Q =
"from MsgBoxObj where (login.id = :login)";

		return object(MsgBoxObj.class, Q, "login", login);
	}

	/**
	 * Creates the link of the type given between the message
	 * box and the user (login) if it absents and returns true.
	 * If the link does present, returns false.
	 */
	public Long      getBoxLink(Long box, Long source, String type)
	{
/*

 select id from MsgLink where (msgBox.id = :box) and
   (source.id = :source) and (type = :type)

 */

		final String Q =

"select id from MsgLink where (msgBox.id = :box) and\n" +
"  (source.id = :source) and (type = :type)";

		return object(Long.class, Q, "box", box, "source", source, "type", type);
	}
}