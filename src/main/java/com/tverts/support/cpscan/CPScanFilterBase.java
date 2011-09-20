package com.tverts.support.cpscan;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Spring Framework (internals) */

import org.springframework.core.type.classreading.MetadataReader;


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


	/* protected: filtering support */

	protected boolean isAllowedClass(MetadataReader mr)
	{
		return mr.getClassMetadata().isConcrete();
	}

	protected String  className(MetadataReader mr)
	{
		return mr.getClassMetadata().getClassName();
	}

	protected Class   loadClass(MetadataReader mr)
	{
		try
		{
			return Thread.currentThread().getContextClassLoader().
			  loadClass(className(mr));
		}
		catch(Exception e)
		{
			throw new RuntimeException(String.format(
			  "Error occured while loading scanned class '%s'!",
			  className(mr)), e);
		}
	}
}