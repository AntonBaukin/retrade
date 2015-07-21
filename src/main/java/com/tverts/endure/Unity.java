package com.tverts.endure;

/* com.tverts: hibery */

import com.tverts.hibery.OxBytes;
import com.tverts.hibery.OxBytesType;


/**
 * Unified Mirror object.
 *
 * {@link United} instances may have the mirror
 * that is 1-to-1 related.
 *
 * If instance of arbitrary type need refer United
 * instance of else type, it need refer that's
 * mirror. It is useful for system instances
 * linked with many different types.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class Unity implements PrimaryIdentity, TxEntity
{
	/* Primary Identity */

	public Long       getPrimaryKey()
	{
		return primaryKey;
	}

	private Long primaryKey;

	public void       setPrimaryKey(Long pk)
	{
		if((this.primaryKey != null) && (pk == null))
			throw new IllegalArgumentException(
			  "Primary key of Unity may not be set to undefined!"
			);

		if((this.primaryKey != null) && !this.primaryKey.equals(pk))
			throw new IllegalArgumentException(
			  "Primary key of Unity may not be changed!"
			);

		this.primaryKey = pk;
	}


	/* Unity Interface */

	public UnityType  getUnityType()
	{
		return unityType;
	}

	private UnityType unityType;

	public void       setUnityType(UnityType unityType)
	{
		if(unityType == null)
			throw new IllegalArgumentException();

		this.unityType = unityType;
	}

	public String     getOxSearch()
	{
		return oxSearch;
	}

	private String oxSearch;

	public void       setOxSearch(String oxSearch)
	{
		this.oxSearch = oxSearch;
	}

	/**
	 * Property mapped to Hibernate with special
	 * {@link OxBytesType} strategy class.
	 */
	public OxBytes    getOxBytes()
	{
		return oxBytes;
	}

	private OxBytes oxBytes;

	public void       setOxBytes(OxBytes oxBytes)
	{
		this.oxBytes = oxBytes;
	}


	/* Tx-Entity Interface */

	/**
	 * Transaction number of unified mirror must
	 * have the same value as the cause instance.
	 */
	public Long getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	public void setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}


	/* Object Interface */

	public boolean    equals(Object u)
	{
		if(this == u)
			return true;

		if(!(u instanceof Unity))
			return false;

		Long k0 = this.getPrimaryKey();
		Long k1 = ((Unity)u).getPrimaryKey();

		return (k0 != null) && k0.equals(k1);
	}

	public int        hashCode()
	{
		Long k0 = this.getPrimaryKey();

		return (k0 == null)?(0):(k0.hashCode());
	}

	public String     toString()
	{
		String pk = (getPrimaryKey() == null)?("unsaved"):
		  (getPrimaryKey().toString());

		String ut = (getUnityType() == null)
		  ?("Unity Type undefined")
		  :(getUnityType().toString());

		return String.format("Unity [%s] with %s", pk, ut);
	}
}