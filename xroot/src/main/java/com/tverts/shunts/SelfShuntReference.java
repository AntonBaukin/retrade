package com.tverts.shunts;

/* com.tverts: objects */

import com.tverts.objects.ObjectsReference;

/**
 * Defines a reference to one or more independent
 * self shunt units.
 *
 * Actual implementation of a reference may vary
 * from reporting {@code this} object to scanning
 * the class path for {@link SelfShuntUnit} classes.
 *
 * @author anton.baukin@gmail.com
 */
public interface SelfShuntReference
       extends   ObjectsReference<SelfShunt>
{}