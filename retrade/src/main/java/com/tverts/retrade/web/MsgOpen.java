package com.tverts.retrade.web;

/* com.tverts: endure, messages */

import com.tverts.endure.PrimaryIdentity;
import com.tverts.endure.msg.Message;
import com.tverts.endure.msg.Msg;
import com.tverts.endure.msg.MsgAdapter;
import com.tverts.endure.msg.MsgScript;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Message scripting adapter that invokes
 * 'retrade_msg_open()' function with
 * the primary key of the entity.
 *
 * @author anton.baukin@gmail.com
 */
public class MsgOpen implements MsgAdapter, MsgScript
{
	/* Message Adapter */

	/**
	 * Takes single parameter of the primary key
	 * long, or {@link PrimaryIdentity} instance.
	 */
	public void   adapt(Message msg, Object... params)
	{
		EX.assertx(params.length == 1,
		  "Entity argument is not given!");

		//~: obtain the primary key
		Long id = null;
		if(params[0] instanceof Long)
			id = (Long)params[0];
		else if(params[0] instanceof PrimaryIdentity)
			id = ((PrimaryIdentity)params[0]).getPrimaryKey();

		//?: {has no primary key}
		if(id == null)
			throw EX.ass("Entity primary key is undefined!");

		//~: save the primary key value
		Msg.create(msg).data("PrimaryIdentity", Long.toString(id));
	}


	/* Message Script */

	public String makeScript(Message msg)
	{
		//~: get the entity key
		String id = EX.asserts(
		  EX.assertn(msg.getData()).get("PrimaryIdentity"),
		  "Entity primary key was not saved in the message data!"
		);

		return SU.cats("retrade_msg_open(", id, ")");
	}
}