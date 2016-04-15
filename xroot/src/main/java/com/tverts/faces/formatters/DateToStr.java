package com.tverts.faces.formatters;

/* standard Java classes */

import java.util.Date;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Converter and formatter that supports standard
 * format of date 'DD.MM.YYYY'.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class DateToStr extends ConverterFormatterBase<Date>
{
	public static final DateToStr DATE2STR =
	  new DateToStr();


	/* protected: formatting */

	protected void    format(Request<Date> request)
	{
		request.setString(DU.date2str(request.getValue()));
	}

	protected Class   getValueClass()
	{
		return Date.class;
	}


	/* protected: converting */

	protected void    convert(Request<Date> request)
	{
		request.setValue(DU.str2date(request.getString()));
	}
}