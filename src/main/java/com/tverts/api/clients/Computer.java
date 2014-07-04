package com.tverts.api.clients;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;


/**
 * Data about a Computer
 * registered in the system.
 */
@XmlRootElement(name = "computer")
@XmlType(name = "computer",
  propOrder = { "comment" }
)
public class Computer extends CatItem
{
	@XmlElement(name = "comment")
	public String getComment()
	{
		return comment;
	}

	private String comment;

	public void setComment(String comment)
	{
		this.comment = comment;
	}
}