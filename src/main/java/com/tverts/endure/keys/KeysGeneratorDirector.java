package com.tverts.endure.keys;

/* com.tverts: support */

import static com.tverts.support.OU.cls;

/**
 * TODO comment KeysGeneratorDirector
 *
 * @author anton.baukin@gmail.com
 */
public abstract class KeysGeneratorDirector
       implements     KeysGenerator
{
	/* public: KeysGenerator interface */

	public Object    createPrimaryKey(KeysContext context)
	{
		if(context == null) throw new IllegalArgumentException();

		KeysGenerator gen = selectGenerator(context);
		return (gen != null)?(gen.createPrimaryKey(context)):
		  (generateDefault(context));
	}

	/* protected: generator selection */

	protected abstract KeysGenerator
	                 selectGenerator(KeysContext context);

	protected Object generateDefault(KeysContext context)
	{
		throw new IllegalStateException(String.format(
		  "No generator found for class [%s]",
		  cls(context.getSavedInstance(), context.getKeysClass())
		));
	}
}