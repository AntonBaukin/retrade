package com.tverts.faces.formatters;

/* standard Java classes */

import java.util.Collections;
import java.util.Map;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.escapeJSString;


/**
 * Translates given strings map into JSON array
 * object with enclosing '[' and ']'. Each key
 * of the map has even index, and the value has
 * odd +1 index.
 *
 * We do not translate into real JSON map as
 * no keys order is preserved by the standard.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   JSONStringsMapListFormatter
       extends ConverterFormatterBase<Map>
{
	public static final JSONStringsMapListFormatter
	  JSON_STRINGS_MAP_LIST_FORMATTER = new JSONStringsMapListFormatter();


	/* protected: formatting */

	@SuppressWarnings("unchecked")
	protected void  format(Request<Map> req)
	{
		Map<Object, Object> v = req.getValue();
		if(v == null) v = Collections.emptyMap();

		StringBuilder       s = new StringBuilder(64);
		s.append('[');

		//c: for all map items
		for(Map.Entry e : v.entrySet()) if(e.getKey() != null)
		{
			//~: check the key
			String k = s2s(e.getKey().toString());
			if(k == null) continue;

			//~: get the value
			String x = (e.getValue() == null)?(""):
			  s2s(e.getValue().toString());
			if(x == null) x = "";

			//~: ', '
			if(s.length() != 1) s.append(", ");

			//~: write key
			s.append('\'').append(escapeJSString(k)).append('\'');

			//~: ', '
			s.append(", ");

			//~: write value
			s.append('\'').append(escapeJSString(x)).append('\'');
		}

		s.append(']');

		//~: assign the result
		req.setString(s.toString());
	}

	protected Class getValueClass()
	{
		return Map.class;
	}

}