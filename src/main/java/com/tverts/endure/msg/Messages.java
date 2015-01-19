package com.tverts.endure.msg;

/* Java */

import java.util.Arrays;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionResult;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Static utilities for the Messages.
 *
 * @author anton.baukin@gmail.com.
 */
public class Messages
{
	/* Message Routines */

	public static MsgBoxObj box(Long login)
	{
		return EX.assertn(bean(GetMsg.class).findMsgBox(login),
		  "Auth Login [", login, "] has no Message Box created!"
		);
	}

	public static MsgLink   link(MsgBoxObj box, Long source, String type)
	{
		return actionResult(MsgLink.class, actionRun(ActMsgBox.LINK,
		  box, ActMsgBox.TYPE, type, ActMsgBox.SOURCE, source
		));
	}

	public static void      send(Long source, Message msg, String... types)
	{
		//?: {has no time}
		if(msg.getTime() == null)
			msg.setTime(new java.util.Date());

		//!: insert into the database
		bean(GetMsg.class).send(source, msg, Arrays.asList(types));
	}

	public static void      send(MsgBoxObj box, Message msg)
	{
		//?: {has no time}
		if(msg.getTime() == null)
			msg.setTime(new java.util.Date());

		//!: insert into the database
		bean(GetMsg.class).send(box, msg);
	}


	/* Send Simplifications */

	/**
	 * Sends Domain message for the Domain of the current user.
	 */
	public static void      domain(Message msg, String... types)
	{
		send(SecPoint.domain(), msg, types);
	}

	public static void      user(Message msg)
	{
		send(box(SecPoint.login()), msg);
	}

	public static void      umsg(char color, Object... txt)
	{
		Message msg = new Message();

		//=: message title
		msg.setTitle(SU.s2s(SU.cat(txt)));
		EX.assertn(msg.getTitle());

		//=: color
		msg.setColor(color);

		user(msg);
	}

	public static void      utext(Object... txt)
	{
		umsg('N', txt);
	}

	public static void      ugreen(Object... txt)
	{
		umsg('G', txt);
	}

	public static void      uorange(Object... txt)
	{
		umsg('O', txt);
	}

	public static void      ured(Object... txt)
	{
		umsg('R', txt);
	}
}