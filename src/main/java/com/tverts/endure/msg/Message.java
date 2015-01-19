package com.tverts.endure.msg;

/* Java */

import java.util.Date;
import java.util.Map;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: api */

import com.tverts.api.support.CharAdapter;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Basic message data object for embedding
 * into {@link MsgObj} database instances.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "message")
@XmlType(name = "message", propOrder = {
  "time", "color", "title", "adapter", "data"
})
public class Message
{
	/* Message */

	@XmlAttribute
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getTime()
	{
		return time;
	}

	private Date time;

	public void setTime(Date time)
	{
		this.time = time;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(CharAdapter.class)
	public Character getColor()
	{
		return color;
	}

	private Character color = 'N';

	public void setColor(Character c)
	{
		this.color = EX.assertn(c);
	}

	public String getAdapter()
	{
		return adapter;
	}

	private String adapter;

	public void setAdapter(String adapter)
	{
		this.adapter = adapter;
	}

	public String getTitle()
	{
		return title;
	}

	private String title;

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Map<String, Object> getData()
	{
		return data;
	}

	private Map<String, Object> data;

	public void setData(Map<String, Object> data)
	{
		this.data = data;
	}
}