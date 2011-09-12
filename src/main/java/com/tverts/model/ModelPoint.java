package com.tverts.model;

/**
 * Model point is a single entry point to the user'
 * data model beans. It aggregates strategies to
 * store and control the model beans.
 *
 * Note that model point is itself a Java Bean
 * object what is not exposed in it's interface.
 *
 * Model point instance may be stored in several
 * ways to make it accessible from each application
 * running on the server. The simplest way is to
 * save it as a web session attribute.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelPoint extends ModelStore
{}