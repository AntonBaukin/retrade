package com.tverts.support.streams;

/* Java */

import java.io.IOException;
import java.io.Reader;


/**
 * Reader that has no content.
 *
 * @author anton.baukin@gmail.com.
 */
public class EmptyReader extends Reader
{
	public int read(char[] cbuf, int off, int len)
	{
		return -1;
	}

	public void close()
	{}
}