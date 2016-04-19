package com.tverts.endure.keys;

/* Java */

import java.util.HashMap;
import java.util.Map;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: servlet (listeners) */

import com.tverts.servlet.listeners.ContextListenerBase;
import com.tverts.servlet.listeners.PickListener;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Builds database sequences used to create
 * primary keys of the entities.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickListener(order = 100)
public class   KeysPointActivator
       extends ContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	protected void init()
	{
		bindKeysGenerators();
	}


	/* protected: keys generators binding */

	protected void bindKeysGenerators()
	{
		Map<String, KeysGenerator> gens = new HashMap<>();

		for(KeysGeneratorBinder b : KeysPoint.getInstance().getGeneratorBinders())
		{
			//?: {has no generator name}
			EX.asserts(b.getGeneratorName(),
			  "Keys Generator (Binder) name is not specified!");

			//?: {had already bound this name}
			EX.assertx(!gens.containsKey(b.getGeneratorName()),
			  "Keys Generator (Binder) name [", b.getGeneratorName(),
			  "] is already bound!");

			//~: do bind
			b.bindGenerator();

			//?: {couldn't bind}
			EX.assertn(b.getGeneratorBound(),
			  "Keys Generator Binder with name [", b.getGeneratorName(),
			  "] could not bound the generator!");

			gens.put(b.getGeneratorName(),
			  b.getGeneratorBound()
			);
		}

		//!: assign the generators bound to the keys point
		assignKeysGenerators(gens);
	}

	protected void assignKeysGenerators(Map<String, KeysGenerator> gens)
	{
		//~: the the whole mapping
		KeysPoint.getInstance().setGenerators(gens);

		assignMajorKeysGenerators(gens);
		assignFacadeKeysGenerator();
	}

	protected void assignMajorKeysGenerators(Map<String, KeysGenerator> gens)
	{
		//?: {has no primary generator}
		if((KeysPoint.getInstance().getPrimaryGenerator() == null) &&
		    gens.containsKey(KeysPoint.GEN_PRIME)
		  )
			KeysPoint.getInstance().setPrimaryGenerator(
			  gens.get(KeysPoint.GEN_PRIME));


		//?: {has no transactions numbers generator}
		if((KeysPoint.getInstance().getTxnGenerator() == null) &&
		    gens.containsKey(KeysPoint.GEN_TXN)
		  )
			KeysPoint.getInstance().setTxnGenerator(
			  gens.get(KeysPoint.GEN_TXN));


		//?: {has no other generator}
		if((KeysPoint.getInstance().getOtherGenerator() == null) &&
		    gens.containsKey(KeysPoint.GEN_OTHER)
		  )
			KeysPoint.getInstance().setOtherGenerator(
			  gens.get(KeysPoint.GEN_OTHER));
	}

	protected void assignFacadeKeysGenerator()
	{
		if(KeysPoint.getInstance().getFacadeGenerator() != null)
			return;

		KeysPoint.getInstance().setFacadeGenerator(
		  new KeysPointGenerator());
	}
}