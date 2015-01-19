package com.tverts.endure.msg;

/* com.tverts: endure (core) */

import com.tverts.endure.OxNumericBase;
import com.tverts.endure.OxSearch;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * User message object stored in the database.
 *
 * @author anton.baukin@gmail.com.
 */
public class MsgObj extends OxNumericBase implements OxSearch
{
	/* Object Extraction */

	public Message getOx()
	{
		Message ox = (Message) super.getOx();
		if(ox == null) setOx(ox = new Message());
		return ox;
	}

	public void    setOx(Object ox)
	{
		EX.assertx(ox instanceof Message);
		super.setOx(ox);
	}

	public void    updateOx()
	{
		super.updateOx();

		Message m = this.getOx();

		//=: message color
		this.color    = m.getColor();

		//=: ox-search key words
		this.oxSearch = MsgAdapters.oxSearch(m);
	}


	/* Message Object */

	public MsgBoxObj getMsgBox()
	{
		return msgBox;
	}

	private MsgBoxObj msgBox;

	public void setMsgBox(MsgBoxObj msgBox)
	{
		this.msgBox = msgBox;
	}

	public Long getActive()
	{
		return active;
	}

	private Long active;

	public void setActive(Long active)
	{
		this.active = active;
	}

	public char getColor()
	{
		return color;
	}

	private char color;

	public void setColor(char color)
	{
		this.color = color;
	}

	public String getOxSearch()
	{
		return oxSearch;
	}

	private String oxSearch;

	public void setOxSearch(String oxSearch)
	{
		this.oxSearch = oxSearch;
	}
}