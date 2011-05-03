package com.tverts.mp;

/* standard Java classes */

import java.util.HashMap;

/**
 * Suppose that your thread must wait till one or more
 * operations are completed. Operations may be initiated
 * during the waiting. At the moment when all of them
 * are complete, all waiting threads are released.
 *
 * This implementation handles the case when the same
 * flag is set more than one time. If some procedure
 * sets a flag, it must release it lately.
 *
 * It is also possible to release flag before occupying
 * it what can reduce additional synchronizations.
 * If some flag is released before occupation, the
 * latter would not cause the following wait request
 * to block (on this flag).
 *
 * As the result: a flag is removed from the set when
 * it's occupy/release counter equals to zero. When
 * all the flags are removed, the waiting threads are
 * resumed.
 *
 * @author anton.baukin@gmail.com
 */
public final class WaitEmptyFlags
{
	/* public: WaitEmptyFlags interface */

	/**
	 * Tells to wait on the following flags.
	 * (Note about pre-released flags.)
	 */
	public void    occupy(Object... flags)
	{
		synchronized(mutex)
		{
			doOccupy(flags);
		}
	}

	/**
	 * Tells that the specified flags are
	 * no busy. Note about pre-release and
	 * the occupy/release counter.
	 */
	public void    release(Object... flags)
	{
		synchronized(mutex)
		{
			doRelease(flags);
		}
	}

	/**
	 * Does release, then occupy in single
	 * critical section. Note that the
	 * order means here, as release may actually
	 * resume threads, what is not possible if
	 * the occupation is before.
	 *
	 * If wait flag is set also will wait as the third
	 * operation (not exiting the critical section).
	 */
	public void    relocc(Object[] release, Object[] occupy, boolean wait)
	  throws InterruptedException
	{
		synchronized(mutex)
		{
			doRelease(release);
			doOccupy (occupy);

			if(wait && !occupied.isEmpty())
				mutex.wait();
		}
	}

	public void    relocc(Object release, Object occupy, boolean wait)
	  throws InterruptedException
	{
		synchronized(mutex)
		{
			doRelease(release);
			doOccupy (occupy);

			if(wait && !occupied.isEmpty())
				mutex.wait();
		}
	}

	/**
	 * This call blocks the invoking thread
	 * until all the flags are removed from
	 * the set, what means that occupy/release
	 * queries on all the flags are balanced
	 * to zero.
	 */
	public void    waitEmptyFlags()
	  throws InterruptedException
	{
		synchronized(mutex)
		{
			if(!occupied.isEmpty())
				mutex.wait();
		}
	}

	/**
	 * The same wait as {@link #waitEmptyFlags()},
	 * but with the timeout. Returns {@code true} if
	 * wait operation was successful.
	 */
	public boolean waitEmptyFlags(long timeout)
	  throws InterruptedException
	{
		if(timeout < 0L)
			throw new IllegalArgumentException();

		synchronized(mutex)
		{
			while(!occupied.isEmpty())
				mutex.wait(timeout);
			return occupied.isEmpty();
		}
	}

	/* private: operations */

	private void   doOccupy(Object... flags)
	{
		Integer n;

		for(Object flag : flags)
			//?: {has no pre-released flag}
			if((n = released.get(flag)) == null)
			{
				n = occupied.get(flag);
				occupied.put(flag, (n == null)?(1):(n + 1));
			}
			else
			{
				n = n - 1;

				//?: {have no pre-resumes more}
				if(n == 0)
					released.remove(flag);
				else
					released.put(flag, n);
			}
	}

	private void   doRelease(Object... flags)
	{
		Integer n;

		for(Object flag : flags)
			//?: {have this flag not occupied} add to released
			if((n = occupied.get(flag)) == null)
			{
				n = released.get(flag);
				released.put(flag, (n == null)?(1):(n + 1));
			}
			else
			{
				n = n - 1;

				//?: {have no occupations more}
				if(n == 0)
					occupied.remove(flag);
				else
					occupied.put(flag, n);
			}

		//?: {all the flags are cleared} notify
		if(this.occupied.isEmpty())
			mutex.notifyAll();
	}

	/* private: mutex state */

	private HashMap<Object, Integer> occupied =
	  new HashMap<Object, Integer>(7);
	private HashMap<Object, Integer> released =
	  new HashMap<Object, Integer>(7);

	private final Object             mutex    = new Object();
}