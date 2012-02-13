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

	protected void  format(Request<String[]> request)
	{
		request.setString(SU.cat(null, " ",
		  request.getValue()).toString());
	}

	protected Class getValueClass()
	{
		return String[].class;
	}


	/* protected: converting */

	protected void  convert(Request<String[]> request)
	{
		String[] s = SU.s2aws(request.getString());
		if(s.length == 0) s = null;
		
		request.setValue(s);
	}
}