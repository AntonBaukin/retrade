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

/* com.tverts: endure (auth, core) */

import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.core.DomainEntity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Messages processing utility with methods chaining.
 *
 * @author anton.baukin@gmail.com.
 */
public class Msg
{
	/* public: static constructors */

	/**
	 * Creates Message builder with the defaults.
	 */
	public static Msg create()
	{
		return new Msg(new Message()).defaults();
	}

	public static Msg create(Message msg)
	{
		return new Msg(msg);
	}

	/**
	 * Build target.
	 */
	public final Message msg;

	protected Msg(Message msg)
	{
		this.msg = EX.assertn(msg);
	}


	/* Initialization Routines */

	public Msg defaults()
	{
		//?: {has no time}
		if(msg.getTime() == null)
			msg.setTime(new java.util.Date());

		//?: {has no color}
		if(msg.getColor() == null)
			msg.setColor('N');

		return this;
	}

	public Msg title(Object... txt)
	{
		//=: message title
		msg.setTitle(SU.s2s(SU.cat(txt)));
		EX.assertn(msg.getTitle(), "Message text is undefined!");

		return this;
	}

	public Msg time(java.util.Date time)
	{
		//=: message time
		msg.setTime(EX.assertn(time));
		return this;
	}

	public Msg color(char c)
	{
		//=: message color
		EX.assertx(c == 'N' || c == 'R' || c == 'G' || c == 'O');
		msg.setColor(c);

		return this;
	}

	public Msg green()
	{
		return color('G');
	}

	public Msg red()
	{
		return color('R');
	}

	public Msg orange()
	{
		return color('O');
	}


	/* Source-Wide Send Routines */

	/**
	 * Tunes this builder to send message to source-wide boxes.
	 * Note that you must assign the types of the Message Links
	 * traces with {@link #types(String...)}.
	 */
	public Msg  source(Long source)
	{
		this.source = source;
		return this;
	}

	private Long source;

	/**
	 * Assigns the source as Domain of the executing user.
	 */
	public Msg  domain()
	{
		return source(SecPoint.domain());
	}

	public Msg  domain(DomainEntity de)
	{
		return source(de.getDomain().getPrimaryKey());
	}

	public Msg  types(String... types)
	{
		EX.asserte(types);
		for(String type : types)
			EX.asserts(type);
		this.types = types;
		return this;
	}

	private String[] types;

	/**
	 * Send execution. This is chain terminator.
	 */
	public void send()
	{
		EX.assertx((box != null) | (source != null),
		  "Either message send source, not message box are specified!"
		);

		//?: {send to the direct box}
		if(box != null)
		{
			//!: insert into the database
			bean(GetMsg.class).send(box, msg);
		}

		//?: {send to the source-wide}
		if(source != null)
		{
			EX.asserte(types, "No message links types are assigned!");

			//!: insert into the database
			bean(GetMsg.class).send(source, msg, Arrays.asList(types));
		}
	}


	/* Message Box Send Routines */

	/**
	 * Sends message to the box directly assigned.
	 */
	public Msg  box(MsgBoxObj box)
	{
		this.box = box;
		return this;
	}

	private MsgBoxObj box;

	/**
	 * Sends message to the box of the executing user.
	 */
	public Msg  user()
	{
		return box(bean(GetMsg.class).msgBox(SecPoint.login()));
	}

	public Msg  user(AuthLogin login)
	{
		return box(bean(GetMsg.class).msgBox(login.getPrimaryKey()));
	}


	/* Static Routines */

	public static MsgLink link(MsgBoxObj box, Long source, String type)
	{
		return actionResult(MsgLink.class, actionRun(ActMsgBox.LINK,
		  box, ActMsgBox.TYPE, type, ActMsgBox.SOURCE, source
		));
	}

	public static MsgLink link(MsgBoxObj box, DomainEntity domain, String type)
	{
		return link(box, domain.getDomain().getPrimaryKey(), type);
	}
}