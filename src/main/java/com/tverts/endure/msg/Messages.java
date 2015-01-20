package com.tverts.endure.msg;

/* Java */

import java.util.Arrays;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionResult;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: secure */

import com.tverts.endure.core.DomainEntity;
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
	 * Sends Domain message for the domain specified.
	 */
	public static void domain(Long domain, Message msg, String... types)
	{
		if(domain == null)
			domain = SecPoint.domain();

		send(domain, msg, types);
	}

	/**
	 * Sends Domain message for the domain of the current user.
	 */
	public static void domain(Message msg, String... types)
	{
		send(SecPoint.domain(), msg, types);
	}

	public static void msg(Long domain, char color, String type, Object... txt)
	{
		Message msg = new Message();

		//=: message title
		msg.setTitle(SU.s2s(SU.cat(txt)));
		EX.assertn(msg.getTitle());

		//=: color
		msg.setColor(color);

		domain(domain, msg, type);
	}

	public static void msg(String type, Object... txt)
	{
		msg(null, 'N', type, txt);
	}

	public static void green(String type, Object... txt)
	{
		msg(null, 'G', type, txt);
	}

	public static void orange(String type, Object... txt)
	{
		msg(null, 'O', type, txt);
	}

	public static void red(String type, Object... txt)
	{
		msg(null, 'R', type, txt);
	}

	public static void msg(DomainEntity de, String type, Object... txt)
	{
		msg(de.getDomain().getPrimaryKey(), 'N', type, txt);
	}

	public static void green(DomainEntity de, String type, Object... txt)
	{
		msg(de.getDomain().getPrimaryKey(), 'G', type, txt);
	}

	public static void orange(DomainEntity de, String type, Object... txt)
	{
		msg(de.getDomain().getPrimaryKey(), 'O', type, txt);
	}

	public static void red(DomainEntity de, String type, Object... txt)
	{
		msg(de.getDomain().getPrimaryKey(), 'R', type, txt);
	}

	/**
	 * Sends the message to the given user.
	 */
	public static void user(Long login, Message msg)
	{
		if(login == null)
			login = SecPoint.login();

		send(box(login), msg);
	}

	/**
	 * Sends the message to the current user.
	 */
	public static void user(Message msg)
	{
		send(null, msg);
	}

	public static void umsg(Long login, char color, Object... txt)
	{
		Message msg = new Message();

		//=: message title
		msg.setTitle(SU.s2s(SU.cat(txt)));
		EX.assertn(msg.getTitle());

		//=: color
		msg.setColor(color);

		user(login, msg);
	}

	public static void utext(Object... txt)
	{
		umsg(null, 'N', txt);
	}

	public static void ugreen(Object... txt)
	{
		umsg(null, 'G', txt);
	}

	public static void uorange(Object... txt)
	{
		umsg(null, 'O', txt);
	}

	public static void ured(Object... txt)
	{
		umsg(null, 'R', txt);
	}

	public static void utext(Long login, Object... txt)
	{
		umsg(login, 'N', txt);
	}

	public static void ugreen(Long login, Object... txt)
	{
		umsg(login, 'G', txt);
	}

	public static void uorange(Long login, Object... txt)
	{
		umsg(login, 'O', txt);
	}

	public static void ured(Long login, Object... txt)
	{
		umsg(login, 'R', txt);
	}
}