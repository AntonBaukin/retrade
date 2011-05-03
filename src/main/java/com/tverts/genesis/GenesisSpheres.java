package com.tverts.genesis;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Collects root {@link GenesisReference}s to create
 * a list of {@link GenesisSphere} instabces.
 *
 * @author anton.baukin@gmail.com
 */
public class GenesisSpheres
{
	/* public: genesis references access */

	/**
	 * Creates {@link GenesisSphere}s with the configured
	 * {@link GenesisReference} roots.
	 */
	public List<GenesisSphere>
	            createGenesisSpheres()
	{
		ArrayList<GenesisSphere> res  =
		  new ArrayList<GenesisSphere>(getReferences().size());

		for(GenesisReference ref : getReferences())
			res.add(new GenesisSphere(ref));
		return res;
	}

	/**
	 * Returns a read-only collection of currently set
	 * root {@link GenesisReference} instances. Each will
	 * create a {@link GenesisSphere}.
	 */
	public List<GenesisReference>
	            getReferences()
	{
		return references;
	}

	public void setReferences(List<GenesisReference> refs)
	{
		if((refs == null) || refs.isEmpty())
			this.references = Collections.emptyList();
		else
			this.references = Arrays.asList(
			  refs.toArray(new GenesisReference[refs.size()]));
	}

	/* private: references */

	private List<GenesisReference> references =
	  Collections.emptyList();
}