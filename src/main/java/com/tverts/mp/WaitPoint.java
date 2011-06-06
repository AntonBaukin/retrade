package com.tverts.mp;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Wait Point is where co-working processes
 * do wait each other.
 *
 * When a thread invokes {@link #waitOnPoint(Collection)}
 * it is blocked until all the resources of the
 * point are ready. More than one thread is possible
 * to wait: they are unblocked simultaneously.
 *
 * Note that this class is not a semaphore:
 * when all the resources are ready threads
 * are not blocked anymore.
 *
 *
 * @author anton.baukin@gmail.com
 */
public final class WaitPoint
{
	/* public: constructor */

	/**
	 * Specifies the set of resources to wait.
	 */
	@SuppressWarnings("unchecked")
	public WaitPoint(Set resources)
	{
		if(resources == null) throw new IllegalArgumentException();
		this.resources = new HashSet(resources);
	}

	/* public: WaitPoint interface */

	/**
	 * Tells to wait until all the resources are ready.
	 *
	 * The optional except resources are controlled
	 * by the callee and must be excluded from the check.
	 * (Regarded as ready.)
	 */
	@SuppressWarnings("unchecked")
	public void    waitOnPoint(Collection except)
	  throws InterruptedException
	{
		synchronized(resources)
		{
			Set r = resources;

			if((except != null) && !except.isEmpty())
			{
				Set t = new HashSet(r);
				t.removeAll(except);
				r = t;
			}

			if(!r.isEmpty())
				resources.wait();
		}
	}

	public void    waitOnPoint(Object... except)
	  throws InterruptedException
	{
		this.waitOnPoint(Arrays.asList(except));
	}

	/**
	 * Waits for the resources with the timeout limit.
	 * If it is zero does not wait, and works as 'try-wait'.
	 * Returns {@code true} when all the resources are ready.
	 */
	@SuppressWarnings("unchecked")
	public boolean waitOnPoint(long timeout, Collection except)
	  throws InterruptedException
	{
		if(timeout < 0L) throw new IllegalArgumentException();

		synchronized(resources)
		{
			Set r = resources;

			if((except != null) && !except.isEmpty())
			{
				Set t = new HashSet(r);
				t.removeAll(except);
				r = t;
			}

			if(!r.isEmpty() && (timeout != 0L))
			{
				resources.wait(timeout);

				//!: mek the copy actual
				r.retainAll(resources);
			}

			return r.isEmpty();
		}
	}

	public boolean waitOnPoint(long timeout, Object... except)
	  throws InterruptedException
	{
		return this.waitOnPoint(timeout, Arrays.asList(except));
	}

	/**
	 * Tells that the resources given are ready.
	 * Releasing the same resource more than one time
	 * has no effect.
	 */
	@SuppressWarnings("unchecked")
	public void    releaseResources(Collection resources)
	{
		synchronized(this.resources)
		{
			this.resources.removeAll(resources);

			if(this.resources.isEmpty())
				this.resources.notifyAll();
		}
	}

	public void    releaseResources(Object... resources)
	{
		this.releaseResources(Arrays.asList(resources));
	}

	/* private: the resources */

	private final Set resources;
}