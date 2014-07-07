package com.tverts.endure.core;

/* com.tverts: endure (core) */

import com.tverts.endure.Ox;

/* com.tverts: objects */

import com.tverts.hibery.OxBytes;
import com.tverts.hibery.OxBytesType;


/**
 * Entity that stores it's state as
 * XML object of the predefined type.
 *
 * @author anton.baukin@gmail.com.
 */
public abstract class OxEntity
       extends        Entity
       implements     Ox
{
	/* Object Extraction */

	public Object  getOx()
	{
		return (oxBytes == null)?(null):(oxBytes.getOx());
	}

	public void    setOx(Object ox)
	{
		if(oxBytes != null)
			oxBytes.setOx(ox);
		else if(ox != null)
			oxBytes = new OxBytes(ox);
	}

	public void    updateOx()
	{
		if(oxBytes != null)
			oxBytes.updateOx();
	}

	public boolean isUpdatedOx()
	{
		return (oxBytes != null) && oxBytes.isUpdatedOx();
	}



	/* Entity Access */

	/**
	 * Property mapped to Hibernate with special
	 * {@link OxBytesType} strategy class.
	 */
	public OxBytes  getOxBytes()
	{
		return oxBytes;
	}

	private OxBytes oxBytes;

	public void     setOxBytes(OxBytes oxBytes)
	{
		this.oxBytes = oxBytes;
	}
}