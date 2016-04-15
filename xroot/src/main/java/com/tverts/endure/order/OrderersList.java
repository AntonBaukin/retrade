package com.tverts.endure.order;

/* com.tverts: objects */

import com.tverts.objects.ObjectsRedirector;

/**
 * A list of references to ordering strategies.
 *
 * @author anton.baukin@gmail.com
 */
public class      OrderersList
       extends    ObjectsRedirector<Orderer>
       implements OrdererReference
{}