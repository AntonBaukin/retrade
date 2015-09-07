package com.tverts.hibery;

/* com.tverts: endure */

import com.tverts.endure.Ox;

/* com.tverts: objects */

import com.tverts.objects.XPoint;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Special object to store objects
 * XML-written into byte arrays.
 *
 * @author anton.baukin@gmail.com.
 */
public class OxBytes implements Ox
{
	/* public: constructor */

	public OxBytes(Object ox)
	{
		this.storage    = new Storage();
		this.storage.ox = ox;
		if(ox == null) //<-- as it was set after the update
			this.storage.updated = UPDATED;
	}

	public OxBytes(byte[] bytes)
	{
		this.storage = new Storage();
		this.storage.bytes = EX.assertn(bytes);
	}

	public OxBytes(OxBytes master)
	{
		this.storage = EX.assertn(master).storage;
	}


	/* Object Extraction */

	public Object  getOx()
	{
		//?: {has object decoded}
		if(storage.ox != null)
			return storage.ox;

		//?: {has no bytes}
		if(storage.bytes == null)
			return null;

		//?: {has updated to null}
		if(storage.bytes == LOADED)
		{
			EX.assertx(storage.updated == UPDATED);
			return null;
		}

		//~: extract bytes
		storage.ox    = decode(storage.bytes);
		storage.bytes = LOADED;

		return storage.ox;
	}

	public void    setOx(Object ox)
	{
		//HINT: when object is loaded, but not yet decoded,
		//  the bytes are defined. We mark them as loaded.
		//  This flag is then never cleared.

		storage.ox      = ox;
		storage.bytes   = (storage.bytes == null)?(null):(LOADED);
		storage.updated = UPDATED;
	}

	public void    updateOx()
	{
		//~: decode the object
		this.getOx(); //<-- for side-effect

		//HINT: if the object was updated and written
		//  back, we discard those not valid now bytes.

		//~: mark as updated
		storage.updated = UPDATED;
	}

	public boolean isUpdatedOx()
	{
		return (storage.updated == UPDATED) ||
		  //--> true, always for a new object
		  ((storage.ox != null) && (storage.bytes != LOADED));
	}


	/* Object */

	/**
	 * Note that storage doesn't support detached
	 * entities, and doesn't compare the object
	 * or the bytes by the content.
	 */
	public int     hashCode()
	{
		return storage.hashCode();
	}

	public boolean equals(OxBytes that)
	{
		return (this == that) ||
		  (this.storage == that.storage) && !isUpdatedOx();
	}

	public boolean equals(Object that)
	{
		return (this == that) ||
		  !((that == null) || !this.getClass().equals(that.getClass())) &&
		    this.equals((OxBytes)that);
	}


	/* Storage Access */

	/**
	 * Returns the original bytes, or the encoded
	 * bytes of updated object. When the object
	 * is undefined, always returns null.
	 */
	public byte[] oxBytes()
	{
		//?: {the object was loaded and not decoded}
		if(storage.bytes != LOADED)
			if(storage.bytes != null)
				return storage.bytes;

		//?: {the object is undefined}
		if(storage.ox == null)
		{
			EX.assertx(storage.updated == UPDATED);
			return null;
		}

		//?: {has updated bytes ready}
		if(storage.updated != null)
			if(storage.updated != UPDATED)
				return storage.updated;

		//~: encode the object
		return storage.updated = encode(storage.ox);
	}


	/* Internal Storage */

	protected final Storage storage;

	/**
	 * Updated marker. This marker is temporary set to
	 * {@link Storage#updated} to indicate that the storage
	 * object was updated, but not written back yet.
	 */
	protected static final byte[] UPDATED = new byte[0];

	/**
	 * Loaded marker. This marker is permanently set to
	 * the bytes after the object is decoded.
	 */
	protected static final byte[] LOADED  = new byte[0];

	protected static class Storage
	{
		/**
		 * The object decoded from the bytes.
		 * When the object is decoded, the
		 * bytes link is removed to free
		 * the memory.
		 */
		public Object ox;

		/**
		 * The original (loaded) data bytes.
		 * The link is removed when the object
		 * is updated and encoded back.
		 */
		public byte[] bytes;

		/**
		 * The bytes of encoded updated object.
		 *
		 */
		public byte[] updated;
	}


	/* protected: object coding */

	protected byte[] encode(Object ox)
	{
		EX.assertn(ox);
		return XPoint.xml().write(true, ox);
	}

	protected Object decode(byte[] bytes)
	{
		EX.assertn(bytes);
		return XPoint.xml().read(true, Object.class, bytes);
	}
}