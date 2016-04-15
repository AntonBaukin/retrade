package com.tverts.shunts.service;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShunt;
import com.tverts.shunts.SelfShuntReference;


/**
 * This class links {@link SelfShuntsSet} with
 * {@link SelfShuntReference} object.
 *
 * These two interfaces have differ meaning in
 * the system. References allow to flexibly
 * collect the shunt units in the Spring
 * configuration file. The set of shunts is the
 * resulting object used by the system internals.
 *
 * So, this class binds the external view on the
 * shunts collection with the internal implementation.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SelfShuntsRefsSet
       extends    SelfShuntsSetBase
       implements SelfShuntReference
{
	/* public: SelfShuntsSet interface */

	public List<SelfShunt>    listShunts()
	{
		return this.dereferObjects();
	}


	/* public: SelfShuntReference interface */

	public List<SelfShunt>    dereferObjects()
	{
		SelfShuntReference ref = getReference();

		if(ref == null) return Collections.emptyList();
		return ref.dereferObjects();
	}


	/* public: SelfShuntsRefsSet (bean) interface */

	public SelfShuntReference getReference()
	{
		return reference;
	}

	public void setReference(SelfShuntReference reference)
	{
		this.reference = reference;
	}


	/* private: aggregated shunts reference */

	private SelfShuntReference reference;
}