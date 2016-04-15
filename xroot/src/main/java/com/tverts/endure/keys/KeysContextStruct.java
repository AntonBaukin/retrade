package com.tverts.endure.keys;

/**
 * COMMENT KeysContextStruct
 *
 * @author anton.baukin@gmail.com
 */
public class KeysContextStruct implements KeysContext
{
	/* public: constructor */

	public KeysContextStruct(Class keysClass)
	{
		this.keysClass = keysClass;
	}

	/* public: KeysContext interface */

	public Class  getKeysClass()
	{
		return keysClass;
	}

	public Object getSavedInstance()
	{
		return savedInstance;
	}

	/* public: KeysContextStruct interface */

	public KeysContextStruct setSavedInstance(Object instance)
	{
		this.savedInstance = instance;
		return this;
	}

	/* private: structure fields */

	private Class  keysClass;

	private Object savedInstance;
}