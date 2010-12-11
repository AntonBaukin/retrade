package com.tverts.faces.formatters;

/* standart Java classes */

import java.util.Date;
import java.util.Calendar;

/* com.tverts: support */

import com.tverts.support.DU;

/**
 * @author anton baukin (abaukin@mail.ru)
 */
public class NamedDateTimeToStrRu extends DateTimeToStr
{
	public static final NamedDateTimeToStrRu
	  NAMED_DATETIME2STR_RU = new NamedDateTimeToStrRu();

	/* public: FormatterBase interface */

	public String format(Date value)
	{
		StringBuilder sb = new StringBuilder(48);
		Calendar      cl = Calendar.getInstance();

		DU.namedDateTimeToStrRu(value, sb, "&#160;", cl);
		sb.append("&#160;");
		DU.time2str(value, sb, cl);

		return sb.toString();
	}
}