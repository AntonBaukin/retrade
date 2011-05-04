package com.tverts.endure.keys;

public class KeysPoint
{
	/* public: Singleton */

	public static KeysPoint getInstance()
	{
		return INSTANCE;
	}

	private static final KeysPoint INSTANCE =
	  new KeysPoint();

	protected KeysPoint()
	{}

	/* public: access point structures */

	public KeysGenerator getFacadeGenerator()
	{
		return facadeGenerator;
	}

	public void          setFacadeGenerator(KeysGenerator gen)
	{
		this.facadeGenerator = gen;
	}

	public KeysGenerator getPrimaryGenerator()
	{
		return primaryGenerator;
	}

	public void          setPrimaryGenerator(KeysGenerator gen)
	{
		if(this.primaryGenerator != null)
			throw new IllegalStateException();
		this.primaryGenerator = gen;
	}

	/* public: access point support */

	public static KeysGenerator facadeGenerator()
	{
		KeysGenerator gen = getInstance().getFacadeGenerator();

		if(gen == null) throw new IllegalStateException();
		return gen;
	}

	public static KeysGenerator primaryGenerator()
	{
		KeysGenerator gen = getInstance().getPrimaryGenerator();

		if(gen == null) throw new IllegalStateException();
		return gen;
	}

	/* private: point structures */

	private volatile KeysGenerator facadeGenerator;
	private volatile KeysGenerator primaryGenerator;
}