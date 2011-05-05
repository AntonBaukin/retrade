package com.tverts.endure.keys;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* com.tverts: servlet (listeners) */

import com.tverts.servlet.listeners.ServletContextListenerBase;

/* com.tverts: support */

import static com.tverts.support.SU.sXe;

/**
 * TODO comment KeysPointActivator
 *
 * @author anton.baukin@gmail.com
 */
public class   KeysPointActivator
       extends ServletContextListenerBase
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		bindKeysGenerators();
	}

	public void contextDestroyed(ServletContextEvent sce)
	{}

	/* protected: keys generators binding */

	protected void bindKeysGenerators()
	{
		HashMap<String, KeysGenerator> generators =
		  new HashMap<String, KeysGenerator>();

		for(KeysGeneratorBinder binder :
		      KeysPoint.getInstance().getGeneratorBinders())
		{
			//?: {has no generator name}
			if(sXe(binder.getGeneratorName()))
				throw new IllegalArgumentException(
				  "Keys Generator (Binder) name is not specified!");

			//?: {had already bound this name}
			if(generators.containsKey(binder.getGeneratorName()))
				throw new IllegalArgumentException(String.format(
				  "Keys Generator (Binder) name '%s' is already bound!",
				  binder.getGeneratorName()));

			//~: do bind
			binder.bindGenerator();

			//?: {couldn't bind}
			if(binder.getGeneratorBound() == null)
				throw new IllegalArgumentException(String.format(
				  "Keys Generator Binder with name '%s' could not" +
				  "bound the generator!", binder.getGeneratorName()));

			generators.put(binder.getGeneratorName(),
			  binder.getGeneratorBound());
		}

		//!: assign the generators bound to the keys point
		assignKeysGenerators(generators);
	}

	protected void assignKeysGenerators
	  (Map<String, KeysGenerator> generators)
	{
		//~: the the whole mapping
		KeysPoint.getInstance().setGenerators(generators);

		assignMajorKeysGenerators(generators);
	}

	protected void assignMajorKeysGenerators
	  (Map<String, KeysGenerator> generators)
	{
		//?: {has no primary generator}
		if((KeysPoint.getInstance().getPrimaryGenerator() == null) &&
		   generators.containsKey(KeysPoint.GEN_PRIME)
		  )
			KeysPoint.getInstance().setPrimaryGenerator(
			  generators.get(KeysPoint.GEN_PRIME));

		//?: {has no other generator}
		if((KeysPoint.getInstance().getOtherGenerator() == null) &&
		   generators.containsKey(KeysPoint.GEN_OTHER)
		  )
			KeysPoint.getInstance().setOtherGenerator(
			  generators.get(KeysPoint.GEN_OTHER));
	}
}