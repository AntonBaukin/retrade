package com.tverts.hibery.system;

/* Hibernate Persistence Layer */

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/* com.tverts: hibery (events) */

import com.tverts.hibery.system.events.OnPostLoadEvent;


/**
 * Integrates Hibernate System with the application.
 *
 * @author anton.baukin@gmail.com
 */
public class HiberSystemIntegration implements Integrator
{
	/* Integrator */

	public void integrate(Metadata metadata, SessionFactoryImplementor sf,
	  SessionFactoryServiceRegistry sr)
	{
		//~: save the configuration
		HiberSystem.getInstance().setServiceRegistry(sr);
		HiberSystem.getInstance().setMetadata(metadata);

		//~: register the event listeners
		registerEventListeners(
			sr.getService(EventListenerRegistry.class)
		);
	}

	public void disintegrate(
	  SessionFactoryImplementor sf,
	  SessionFactoryServiceRegistry sr)
	{}


	/* protected: integration */

	@SuppressWarnings("unchecked")
	protected void registerEventListeners(EventListenerRegistry er)
	{
		er.appendListeners(EventType.POST_LOAD, new OnPostLoadEvent());
	}
}