package com.tverts.system.services;

/* com.tverts: objects */

import com.tverts.objects.FixedObjectsRedirector;

/* tverts.com: system */

import com.tverts.system.Service;
import com.tverts.system.ServiceReference;

/**
 * Intermediate class to store references to the services
 * or nested collections of references to the services.
 *
 * Note that the services are collected at the configuration
 * time, when the list of root references is provided.
 * Consider {@link FixedObjectsRedirector}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      ServicesList
       extends    FixedObjectsRedirector<Service>
       implements ServiceReference
{}