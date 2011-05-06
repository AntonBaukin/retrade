package com.tverts.endure.keys;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO comment KeysPoint
 *
 * @author anton.baukin@gmail.com
 */
public class KeysPoint
{
	/* public: the names of major generators */

	public static final String GEN_PRIME = "primary";

	public static final String GEN_OTHER = "other";

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

	public KeysGenerator getOtherGenerator()
	{
		return otherGenerator;
	}

	public void          setOtherGenerator(KeysGenerator gen)
	{
		this.otherGenerator = gen;
	}

	public Map<String, KeysGenerator>
	                     getGenerators()
	{
		return generators;
	}

	public void          setGenerators
	  (Map<String, KeysGenerator> generators)
	{
		if((generators == null) || generators.isEmpty())
			generators = Collections.emptyMap();
		else
			generators = Collections.unmodifiableMap(
			  Collections.synchronizedMap(
			    new HashMap<String, KeysGenerator>(generators)));

		this.generators = generators;
	}

	public List<KeysGeneratorBinder>
	                     getGeneratorBinders()
	{
		return generatorBinders;
	}

	public void          setGeneratorBinders
	  (List<KeysGeneratorBinder> binders)
	{
		if((binders == null) || binders.isEmpty())
			binders = Collections.emptyList();
		else
			binders = Collections.unmodifiableList(
			  new ArrayList<KeysGeneratorBinder>(binders));

		this.generatorBinders = binders;
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

	public static KeysGenerator otherGenerator()
	{
		KeysGenerator gen = getInstance().getOtherGenerator();

		if(gen == null) throw new IllegalStateException();
		return gen;
	}

	/* private: point structures */

	private KeysGenerator facadeGenerator;
	private KeysGenerator primaryGenerator;
	private KeysGenerator otherGenerator;

	/* private: generators & binders */

	private Map<String, KeysGenerator> generators =
	  Collections.emptyMap();

	private List<KeysGeneratorBinder>  generatorBinders =
	  Collections.emptyList();
}