package com.tverts.jsx;

/* Java */

import java.io.File;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URI;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.streams.BytesStream;


/**
 * Denotes JavaScript file stored somewhere.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsFile implements AutoCloseable
{
	public JsFile(URI uri)
	{
		this.uri = EX.assertn(uri);

		File f = null; try
		{
			f = new File(uri);
		}
		catch(Throwable e)
		{
			try
			{
				f = new File(uri.getPath());
			}
			catch(Throwable x)
			{}
		}
		finally
		{
			if(f != null) try
			{
				if(!f.isFile() || !f.canRead() || (f.length() == 0L))
					f = null;
			}
			catch(Throwable e)
			{
				f = null;
			}
		}

		this.file = f;
	}

	protected final URI uri;


	/* JavaScript File */

	public URI     uri()
	{
		return this.uri;
	}

	/**
	 * Returns file if script is located
	 * explicitly in the file system.
	 */
	public File    file()
	{
		return this.file;
	}

	protected final File file;

	/**
	 * Loads the file on the first demand
	 * (also, after each clean request).
	 */
	public byte[]  bytes()
	{
		byte[] res = (bytes == null)?(null):(bytes.get());

		if(res == null) synchronized(bytesLock)
		{
			res = (bytes == null)?(null):(bytes.get());
			if(res == null) try
			(
			  InputStream is = this.uri.toURL().openStream();
			  BytesStream bs = new BytesStream()
			)
			{
				EX.assertn(is, "Resource file does not exist!");
				bs.write(is);

				res = bs.bytes();
				bytes = new SoftReference<byte[]>(res);
				this.ts = System.currentTimeMillis();
			}
			catch(Throwable e)
			{
				throw EX.wrap(e, "Can't read JsFile [", this.uri, "]!");
			}
		}

		return res;
	}

	protected Reference<byte[]> bytes;
	protected final Object      bytesLock = new Object();

	/**
	 * Just frees the bytes buffer.
	 */
	public void    close()
	{
		this.bytes = null;
	}

	/**
	 * For local files, checks that the file
	 * was modified (by the timestamp) and,
	 * if so, cleans the buffer to reload
	 * the file further. For remote files
	 * always cleans. Not call this method
	 * frequently.
	 */
	public void    revalidate()
	{
		if(file == null)
			this.close();
		else try
		{
			if(file.lastModified() > this.ts)
				this.close();
		}
		catch(Throwable e)
		{
			this.close();
		}
	}

	protected volatile long ts;
}