package com.tverts.objects;

/* standard Java classes */

import java.util.Collections;
import java.util.List;


/**
 * Stores string value as a reference bean.
 *
 * @author anton.baukin@gmail.com
 */
public class      StringBean
       implements CharSequence, StringsReference
{
	/* public: StringsReference interface */

	public List<CharSequence> dereferObjects()
	{
		return Collections.singletonList(string);
	}


	/* public: access string value */

	public CharSequence getString()
	{
		return string;
	}

	public void         setString(CharSequence string)
	{
		if(string == null) throw new IllegalArgumentException();
		this.string = string;
	}


	/* public: CharSequence interface */

	public int          length()
	{
		return string.length();
	}

	public char         charAt(int index)
	{
		return string.charAt(index);
	}

	public CharSequence subSequence(int start, int end)
	{
		return string.subSequence(start, end);
	}

	/* public: Object interface */

	public String       toString()
	{
		return string.toString();
	}

	public boolean      equals(Object o)
	{
		return (o == this) ||
		  ((o instanceof CharSequence) && string.equals(o));
	}

	public int          hashCode()
	{
		return string.hashCode();
	}


	/* private: string value */

	private CharSequence string = "";
}