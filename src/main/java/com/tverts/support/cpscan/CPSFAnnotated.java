package com.tverts.support.cpscan;

/* Spring Framework (internals) */

import org.springframework.core.type.classreading.MetadataReader;


/**
 * Allows classes that directly declare the runtime
 * annotation specified.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class CPSFAnnotated extends CPScanFilterBase
{
	/* public: CPSFInstanceOf (bean) interface */

	public Class   getAnnotation()
	{
		return annotation;
	}

	public void    setAnnotation(Class annotation)
	{
		this.annotation = annotation;
	}


	/* public: CPScanFilter interface */

	public boolean isClassOfInterest(MetadataReader mr)
	{
		if((getAnnotation() == null) || !getAnnotation().isAnnotation())
			return false;

		if(!isAllowedClass(mr))
			return false;

		return mr.getAnnotationMetadata().hasAnnotation(
		  getAnnotation().getName());
	}


	/* private: parameters of the filter */

	private Class annotation;
}