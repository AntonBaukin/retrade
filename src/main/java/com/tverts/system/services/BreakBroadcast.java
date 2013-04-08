package com.tverts.system.services;

/**
 * Throw this runtime Exception from
 * a service while processing a broadcast
 * event to stop the processing.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class BreakBroadcast extends RuntimeException
{}