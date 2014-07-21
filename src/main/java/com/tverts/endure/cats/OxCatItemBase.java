package com.tverts.endure.cats;

/* com.tverts: api */

import com.tverts.api.core.CatItem;

/* com.tverts: hibery */

import com.tverts.api.core.PkeyObject;
import com.tverts.hibery.OxBytes;
import com.tverts.hibery.OxBytesType;

/* com.tverts: endure */

import com.tverts.endure.Ox;
import com.tverts.endure.OxSearch;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Implementation base for catalogue items
 * having nested XML document.
 *
 * @author anton.baukin@gmail.com.
 */
public abstract class OxCatItemBase
       extends        CatItemBase
       implements     Ox, OxSearch
{
	/* Object Extraction */

	public CatItem getOx()
	{
		return (CatItem)((oxBytes == null)?(null):(oxBytes.getOx()));
	}

	public void    setOx(Object ox)
	{
		EX.assertx(ox instanceof CatItem);

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

		//~: assign code+name from the ox-item
		CatItem i; if((i = this.getOx()) != null)
		{
			setCode(i.getCode());
			setName(i.getName());
		}

		//~: update the search text
		this.oxSearch = null;
	}

	public boolean isUpdatedOx()
	{
		return (oxBytes != null) && oxBytes.isUpdatedOx();
	}

	public String  getOxSearch()
	{
		return (oxSearch != null)?(oxSearch):
		  (oxSearch = createOxSearch());
	}

	private String oxSearch;

	public void    setOxSearch(String oxSearch)
	{
		this.oxSearch = oxSearch;
	}

	public String  createOxSearch()
	{
		return SU.catx(getCode(), getName());
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