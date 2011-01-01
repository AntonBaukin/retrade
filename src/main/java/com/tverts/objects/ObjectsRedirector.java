package com.tverts.objects;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a list references. May be used as a plain
 * intermediate level of references tree, or as a
 * base class for aome advanced referencing.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      ObjectsRedirector<O>
       implements ObjectsReference<O>
{
	/* public: constructor */

	public ObjectsRedirector()
	{
		this.references = Collections.emptyList();
	}

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
	public List<ObjectsReference<O>>
	               getReferences()
	{
		return references;
	}

	/**
	 * Saves a copy of the references list provided.
	 */
	public void    setReferences(List<ObjectsReference<O>> references)
	{
		if((references == null) || references.isEmpty())
			this.references = Collections.emptyList();
		else
			this.references = Collections.unmodifiableList(
			  new ArrayList<ObjectsReference<O>>(references));
	}

	/* private: the collection of references */

	private List<ObjectsReference<O>> references;
}