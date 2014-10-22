package com.tverts.support.streams;

/* Java */

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Output stream filter that never
 * closes the underlie stream till
 * it is ordered directly.
 *
 * @author anton.baukin@gmail.com.
 */
public class NotCloseOutput extends FilterOutputStream
{
	public NotCloseOutput(OutputStream out)
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