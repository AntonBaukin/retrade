package com.tverts.support.streams;

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
	public void        copy(OutputStream stream)
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
	 * Copies the bytes to the array given and returns
	 * the number of bytes actually copied.
	 *
	 * @param off  the offset within the argument array.
	 */
	public int         copy(byte[] a, int off, int len)
	  throws IOException
	{
		if(buffers == null)
			throw new IOException("ByteStream is closed!");

		if(buffers.isEmpty())
			return 0;

		int    res  = 0;
		byte[] last = buffers.get(buffers.size() - 1);

		for(byte[] buf : buffers)
		{
			int sz = (buf == last)?(position):(buf.length);
			if(sz > len) sz = len;

			System.arraycopy(buf, 0, a, off, sz);
			off += sz; len -= sz; res += sz;

			if(len == 0) break;
		}

		return res;
	}

	/**
	 * Returns a copy of the bytes written.
	 */
	public byte[]      bytes()
	  throws IOException
	{
		byte[] res = new byte[(int) length()];
		int    csz = copy(res, 0, res.length);

		if(res.length != csz)
			throw new IllegalStateException(
			  "Error in BytesStream.copy(bytes) implementation!");
		return res;
	}

	/**
	 * Writes all the bytes from the stream given.
	 * The stream is not closed in this call.
	 */
	public void        write(InputStream stream)
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

	public long        length()
	  throws IOException
	{
		if(buffers == null)
			throw new IOException("ByteStream is closed!");

		return this.length;
	}

	public void        digest(MessageDigest md)
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

	public InputStream inputStream()
	{
		return new Stream();
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
	{
		if(buffers == null) return;

		ByteBuffers.INSTANCE.free(buffers);
		buffers = null;
	}


	/* Input Stream */

	private class Stream extends InputStream
	{
		/* public: InputStream interface */

		public int     read()
		  throws IOException
		{
			if(byte1 == null)
				byte1 = new byte[1];

			int x = this.read(byte1, 0, 1);
			return (x <= 0)?(-1):(byte1[0] & 0xFF);
		}

		public int     read(byte[] b, int off, int len)
		  throws IOException
		{
			if(b == null)
				throw new NullPointerException();
			if((off < 0) | (len < 0) | (len > b.length - off))
				throw new IndexOutOfBoundsException();

			if(buffers == null)
				throw new IOException("ByteStream is closed!");
			if(bufind  == -1)
				throw new IOException("Input Stream of ByteStream is closed!");

			if(buffers.isEmpty())
				return -1;

			int got = 0;

			while(len != 0)
			{
				byte[] buf = buffers.get(bufind);
				int    sz;

				//?: {it is the current buffer}
				if(bufind == buffers.size() - 1)
				{
					if(bufpos == position)
						break;

					sz = position - bufpos;
				}
				//!: it is one of the fully filled buffers
				else if(bufpos == buf.length)
				{
					bufind++; bufpos = 0;
					continue;
				}
				else
					sz = buf.length - bufpos;

				if(sz > len) sz = len;
				System.arraycopy(buf, bufpos, b, off, sz);
				bufpos += sz; off += sz; len -= sz; got += sz;
			}

			return (got == 0)?(-1):(got);
		}

		public void   close()
		  throws IOException
		{
			this.bufind = -1;
		}

		public boolean markSupported()
		{
			return false;
		}


		/* private: read position */

		private int    bufind;
		private int    bufpos;
		private byte[] byte1;
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