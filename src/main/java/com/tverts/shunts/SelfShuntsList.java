package com.tverts.shunts;

/* com.tverts: objects */

import com.tverts.objects.FixedObjectsRedirector;

/**
 * Holds a list of references to actual or
 * intermediate shunt units.
 *
 * Note that the shunts references are collected
 * when setting the list of root references via
 * {@link #setReferences(java.util.List)}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      SelfShuntsList
       extends    FixedObjectsRedirector<SelfShunt>
       implements SelfShuntReference
{}