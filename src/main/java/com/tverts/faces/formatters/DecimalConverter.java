package com.tverts.faces.formatters;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: support */

import com.tverts.support.CMP;


/**
 * Formats and converts {@link BigDecimal}
 * values having trailing zeros trimmed to
 * one decimal '.00'.
 *
 * @author anton.baukin@gmail.com
 */
public class   DecimalConverter
       extends ConverterFormatterBase<BigDecimal>
{
	public static final DecimalConverter INSTANCE =
	  new DecimalConverter();


	/* protected: ConverterFormatterBase interface */

	protected Class getValueClass()
	{
		return BigDecimal.class;
	}

	public String   format(BigDecimal d)
	{
		//~: strip trailing zeros
		if(CMP.eqZero(d))
			d = BigDecimal.ZERO;

		if(d.scale() < 0)
			d = d.setScale(0);

		return d.toString();
	}

	protected void  format(Request<BigDecimal> request)
	{
		request.setString(format(request.getValue()));
	}

	protected void  convert(Request<BigDecimal> request)
	{
		BigDecimal d = new BigDecimal(request.getString());

		//~: strip trailing zeros
		d = d.stripTrailingZeros();

		//?: {has no '.0'} add them
		if(d.scale() < 1)
			d = d.setScale(1);

		request.setValue(d);
	}
}