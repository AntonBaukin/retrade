package com.tverts.support.jaxb;

/* standard Java classes */

import java.util.Calendar;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.adapters.XmlAdapter;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Used in JAXB annotation to format timestamp
 * values as 'DD:MM:YYYY HH:mm'.
 *
 * @author anton.baukin@gmail.com
 */
public final class DateTimeAdapter
       extends     XmlAdapter<String, Date>
{
	/* public: XmlAdapter interface */

	public String marshal(Date v)
	{
		StringBuilder sb = new StringBuilder(16);
		Calendar      cl = Calendar.getInstance();
		cl.setTime(v);

		//~: date
		DU.date2str(sb, cl);
		sb.append(' ');

		//~: time
		DU.time2str(sb, cl);

		return sb.toString();
	}

	public Date   unmarshal(String v)
	  throws Exception
	{
		Calendar cl = Calendar.getInstance();
		cl.clear();

		//~: date
		DU.str2date(v, cl);

		//~: time
		int i = v.indexOf(' ');
		if(i != -1)
			DU.str2time(v.substring(i), cl);

		return cl.getTime();
	}
}