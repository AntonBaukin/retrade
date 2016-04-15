package com.tverts.faces.formatters;

/* standard Java classes */

import java.util.Date;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Formatter that supports standard
 * format of timestamp 'DD.MM.YYYY hh:mm'.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class DateTimeToStr extends ConverterFormatterBase<Date>
{
	public static final DateTimeToStr DATETIME2STR =
	  new DateTimeToStr();


	/* protected: formatting */

	protected void    format(Request<Date> request)
	{
		request.setString(DU.datetime2str(request.getValue()));
	}

	protected Class   getValueClass()
	{
		return Date.class;
	}
}