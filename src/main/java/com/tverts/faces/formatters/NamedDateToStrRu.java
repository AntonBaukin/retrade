package com.tverts.faces.formatters;

/* standart Java classes */

import java.util.Calendar;
import java.util.Date;

/* com.tverts: support */

import com.tverts.support.DU;

/**
 * @author anton baukin (abaukin@mail.ru)
 */
public class NamedDateToStrRu extends DateTimeToStr
{
	public static final NamedDateToStrRu
	  NAMED_DATE2STR_RU = new NamedDateToStrRu();

	/* public: FormatterBase interface */

	public String format(Date value)
	{
		StringBuilder sb = new StringBuilder(48);
		Calendar      cl = Calendar.getInstance();

		DU.namedDateTimeToStrRu(value, sb, "&#160;", cl);
		return sb.toString();
	}
}