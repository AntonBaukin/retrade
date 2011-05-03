package com.tverts.faces.formatters;

/* standard Java classes */

import java.util.Calendar;
import java.util.Date;

/* com.tverts: support */

import com.tverts.support.DU;

/**
 * @author anton.baukin@gmail.com
 */
public class DateTimeToStr extends DateToStr
{
	public static final DateTimeToStr DATETIME2STR =
	  new DateTimeToStr();

	/* public: FormatterBase interface */

	public String format(Date value)
	{
		StringBuilder sb = new StringBuilder(18);
		Calendar      cl = Calendar.getInstance();

		DU.date2str(value, sb, cl);
		sb.append("&#160;");
		DU.time2str(value, sb, cl);
		
		return sb.toString();
	}
}