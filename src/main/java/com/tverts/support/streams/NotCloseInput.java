package com.tverts.support.streams;

/* Java */

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Input stream filter that never
 * closes the underlie stream till
 * it is ordered directly.
 *
 * @author anton.baukin@gmail.com.
 */
public class NotCloseInput extends FilterInputStream
{
	public NotCloseInput(InputStream out)
	{
		super(out);
	}

	public void allowClose()
	{
		if(close == null)
			close = true;
	}

	private Boolean close;

	public void close()
	  throws IOException
	{
		if(Boolean.TRUE.equals(close))
		{
			close = false;
			super.close();
		}
	}
}