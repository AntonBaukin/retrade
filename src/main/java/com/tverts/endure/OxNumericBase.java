package com.tverts.endure;

/* com.tverts: api */

import com.tverts.api.core.PkeyObject;

/* com.tverts: hibery */

import com.tverts.hibery.OxBytes;
import com.tverts.hibery.OxBytesType;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Plain object storing it's state
 * as nested XML document.
 *
 * Has Ox-Search update support for child
 * classes implementing the interface.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public abstract class OxNumericBase
       extends        NumericBase
       implements     Ox
{
	/* Object Extraction */

	public Object  getOx()
	{
		return (oxBytes == null)?(null):(oxBytes.getOx());
	}

	public void    setOx(Object ox)
	{
		if(oxBytes == null)
			oxBytes = new OxBytes(ox);
		else
			oxBytes.setOx(ox);

		//!: self-update
		this.updateOx();
	}

	public void    updateOx()
	{
		//~: update ox-object
		EX.assertn(oxBytes).updateOx();

		//?: {object has primary key}
		Object ox = oxBytes.getOx();
		if(ox instanceof PkeyObject)
		{
			Long pk = this.getPrimaryKey();
			Long xk = ((PkeyObject)ox).getPkey();

			if(xk != null)
				EX.assertx(CMP.eq(xk, pk));
			else if(pk != null)
				((PkeyObject)ox).setPkey(pk);
		}

		//~: update the search text
		if(this instanceof OxSearch)
			setOxSearch(null);
	}

	public boolean isUpdatedOx()
	{
		return (oxBytes != null) && oxBytes.isUpdatedOx();
	}

	public void    setOxSearch(String oxSearch)
	{
		EX.assertx(oxSearch == null);
	}

	/**
	 * Property mapped to Hibernate with special
	 * {@link OxBytesType} strategy class.
	 */
	public OxBytes getOxBytes()
	{
		return oxBytes;
	}

	private OxBytes oxBytes;

	public void    setOxBytes(OxBytes oxBytes)
	{
		this.oxBytes = oxBytes;
	}
}