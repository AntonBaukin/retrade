package com.tverts.support.misc;

/**
 * Wraps array of characters into
 * a {@link CharSequence}.
 *
 * @author anton.baukin@gmail.com
 */
public final class CharArraySequence implements CharSequence
{
	/* public: constructor */

	public CharArraySequence(char[] array)
	{
		if(array == null) array = new char[0];

		this.array  = array;
		this.first  = 0;
		this.length = array.length;
	}

	public CharArraySequence(char[] array, int first, int length)
	{
		if(array == null) array = new char[0];
		if((first < 0) || (length > array.length))
			throw new IndexOutOfBoundsException();

		this.array  = array;
		this.first  = first;
		this.length = length;
	}


	/* public: CharSequence interface */

	public int          length()
	{
		return length;
	}

	public char         charAt(int index)
	{
		if((index < 0) || (index >= length))
			throw new IndexOutOfBoundsException();
		return array[first + index];
	}

	public CharSequence subSequence(int s, int e)
	{
		if((s < 0) || (e < 0) || (s > e) || (e > length))
			throw new IndexOutOfBoundsException();

		return new CharArraySequence(array, first + s, e - s);
	}


	/* character array */

	private char[] array;
	private int    first;
	private int    length;
}