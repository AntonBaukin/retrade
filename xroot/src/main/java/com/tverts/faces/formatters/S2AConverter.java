package com.tverts.faces.formatters;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Converters String value to arrays of
 * strings using blanks as separators.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class S2AConverter extends ConverterFormatterBase<String[]>
{
	public static final S2AConverter S2A_CONVERTER =
	  new S2AConverter();


	/* protected: formatting */

	@SuppressWarnings("unchecked")
	protected void  format(Request<String[]> request)
	{
		Object v = request.getValue();

		if(v instanceof String)
			request.setString((String)v);
		if(v instanceof Object[])
			request.setString(SU.scats(" ", (Object[])request.getValue()));
	}

	protected Class getValueClass()
	{
		return String[].class;
	}


	/* protected: converting */

	protected void  convert(Request<String[]> request)
	{
		String[] s = SU.s2a(request.getString());
		if(s.length == 0) s = null;
		
		request.setValue(s);
	}
}