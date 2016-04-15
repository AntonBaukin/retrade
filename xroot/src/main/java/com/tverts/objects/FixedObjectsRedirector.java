package com.tverts.objects;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/**
 * This variant of {@link ObjectsRedirector} does
 * actual de-referencing not at the call time of
 * {@link #dereferObjects()}, but when the initial
 * list of references is provided at call of
 * {@link #setReferences(List)}.
 *
 * Hence all the references provided must be ready
 * at the configuration time!
 *
 * Note that this implementation is not thread-safe.
 * Method {@link #setReferences(List)} is designed
 * to be invoked in the configuration (system
 * startup) time (better only once).
 *
 * @author anton.baukin@gmail.com
 */
public class   FixedObjectsRedirector<O>
       extends ObjectsRedirector<O>
{
	/* public: ObjectsReference interface */

	public List<O> dereferObjects()
	{
		return getDereferedObjects();
	}


	/* public: ObjectsRedirector interface */

	/**
	 * Saves a copy of the references list provided. At the call
	 * time collects all the objects are being referenced!
	 */
	public void    setReferences(List<ObjectsReference<O>> references)
	{
		super.setReferences(references);
		this.derefered = dereferObjectsPrepare();
	}


	/* protected: de-referencing */

	protected List<O> getDereferedObjects()
	{
		return derefered;
	}

	protected List<O> dereferObjectsActual()
	{
		return super.dereferObjects();
	}

	protected List<O> dereferObjectsPrepare()
	{
		List<O> res = dereferObjectsActual();
		return (res != null)?(res):
		  Collections.<O> emptyList();
	}

	/* private: prepared de-referenced list */

	private List<O> derefered =
	  Collections.emptyList();
}