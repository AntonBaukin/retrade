package com.tverts.api;

/* standard Java classes */

import java.io.Serializable;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Server-side execution error implemented as
 * XML-serializable Java Bean.
 */
@XmlRootElement(name = "exec-error")
public class ExecError implements Serializable
{
	public static final long serialVersionUID = 0L;

	public ExecError()
	{}

	public ExecError(String text)
	{
		this.text = text;
	}


	/* public: ExecError interface */

	@XmlElement(name = "text")
	public String getText()
	{
		return text;
	}

	public void   setText(String text)
	{
		this.text = text;
	}


	/* text */

	private String text;
}
