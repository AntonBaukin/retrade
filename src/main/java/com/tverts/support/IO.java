package com.tverts.support;

/* Java */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* com.tverts: support */

import com.tverts.support.streams.ByteBuffers;


/**
 * Input-output helping functions.
 *
 * @author anton.baukin@gmail.com.
 */
public class IO
{
	/* streaming */

	public static int pump(InputStream i, OutputStream o)
	  throws IOException
	{
		byte[] buf = ByteBuffers.INSTANCE.get();
		int    res = 0;

		try
		{
			int s; while((s = i.read(buf)) > 0)
			{
				o.write(buf, 0, s);
				res += s;
			}
		}
		finally
		{
			ByteBuffers.INSTANCE.free(buf);
		}

		return res;
	}
}