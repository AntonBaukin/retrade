package com.tverts.endure.core;

/* com.tverts: api */

import com.tverts.api.core.PkeyObject;

/* com.tverts: endure (core) */

import com.tverts.endure.Ox;
import com.tverts.endure.OxSearch;
import com.tverts.endure.Unity;

/* com.tverts: hibery */

import com.tverts.hibery.OxBytes;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


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
		if(oxBytes != null)
			return oxBytes.getOx();

		if(getUnity() != null)
			oxBytes = getUnity().getOxBytes();

		return (oxBytes == null)?(null):(oxBytes.getOx());
	}

	private OxBytes oxBytes;

	public void    setOx(Object ox)
	{
		if(oxBytes != null)
			oxBytes.setOx(ox);
		else if(ox != null)
		{
			if(getUnity() != null)
				oxBytes = getUnity().getOxBytes();

			if(oxBytes == null)
			{
				oxBytes = new OxBytes(ox);

				if(getUnity() != null)
					getUnity().setOxBytes(oxBytes);
			}
		}

		//!: self-update
		this.updateOx();
	}

	public void    updateOx()
	{
		//?: {has no bytes here}
		if((oxBytes == null) && (getUnity() != null))
			oxBytes = getUnity().getOxBytes();

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

		//?: {ox-search & unity assigned}
		if((getUnity() != null) && (this instanceof OxSearch))
			getUnity().setOxSearch(((OxSearch)this).getOxSearch());
	}

	public boolean isUpdatedOx()
	{
		return (oxBytes != null) && oxBytes.isUpdatedOx();
	}


	/* Numeric Identity */

	public void setPrimaryKey(Long pk)
	{
		super.setPrimaryKey(pk);

		//?: {has object}
		if((pk != null) && (oxBytes != null))
		{
			Object ox = oxBytes.getOx();

			if(ox instanceof PkeyObject)
				((PkeyObject)ox).setPkey(pk);
		}
	}


	/* public: United Interface */

	public void setUnity(Unity unity)
	{
		//?: {mirror is undefined}
		EX.assertn(unity, "Unified mirror is required for Entity [",
		  getPrimaryKey(), "]!");

		//?: {mirror is the same}
		if(getUnity() == unity) return;

		//?: {mirror may not me altered}
		EX.assertx( (getUnity() == null), "Unified mirror of Entity [",
		  getPrimaryKey(), "] may not be altered!");

		super.setUnity(unity);

		//~: assign ox-bytes
		if((oxBytes != null) && (unity.getOxBytes() != oxBytes))
		{
			EX.assertx(unity.getOxBytes() == null);
			unity.setOxBytes(oxBytes);
		}

		//?: {ox-search is not assigned}
		if((unity.getOxSearch() == null) && (this instanceof OxSearch))
			unity.setOxSearch(((OxSearch)this).getOxSearch());
	}
}