package com.tverts.endure;

/**
 * Returns Unity of the United instance.
 *
 * @author anton.baukin@gmail.com
 */
public class DelayedUnity implements DelayedEntity
{
	public DelayedUnity(United united)
	{
		this.united = united;
	}


	/* public: DelayedEntity interface */

	public Unity accessEntity()
	{
		return united.getUnity();
	}


	/* the united */

	public final United united;
}