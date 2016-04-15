package com.tverts.genesis;

/* standard Java classes */

import java.util.Collections;
import java.util.List;


/**
 * Provides useful utilities to Genesis
 * implementation.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GenesisPartBase
       extends        GenesisBase
       implements     GenesisReference
{
	/* public: GenesisReference interface */

	public List<Genesis> dereferObjects()
	{
		return Collections.<Genesis> singletonList(this);
	}
}