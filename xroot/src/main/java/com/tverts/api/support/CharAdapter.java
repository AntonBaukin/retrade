package com.tverts.api.support;

/* Java XML Binding */

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Helper class to marshall characters directly.
 *
 * @author anton.baukin@gmail.com.
 */
public class CharAdapter extends XmlAdapter<String, Character>
{
	public String    marshal(Character v)
	{
		return new String(new char[]{ v });
	}

	public Character unmarshal(String v)
	{
		return (v.length() == 1)?(v.charAt(0)):('\0');
	}
}