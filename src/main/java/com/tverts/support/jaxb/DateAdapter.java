package com.tverts.support.jaxb;

/* standard Java classes */

import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.adapters.XmlAdapter;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Used in JAXB annotation to format date
 * values as 'DD:MM:YYYY'.
 *
 * @author anton.baukin@gmail.com
 */
public final class DateAdapter
       extends     XmlAdapter<String, Date>
{
	/* public: XmlAdapter interface */

	public String marshal(Date v)
	{
		return DU.date2str(v);
	}

	public Date   unmarshal(String v)
	  throws Exception
	{
		return DU.str2date(v);
	}
}