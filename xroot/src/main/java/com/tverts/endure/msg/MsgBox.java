package com.tverts.endure.msg;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Data object embedded into a {@link MsgBoxObj}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "message-box")
@XmlType(name = "message-box")
public class MsgBox
{
	public int getTotal()
	{
		return total;
	}

	private int total;

	public void setTotal(int total)
	{
		this.total = total;
	}

	public int getRed()
	{
		return red;
	}

	private int red;

	public void setRed(int red)
	{
		this.red = red;
	}

	public int getOrange()
	{
		return orange;
	}

	private int orange;

	public void setOrange(int orange)
	{
		this.orange = orange;
	}

	public int getGreen()
	{
		return green;
	}

	private int green;

	public void setGreen(int green)
	{
		this.green = green;
	}
}