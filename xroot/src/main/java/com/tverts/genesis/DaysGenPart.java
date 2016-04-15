package com.tverts.genesis;

/**
 * Implement this interface in the Genesis Part
 * designed for {@link DaysGenDisp}. It allows
 * the dispatcher to check (and set) the fact
 * of generation in the specified day not to
 * generate again on the system restart.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface DaysGenPart
{
	/* public: DaysGenPart interface */

	/**
	 * Checks whether the day defined in the context
	 * by {@link DaysGenDisp#DAY} property is
	 * available for generation.
	 */
	public boolean isDayClear(GenCtx ctx);

	public void    markDayGenerated(GenCtx ctx);
}