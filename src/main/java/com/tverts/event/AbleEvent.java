package com.tverts.event;

/* com.tverts: endure (secure) */

import com.tverts.endure.secure.SecAble;


/**
 * Triggered after {@link SecAble} was granted
 * to Auth Login, or before it was revoked from.
 *
 * @author anton.baukin@gmail.com
 */
public class AbleEvent extends EventBase
{
	/* public: constructor */

	public AbleEvent(boolean granted, SecAble target)
	{
		super(target);
		this.granted = granted;
	}


	/* public: AbleEvent interface */

	public boolean isGranted()
	{
		return granted;
	}


	/* private: granted-revoked */

	private boolean granted;
}