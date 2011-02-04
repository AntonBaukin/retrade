package com.tverts.shunts;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* com.tverts: shunt protocols */

import com.tverts.shunts.protocol.SeShProtocol;
import com.tverts.shunts.protocol.SeShProtocolReference;

public class      SelfShuntWhat
       implements SeShProtocolReference
{
	/* public: constructors */

	public SelfShuntWhat(SeShProtocol protocol)
	{
		if(protocol == null)
			throw new IllegalArgumentException();

		this.protocol = protocol;
	}

	/* public: SeShProtocolReference interface */

	public List<SeShProtocol> dereferObjects()
	{
		return Collections.singletonList(protocol);
	}

	/* protected: the protocol */

	protected final SeShProtocol protocol;
}