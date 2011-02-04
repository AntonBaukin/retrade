package com.tverts.shunts;

/* com.tverts: objects */

import com.tverts.objects.ObjectsRedirector;

/* com.tverts: shunt protocols */

import com.tverts.shunts.protocol.SeShProtocol;
import com.tverts.shunts.protocol.SeShProtocolReference;

/**
 * Aggregates a collection of the Shunt Protocol References.
 *
 * Each {@link SeShProtocol} incapsulates a method of
 * running a collection of {@link SelfShunt} Units via
 * some type of media: HTTP, JMS and other.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      SelfShuntWhats
       extends    ObjectsRedirector<SeShProtocol>
       implements SeShProtocolReference
{}