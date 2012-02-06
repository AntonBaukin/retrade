package com.tverts.faces.formatters;

/* standard Java classes */

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Converts string with comma-separated codes
 * into the Set of boolean values and back.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   BoolSetItemsConverter
       extends ConverterFormatterBase<Set<String>>
{
	public static final BoolSetItemsConverter
	  BOOL_SET_ITEMS_CONVERTER = new BoolSetItemsConverter();


	/* protected: formatting */

	protected void  format(Request<Set<String>> req)
	{
		Set<String>   v = req.getValue();
		if(v == null) v = Collections.emptySet();

		//~: assign the result
		req.setString(SU.cat(null, ",", v).toString());
	}

	protected Class getValueClass()
	{
		return Set.class;
	}


	/* protected: converting */

	protected void  convert(Request<Set<String>> req)
	{
		String[]    s = SU.s2a(req.getString());
		Set<String> v = req.getValue();

		//?: {has no Set value}
		if(v == null)
			req.setValue(v = new HashSet<String>(s.length));
		else //<-- clear it
			v.clear();

		//c: add the values present
		for(String k : s) v.add(k);
	}

	protected void  convertEmptyString(Request<Set<String>> req)
	{
		Set<String> v = req.getValue();

		//?: {has no Set value} set empty one
		if(v == null)
			req.setValue(new HashSet<String>(1));
		else
			v.clear();
	}
}