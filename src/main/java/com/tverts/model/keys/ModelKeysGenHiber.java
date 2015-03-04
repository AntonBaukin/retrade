package com.tverts.model.keys;

/* com.tverts: hibery */

import com.tverts.endure.keys.KeysContextStruct;
import com.tverts.endure.keys.KeysGenerator;
import com.tverts.endure.keys.KeysPoint;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Model keys generator creating keys via Hibernate
 * Sequence generator named {@link KeysPoint#GEN_MODEL}.
 *
 * @author anton.baukin@gmail.com
 */
public class ModelKeysGenHiber extends ModelKeysGenBase
{
	/* protected: Model Keys Generator Base */

	protected String nextModelKey(ModelBean mb)
	{
		if(gen == null)
			gen = accessKeysGen();

		return keyToString(gen.createPrimaryKey(createKeyCtxStruct(mb)));
	}

	/* protected: generator access */

	protected KeysContextStruct createKeyCtxStruct(ModelBean mb)
	{
		return new KeysContextStruct(mb.getClass()).
		  setSavedInstance(mb);
	}

	protected String            keyToString(Object key)
	{
		if(!(key instanceof Long))
			throw EX.state("Hibernate Keys Generator returned not a Long key!");
		return Long.toHexString((Long)key).toUpperCase();
	}

	protected KeysGenerator     accessKeysGen()
	{
		return KeysPoint.getInstance().
		  getGenerator(KeysPoint.GEN_MODEL);
	}

	protected volatile KeysGenerator gen;
}