package com.tverts.support.jaxb;

/* standard Java classes */

import java.text.SimpleDateFormat;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Used in JAXB annotation to format timestamp
 * values as 'DD:MM:YYYY HH:mm:ss.SSS'.
 *
 * @author anton.baukin@gmail.com
 */
public final class TimestampAdapter
       extends     XmlAdapter<String, Date>
{
	/* public: XmlAdapter interface */

	public String marshal(Date v)
	{
		return f.format(v);
	}

	public Date   unmarshal(String v)
	  throws Exception
	{
		return f.parse(v);
	}


	/* private: the formatter used */

	private final SimpleDateFormat f =
	  new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
}