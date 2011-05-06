package com.tverts.endure.keys;

/* com.tverts: endure */

import com.tverts.endure.PrimaryIdentity;

/**
 * Default facade generator selecting the generator
 * from the collection of {@link KeysPoint}.
 *
 * @author anton.baukin@gmail.com
 */
public class KeysPointGenerator extends KeysGeneratorDirector
{
	/* protected: KeysGeneratorDirector interface */

	protected KeysGenerator selectGenerator(KeysContext context)
	{
		Class cls = null;

		//?: {has specific keys class defined}
		if(context.getKeysClass() != null)
			cls = context.getKeysClass();

		//?: {has saved instance} use it's class
		if((cls == null) && (context.getSavedInstance() != null))
			cls = context.getSavedInstance().getClass();

		//?: {not found any class} do not fallback: error
		if(cls == null) return null;

		return selectGenerator(context, cls);
	}

	/* protected: generator selection */

	protected KeysGenerator selectGenerator(KeysContext ctx, Class cls)
	{
		//?: {this class is of primary generator}
		if(PrimaryIdentity.class.isAssignableFrom(cls))
			return KeysPoint.getInstance().getPrimaryGenerator();

		return KeysPoint.getInstance().getOtherGenerator();
	}
}