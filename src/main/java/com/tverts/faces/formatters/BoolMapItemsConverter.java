package com.tverts.faces.formatters;

/* standard Java classes */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Converts string with comma-separated codes
 * into the map of boolean values and back.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   BoolMapItemsConverter
       extends ConverterFormatterBase<Map<String, Boolean>>
{
	public static final BoolMapItemsConverter
	  BOOL_MAP_ITEMS_CONVERTER = new BoolMapItemsConverter();


	/* protected: formatting */

	protected void  format(Request<Map<String, Boolean>> req)
	{
		Map<String, Boolean> v = req.getValue();
		if(v == null) v = Collections.emptyMap();

		StringBuilder        s = new StringBuilder(64);

		//c: for all TRUE items
		for(Map.Entry<String, Boolean> e : v.entrySet())
			if(Boolean.TRUE.equals(e.getValue()))
				s.append((s.length() != 0)?(","):("")).
				  append(e.getKey());

		//~: assign the result
		req.setString(s.toString());
	}

	protected Class getValueClass()
	{
		return Map.class;
	}


	/* protected: converting */

	protected void  convert(Request<Map<String, Boolean>> req)
	{
		String[]             s = SU.s2a(req.getString());
		Map<String, Boolean> v = req.getValue();

		//?: {has no map value}
		if(v == null)
			req.setValue(v = new HashMap<String, Boolean>(7));
		//c: for all items set them FALSE
		else for(Map.Entry<String, Boolean> e : v.entrySet())
			if(Boolean.TRUE.equals(e.getValue()))
				e.setValue(Boolean.FALSE);

		//c: set TRUE values
		for(String k : s)
			v.put(k, Boolean.TRUE);
	}

	protected void  convertEmptyString(Request<Map<String, Boolean>> req)
	{
		Map<String, Boolean> v = req.getValue();

		//?: {has no map value} set empty one
		if(v == null)
			req.setValue(new HashMap<String, Boolean>(1));
		else
			v.clear();
	}
}