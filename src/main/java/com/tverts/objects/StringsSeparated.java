package com.tverts.objects;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ListIterator;


/**
 * Translates the string given into the list of strings
 * by separating them with the RegExp given. The
 * resulting strings are trimmed.
 *
 * @author anton.baukin@gmail.com
 */
public class StringsSeparated implements StringsReference
{
	/* public: StringsReference interface */

	public List<CharSequence> dereferObjects()
	{
		if(string == null) return Collections.emptyList();

		ArrayList<CharSequence> res = new ArrayList<CharSequence>(
		  Arrays.asList(string.split(regexp)));

		for(ListIterator<CharSequence> i = res.listIterator();(i.hasNext());)
		{
			String s = i.next().toString().trim();

			if(s.length() == 0)
				i.remove();
			else
				i.set(s);
		}

		return res;
	}


	/* public: StringsSeparated (bean) interface */

	public String getString()
	{
		return string;
	}

	public void   setString(String string)
	{
		this.string = string;
	}

	public String getRegexp()
	{
		return regexp;
	}

	public void   setRegexp(String regexp)
	{
		if(regexp == null) throw new IllegalArgumentException();
		this.regexp = regexp;
	}

	/* private: string value */

	private String string;
	private String regexp = ";";
}