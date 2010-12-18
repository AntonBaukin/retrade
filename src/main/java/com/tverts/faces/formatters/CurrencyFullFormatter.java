package com.tverts.faces.formatters;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: support */

import com.tverts.support.SU;

/**
 * @author anton baukin (abaukin@mail.ru)
 */
public class   CurrencyFullFormatter
       extends CurrencyFormatter
{
	public static final CurrencyFullFormatter
	  CURRENCY2STR_FULL = new CurrencyFullFormatter();

	/* public: FormatterBase interface */

	public String format(BigDecimal value)
	{
		StringBuilder sb = new StringBuilder(48);

		sb.append("<span style='white-space:nowrap;'>");
		SU.formatCurrency(value, "<span class='trips-csep'/>", sb);
		sb.append("</span>");

		return sb.toString();
	}
}