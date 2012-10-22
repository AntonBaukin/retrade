package com.tverts.system.zservices;

/* com.tverts: objects */

import com.tverts.objects.FixedObjectsRedirector;


/**
 * Intermediate class to store references to the services
 * or nested collections of references to the services.
 *
 * Note that the services are collected at the configuration
 * time, when the list of root references is provided.
 * Consider {@link FixedObjectsRedirector}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      ServicesList
       extends    FixedObjectsRedirector<Service>
       implements ServiceReference
{}