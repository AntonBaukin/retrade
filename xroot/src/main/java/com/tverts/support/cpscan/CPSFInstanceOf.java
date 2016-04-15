package com.tverts.support.cpscan;

/* Spring Framework (internals) */

import org.springframework.core.type.classreading.MetadataReader;


/**
 * Allows classes that inherit from the given class
 * or do implement it (interface).
 *
 * Note that the check class itself is excluded.
 *
 *
 * WARNING! Using this filter is expensive as it
 * loads all the classes checked! Prefer annotations.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class CPSFInstanceOf extends CPScanFilterBase
{
	/* public: CPSFInstanceOf (bean) interface */

	public Class   getCheckClass()
	{
		return checkClass;
	}

	public void    setCheckClass(Class checkClass)
	{
		this.checkClass = checkClass;
	}


	/* public: CPScanFilter interface */

	@SuppressWarnings("unchecked")
	public boolean isClassOfInterest(MetadataReader mr)
	{
		if(getCheckClass() == null)
			return false;

		if(getCheckClass().getName().equals(className(mr)))
			return false;

		if(!isAllowedClass(mr))
			return false;

		return getCheckClass().isAssignableFrom(loadClass(mr));

	}


	/* private: parameters of the filter */

	private Class checkClass;
}