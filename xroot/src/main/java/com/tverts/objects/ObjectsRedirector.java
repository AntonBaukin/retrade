package com.tverts.objects;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a list of references. May be used as a plain
 * intermediate level of references tree, or as a
 * base class for some advanced referencing.
 *
 * @author anton.baukin@gmail.com
 */
public class      ObjectsRedirector<O>
       implements ObjectsReference<O>
{
	/* public: ObjectsReference interface */

	public List<O> dereferObjects()
	{
		List<O> res = new ArrayList<O>(references.size());

		for(ObjectsReference<O> ref : getReferences())
			res.addAll(ref.dereferObjects());
		return res;
	}


	/* public: ObjectsRedirector interface */

	/**
	 * Returns a read-only list of the aggregated references.
	 */
	public List<ObjectsReference<O>> getReferences()
	{
		return references;
	}

	/**
	 * Saves a copy of the references list provided.
	 */
	public void setReferences(List<ObjectsReference<O>> references)
	{
		if((references == null) || references.isEmpty())
			references = Collections.emptyList();

		this.references = prepareReferences(references);
	}


	/* protected: de-referencing */

	protected List<ObjectsReference<O>> prepareReferences
	  (List<ObjectsReference<O>> references)
	{
		return Collections.unmodifiableList(
		  new ArrayList<ObjectsReference<O>>(references));
	}


	/* private: the collection of references */

	private List<ObjectsReference<O>> references =
	  Collections.emptyList();;
}