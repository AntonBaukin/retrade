package com.tverts.hibery.system;

/* Hibernate Persistence Layer */

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;


/**
 * Integrates Hibernate System with the application.
 *
 * @author anton.baukin@gmail.com
 */
public class HiberSystemIntegration implements Integrator
{
	public void integrate(
	    Configuration cfg, SessionFactoryImplementor sf,
	    SessionFactoryServiceRegistry sr)
	{
		//~: save the configuration
		HiberSystem.getInstance().setConfiguration(cfg);

		//~: register the event listeners
		registerEventListeners(
			sr.getService(EventListenerRegistry.class)
		);
	}

	public void integrate(
	   MetadataImplementor metadata,
	   SessionFactoryImplementor sf,
	   SessionFactoryServiceRegistry sr)
	{}

	public void disintegrate(
	  SessionFactoryImplementor sf,
	  SessionFactoryServiceRegistry sr)
	{}


	/* protected: integration */

	@SuppressWarnings("unchecked")
	protected void registerEventListeners(EventListenerRegistry er)
	{
		er.appendListeners(EventType.SAVE_UPDATE, SetTxHiberyListener.class);
	}
}