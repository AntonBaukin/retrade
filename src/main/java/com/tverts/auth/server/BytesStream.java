package com.tverts.auth.server;

/* standard Java classes */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;


/**
 * Output stream that uses shared buffers and
 * allows simultaneous reading of written bytes.
 *
 * This implementation is not thread-safe!
 *
 *
 * @author anton.baukin@gmail.com
 */
public final class BytesStream extends OutputStream
{
	/* public: BytesStream interface */

	/**
	 * Copies the bytes written to the stream given.
	 */
	public void copy(OutputStream stream)
	  throws IOException
	{
		if(buffers == null)
			throw new IOException("ByteStream is closed!");

		if(buffers.isEmpty())
			return;

		byte[] last = buffers.get(buffers.size() - 1);
		for(byte[] buf : buffers)
			stream.write(buf, 0, (buf == last)?(position):(buf.length));
	}

	/**
	 * Writes all the bytes from the stream given.
	 * The stream is not closed in this call.
	 */
	public void write(InputStream stream)
	  throws IOException
	{
		byte[] buf = ByteBuffers.INSTANCE.get();
		int    sz;

		try
		{
			while((sz = stream.read(buf)) > 0)
				write(buf, 0, sz);
		}
		finally
		{
			ByteBuffers.INSTANCE.free(buf);
		}
	}

	public long length()
	  throws IOException
	{
		if(buffers == null)
			throw new IOException("ByteStream is closed!");

		return this.length;
	}

	public void digest(MessageDigest md)
	  throws IOException
	{
		if(buffers == null)
			throw new IOException("ByteStream is closed!");

		if(buffers.isEmpty())
			return;

		byte[] last = buffers.get(buffers.size() - 1);
		for(byte[] buf : buffers)
			md.update(buf, 0, (buf == last)?(position):(buf.length));
	}


	/* public: OutputStream interface */

	public void write(int b)
	  throws IOException
	{
		if(byte1 == null)
			byte1 = new byte[1];
		byte1[0] = (byte) b;

		this.write(byte1, 0, 1);
	}

	public void write(byte[] b, int off, int len)
	  throws IOException
	{
		if(buffers == null)
			throw new IOException("ByteStream is closed!");

		while(len > 0)
		{
			if(buffers.isEmpty())
			{
				buffers.add(ByteBuffers.INSTANCE.get());
				continue;
			}

			byte[] x = buffers.get(buffers.size() - 1);
			int    s = x.length - position;

			if(s == 0)
			{
				buffers.add(ByteBuffers.INSTANCE.get());
				position = 0;
				continue;
			}

			if(s > len) s = len;

			System.arraycopy(b, off, x, position, s);
			off += s; len -= s; position += s;
			this.length += s;
		}
	}

	public void flush()
	  throws IOException
	{
		if(buffers == null)
			throw new IOException("ByteStream is closed!");
	}

	public void close()
	  throws IOException
	{
		if(buffers == null) return;

		ByteBuffers.INSTANCE.free(buffers);
		buffers = null;
	}



	/* private: list of buffers */

	private ArrayList<byte[]> buffers =
	  new ArrayList<byte[]>(16);

	/**
	 * The position within the last
	 * buffer of the list.
	 */
	private int               position;
	private long              length;

	private byte[]            byte1;
}