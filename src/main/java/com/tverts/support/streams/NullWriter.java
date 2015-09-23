package com.tverts.support.streams;

/* Java */

import java.io.Writer;


/**
 * Writer that does nothing.
 *
 * @author anton.baukin@gmail.com.
 */
public class NullWriter extends Writer
{
	public void write(char[] cbuf, int off, int len)
	{}

	public void flush()
	{}

	public void close()
	{}
}