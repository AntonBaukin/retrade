package com.tverts.genesis;

/* com.tverts: system services */

import com.tverts.system.services.events.ServiceEventBase;


/**
 * {@link GenesisService} broadcasts this
 * event after the generation completed.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GenesisDone extends ServiceEventBase
{
	public static final long serialVersionUID = 0L;
}