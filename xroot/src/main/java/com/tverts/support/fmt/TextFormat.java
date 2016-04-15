package com.tverts.support.fmt;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Implementation replaces $0, $1, ...
 * strings with the parameters given.
 * (No escaping is currently supported.)
 *
 * When $i is not a Character Sequence,
 * invokes toString() method.
 *
 * Undefined values are empty strings.
 * The implementation is thread-safe.
 *
 *
 * @author anton.baukin@gmail.com
 */
public final class TextFormat
{
	/* public: constructor */

	@SuppressWarnings("unchecked")
	public TextFormat(String text)
	{
		if(text == null) text = "";

		List x = new ArrayList(4);
		int  b = 0, d;

		//c: for all '$'
		for(int i = 0;(i != -1);i = text.indexOf('$', i + 1))
		{
			//~: find the following digits
			for(d = i + 1;(d < text.length());d++)
				if(!Character.isDigit(text.charAt(d)))
					break;

			String ds;

			//?: {got to the end}
			if(d == text.length())
				ds = text.substring(i + 1);
			else
				ds = text.substring(i + 1, d);

			//?: {has no digits} skip this
			if(ds.isEmpty()) continue;

			//~: add preceding text
			if(b != i) x.add(text.substring(b, i));
			b = d; //<-- goto the next text fragment

			//~: add index placeholder
			x.add(Integer.parseInt(ds));
		}

		//~: the text tail
		if(b < text.length())
			x.add(text.substring(b));

		//~: assign the items
		this.items = x.toArray();
		for(Object s : this.items)
			if(s instanceof String)
				length += ((String)s).length();
	}


	/* public: TextFormat interface */

	public String format(Object... ps)
	{
		return format(Arrays.asList((Object[])ps));
	}

	@SuppressWarnings("unchecked")
	public String format(List ps)
	{
		StringBuilder s = new StringBuilder(length);

		for(Object i : items)
			if(i instanceof String)
				s.append(i);
			else
			{
				Object x = null;

				if((Integer)i < ps.size())
					x = ps.get((Integer)i);

				if((x != null) && !(x instanceof CharSequence))
					x = x.toString();

				if(x != null) s.append(x);
			}

		return s.toString();
	}


	/* private: string items */

	private Object[] items;
	private int      length;
}