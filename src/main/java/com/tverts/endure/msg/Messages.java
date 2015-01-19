package com.tverts.endure.msg;

/* Java */

import java.util.Arrays;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionResult;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: support */

import com.tverts.support.EX;


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
}