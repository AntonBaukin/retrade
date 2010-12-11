package com.tverts.faces.formatters;

/* standart Java classes */

import java.util.Date;

/* com.tverts: support */

import com.tverts.support.DU;

/**
 * @author anton baukin (abaukin@mail.ru)
 */
public class DateToStr extends FormatterBase<Date>
{
	public static final DateToStr DATE2STR =
	  new DateToStr();

	/* public: FormatterBase interface */

	public Class  getExpectedClass()
	{
		return Date.class;
	}

	public String format(Date value)
	{
		return DU.date2str(value);
	}
}