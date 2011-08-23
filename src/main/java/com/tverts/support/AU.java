package com.tverts.support;

/**
 * Various array utils.
 *
 * @author anton.baukin@gmail.com
 */
public class AU
{
	/* search */

	public static int indexOf(Object[] a, Object o)
	{
		if(a == null)
			return -1;

		if(o == null)
		{
			for(int i = 0;(i < a.length);i++)
				if(a[i] == null)
					return i;

			return -1;
		}

		for(int i = 0;(i < a.length);i++)
			if(o.equals(a[i]))
				return i;

		return -1;
	}

	public static int indexOfRef(Object[] a, Object o)
	{
		if(a != null) for(int i = 0;(i < a.length);i++)
			if(a[i] == o)
				return i;

		return -1;
	}
}
