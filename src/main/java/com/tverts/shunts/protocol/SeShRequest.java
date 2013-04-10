package com.tverts.shunts.protocol;

/**
 * A request to the Self Shunt Service.
 * The servlet is invoked with this object
 * as the parameter.
 *
 * The request is intended to run a single
 * shunt unit. {@link SeShResponse}
 * object always comes as the result of the
 * call.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SeShRequest
       extends   java.io.Serializable
{
	/* public: SeShRequest interface */

	/**
	 * Self Shunt Key is a unique identifier of a Shunt Unit
	 * within {@link com.tverts.shunts.SelfShuntPoint}.
	 * The keys are originated from the point's shunts set.
	 *
	 * Initial request has no this meaning on the keys, that
	 * keys are not registered in the system.
	 */
	public Object getSelfShuntKey();

	/**
	 * Provides the ID of Self-Shunt Context
	 * associated with this request.
	 *
	 * (Assigned by the protocol.)
	 */
	public String getContextUID();

	public void   setContextUID(String uid);
}