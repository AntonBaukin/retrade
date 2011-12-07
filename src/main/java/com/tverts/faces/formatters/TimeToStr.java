package com.tverts.faces.formatters;

/* standard Java classes */

import java.util.Date;

import com.tverts.support.DU;

/* com.tverts: support */


/**
 * Converter and formatter that supports format of
 * 24 hours time 'HH:mm'.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class TimeToStr extends ConverterFormatterBase<Date>
{
	public static final TimeToStr TIME2STR =
	  new TimeToStr();


	/* protected: formatting */

	protected void    format(Request<Date> request)
	{
		request.setString(DU.time2str(request.getValue()));
	}

	protected Class   getValueClass()
	{
		return Date.class;
	}


	/* protected: converting */

	protected void    convert(Request<Date> request)
	{
		request.setValue(DU.str2time(request.getString()));
	}
}