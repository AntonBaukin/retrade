package com.tverts.secure;

/* Java */

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.streams.BytesStream;


/**
 * Caches {@link MessageDigest} instances
 * and helps invoke them.
 *
 * @author anton.baukin@gmail.com
 */
public final class DigestCache
{
	public static final DigestCache INSTANCE =
	  new DigestCache();


	/* public: AuthDigest interface */

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
					digest.update(SU.hex2bytes((char[])v));
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
		return new String(SU.bytes2hex(this.sign(values)));
	}

	public void       random(byte[] bytes)
	{
		EX.assertn(bytes);
		EX.assertx(bytes.length != 0);

		SecureRandom gen = random();
		gen.nextBytes(bytes);
		free(gen);
	}


	/* protected: access secure generators */

	protected MessageDigest digest()
	{
		MessageDigest res = null;

		synchronized(this)
		{
			List<MessageDigest> list =
			  (digests == null)?(null):(digests.get());

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

	private WeakReference<List<MessageDigest>> digests;

	protected void          free(MessageDigest dig)
	{
		synchronized(this)
		{
			List<MessageDigest> list;

			if((digests == null) || ((list = digests.get()) == null))
				digests = new WeakReference<>(list = new ArrayList<>(16));

			list.add(EX.assertn(dig));
		}
	}

	protected SecureRandom  random()
	{
		SecureRandom res = null;

		synchronized(this)
		{
			List<SecureRandom> list =
			  (ramdoms == null)?(null):(ramdoms.get());

			if((list != null) && !list.isEmpty())
				res = list.remove(list.size() - 1);
		}

		return (res != null)?(res):(new SecureRandom());
	}

	private WeakReference<List<SecureRandom>> ramdoms;

	protected void         free(SecureRandom gen)
	{
		synchronized(this)
		{
			List<SecureRandom> list;

			if((ramdoms == null) || ((list = ramdoms.get()) == null))
				ramdoms = new WeakReference<>(list = new ArrayList<>(16));

			list.add(EX.assertn(gen));
		}
	}
}