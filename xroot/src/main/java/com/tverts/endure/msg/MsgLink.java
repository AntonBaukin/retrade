package com.tverts.endure.msg;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.Unity;


/**
 * This typed link subscribes a Message Box
 * to some Unity is being a messages destination.
 *
 * @author anton.baukin@gmail.com.
 */
public class MsgLink extends NumericBase
{
	/* Message Link */

	public Unity getSource()
	{
		return source;
	}

	private Unity source;

	public void setSource(Unity source)
	{
		this.source = source;
	}

	/**
	 * A type of a link. Only one triple
	 * (source, type, box) is allowed.
	 */
	public String getType()
	{
		return type;
	}

	private String type;

	public void setType(String type)
	{
		this.type = type;
	}

	public MsgBoxObj getMsgBox()
	{
		return msgBox;
	}

	private MsgBoxObj msgBox;

	public void setMsgBox(MsgBoxObj msgBox)
	{
		this.msgBox = msgBox;
	}
}