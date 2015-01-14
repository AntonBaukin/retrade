package com.tverts.endure.msg;

/* Java */

import java.util.Map;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: api */

import com.tverts.api.support.CharAdapter;
import com.tverts.support.EX;


/**
 * Basic message data object for embedding
 * into {@link MsgObj} database instances.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "message")
@XmlType(name = "message", propOrder = {
  "login", "adapter", "title", "data"
})
public class Message
{
	/* Message */

	@XmlAttribute
	public Long getLogin()
	{
		return login;
	}

	private Long login;

	public void setLogin(Long login)
	{
		this.login = login;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(CharAdapter.class)
	public char getColor()
	{
		return color;
	}

	private char color = 'N';

	public void setColor(char c)
	{
		this.color = c;
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