package com.tverts.genesis;

/* com.tverts: objects */

import com.tverts.objects.ObjectsRedirector;

/**
 * Collects root {@link GenesisReference}s to create
 * a list of {@link GenesisSphere} instances.
 *
 * @author anton.baukin@gmail.com
 */
public class      GenesisSpheres
       extends    ObjectsRedirector<GenesisSphere>
       implements GenesisSphereReference
{}