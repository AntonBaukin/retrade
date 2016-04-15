package com.tverts.exec;

/* com.tverts: objects */

import com.tverts.objects.ObjectsRedirector;


/**
 * A list of references to {@link Executor}s.
 *
 * @author anton.baukin@gmail.com
 */
public class      ExecutorsList
       extends    ObjectsRedirector<Executor>
       implements ExecutorReference
{}