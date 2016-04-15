package com.tverts.shunts.protocol;

/**
 * Marks an initial request to the system.
 * Such requests are to initialize shunting
 * invocation protocol.
 *
 * Special implementations allow to select
 * the subsets of the shunt units to run.
 *
 * Note that initial request classes must
 * be supported on the implementation level.
 *
 * Initial requests has special notation on
 * the shunt key object. This keys must
 * unambiguously select a sequence of shunts
 * within the scope of the class of initial
 * requests.
 *
 * For example, request for a group of shunt
 * units, {@link SeShRequestGroups},
 * uses the name of the group as the key.
 * For other classes of initial request the
 * same string value as a group name may has
 * completely else meaning.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SeShRequestInitial
       extends   SeShRequest
{}