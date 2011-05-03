package com.tverts.faces.formatters;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: support */

import com.tverts.support.SU;

/**
 * @author anton.baukin@gmail.com
 */
public class   CurrencyFormatter 
       extends FormatterBase<BigDecimal>
{
	public static final CurrencyFormatter
	  CURRENCY2STR = new CurrencyFormatter();

	/* public: FormatterBase interface */

	public Class  getExpectedClass()
	{
		return BigDecimal.class;
	}

	public String format(BigDecimal value)
	{
		StringBuilder sb = new StringBuilder(8);
		SU.formatCurrency(value, null, sb);
		return sb.toString();
	}
}