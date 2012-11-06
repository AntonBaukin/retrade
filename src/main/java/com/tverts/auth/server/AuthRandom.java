package com.tverts.auth.server;

/* standard Java classes */

import java.lang.ref.WeakReference;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Caches {@link SecureRandom} instances
 * and helps invoke them.
 *
 * @author anton.baukin@gmail.com
 */
public final class AuthRandom
{
	/* public: AuthRandom interface */

	public AuthRandom setPoolSize(int poolSize)
	{
		this.poolSize = poolSize;
		return this;
	}

	public byte[]     randomBytes(int length)
	{
		SecureRandom gen = get();
		byte[]       res = new byte[length];

		gen.nextBytes(res);
		free(gen);

		return res;
	}


	/* protected: access secure generators */

	protected SecureRandom get()
	{
		SecureRandom res = null;

		synchronized(this)
		{
			List<SecureRandom> list =
			  (pool == null)?(null):(pool.get());

			if((list != null) && !list.isEmpty())
				res = list.remove(list.size() - 1);
		}

		return (res != null)?(res):(new SecureRandom());
	}

	protected void         free(SecureRandom gen)
	{
		synchronized(this)
		{
			List<SecureRandom> list;

			if((pool == null) || ((list = pool.get()) == null))
				pool = new WeakReference<List<SecureRandom>>(
				  list = new ArrayList<SecureRandom>(poolSize));

			if(list.size() < poolSize)
				list.add(gen);
		}
	}


	/* private: weak pool */

	private WeakReference<List<SecureRandom>> pool;
	private volatile int poolSize = 256;
}