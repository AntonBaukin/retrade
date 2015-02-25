package com.tverts.endure.msg;

/* com.tverts: endure (core) */

import com.tverts.endure.OxNumericBase;
import com.tverts.endure.OxSearch;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * User message object stored in the database.
 *
 * Note that primary keys are used for select ordering,
 * and messages of test domains may not have negative keys!
 *
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
		Message m = this.getOx();

		//=: message time
		if(m.getTime() == null)
			m.setTime(new java.util.Date()); //<-- back update

		//=: message color
		this.color = m.getColor();

		//?: {green}
		if(color == 'G')
		{
			this.green = EX.assertn(getPrimaryKey());
			this.orangeRed = this.red = null;
		}
		//?: {orange}
		else if(color == 'O')
		{
			this.orangeRed = EX.assertn(getPrimaryKey());
			this.green = this.red = null;
		}
		//?: {red}
		else if(color == 'R')
		{
			this.orangeRed = this.red = EX.assertn(getPrimaryKey());
			this.green = null;
		}
		else if(color != 'N')
			throw EX.state("Wrong message color: [", color, "]!");

		//=: ox-search key words
		this.oxSearch = MsgAdapters.oxSearch(m);

		//~: as the last operation...
		super.updateOx();
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


	/* Special Indices */

	/**
	 * Assigned to the primary key when the message is green.
	 */
	public Long getGreen()
	{
		return green;
	}

	private Long green;

	public void setGreen(Long green)
	{
		this.green = green;
	}

	/**
	 * Assigned to the primary key when
	 * the message is orange or red.
	 */
	public Long getOrangeRed()
	{
		return orangeRed;
	}

	private Long orangeRed;

	public void setOrangeRed(Long orangeRed)
	{
		this.orangeRed = orangeRed;
	}

	/**
	 * Assigned to the primary key when the message is red.
	 */
	public Long getRed()
	{
		return red;
	}

	private Long red;

	public void setRed(Long red)
	{
		this.red = red;
	}
}