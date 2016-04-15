package com.tverts.auth.server.support;

/* Java */

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


/**
 * Caches {@link MessageDigest} instances
 * and helps invoke them.
 *
 * @author anton.baukin@gmail.com
 */
public final class AuthDigest
{
	/* public: AuthDigest interface */

	public AuthDigest setPoolSize(int poolSize)
	{
		this.poolSize = poolSize;
		return this;
	}

	public byte[]     sign(Object... values)
	{
		MessageDigest digest = digest();
		byte[]        longs  = null;
		byte[]        result;

		try
		{
			for(Object v : values) if(v != null)
			{
				if(v instanceof Long)
				{
					if(longs == null)
						longs = new byte[8];

					long l = (Long)v;
					for(int i = 0;(i < 8);i++)
						longs[i] = (byte)( (l >> 8*i) & 0xFF );

					digest.update(longs);
					continue;
				}

				if(v instanceof byte[])
				{
					digest.update((byte[]) v);
					continue;
				}

				if(v instanceof char[])
				{
					digest.update(Encodings.hex2bytes((char[])v));
					continue;
				}

				if(v instanceof CharSequence) try
				{
					digest.update(v.toString().getBytes("UTF-8"));
					continue;
				}
				catch(Exception e)
				{
					throw EX.wrap(e); 
				}

				if(v instanceof BytesStream) try
				{
					((BytesStream)v).digest(digest);
					continue;
				}
				catch(Exception e)
				{
					throw EX.wrap(e); 
				}

				throw EX.arg();
			}

			result = digest.digest();
		}
		finally
		{
			digest.reset();
			free(digest);
		}

		return result;
	}

	public String     signHex(Object... values)
	{
		return new String(Encodings.bytes2hex(
		  this.sign(values)
		));
	}

	/* protected: access secure generators */

	protected MessageDigest digest()
	{
		MessageDigest res = null;

		synchronized(this)
		{
			List<MessageDigest> list =
			  (pool == null)?(null):(pool.get());

			if((list != null) && !list.isEmpty())
				res = list.remove(list.size() - 1);
		}

		if(res != null)
			return res;

		try
		{
			return MessageDigest.getInstance("SHA-1");
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}
	}

	protected void          free(MessageDigest gen)
	{
		synchronized(this)
		{
			List<MessageDigest> list;

			if((pool == null) || ((list = pool.get()) == null))
				pool = new WeakReference<List<MessageDigest>>(
				  list = new ArrayList<MessageDigest>(poolSize));

			if(list.size() < poolSize)
				list.add(gen);
		}
	}


	/* private: weak pool */

	private WeakReference<List<MessageDigest>> pool;
	private volatile int poolSize = 256;
}