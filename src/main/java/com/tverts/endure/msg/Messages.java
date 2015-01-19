package com.tverts.endure.msg;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Static utilities for the Messages.
 *
 * @author anton.baukin@gmail.com.
 */
public class Messages
{
	public static MsgBoxObj box(Long login)
	{
		return EX.assertn(bean(GetMsg.class).getMsgBox(login),
		  "Auth Login [", login, "] has no Message Box created!"
		);
	}

}