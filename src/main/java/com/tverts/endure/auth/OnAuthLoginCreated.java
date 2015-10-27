package com.tverts.endure.auth;

/* com.tverts: events */

import com.tverts.event.CreatedEvent;
import com.tverts.event.Event;
import com.tverts.event.Reactor;

/* com.tverts: endure (messages, persons) */

import com.tverts.endure.msg.Msg;
import com.tverts.endure.person.Persons;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Notifies System user on each login created.
 *
 * @author anton.baukin@gmail.com.
 */
public class OnAuthLoginCreated implements Reactor
{
	public void react(Event event)
	{
		if(!(event instanceof CreatedEvent))
			return;

		if(!(event.target() instanceof AuthLogin))
			return;

		AuthLogin login = (AuthLogin) event.target();

		//?: {is System user}
		if(Auth.SYSTEM_USER.equalsIgnoreCase(login.getCode()))
			return;

		//~: message the domain
		Msg.create().orange().
			types(Auth.MSG_LOGIN_CREATE).
			domain(login).title( "Добавлен пользователь [",
			  login.getCode(), "]", SU.catif(login.getPerson(),
			   ": ", Persons.name(login.getPerson()))
			).send();
	}
}
