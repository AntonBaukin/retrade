package com.tverts.support.cpscan;

/* standard Java classes */

import java.util.Collections;
import java.util.List;


/**
 * TODO comment CPScanFilterBase
 *
 * @author anton.baukin@gmail.com
 */
public abstract class CPScanFilterBase
       implements     CPScanFilter, CPScanFilterReference
{
	/* public: ObjectsReference interface */

	public List<CPScanFilter> dereferObjects()
	{
		return Collections.<CPScanFilter> singletonList(this);
	}
}