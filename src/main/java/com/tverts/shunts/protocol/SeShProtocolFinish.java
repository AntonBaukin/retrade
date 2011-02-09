package com.tverts.shunts.protocol;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;

/**
 * Cllback on the {@link SeShProtocol} finish.
 * Must be installed in the protocol instance.
 *
 * Note that once a protocol is added to the
 * processing queue of some Self Shunt Service, it's
 * {@link SeShProtocol#finishProtocol(SelfShuntReport)}
 * is always invoked in the context of the thread
 * of the shunt service.
 */
public interface SeShProtocolFinish
{
	/* public: SeShProtocolFinish interface */

	public void finishProtocol(SelfShuntReport report);
}