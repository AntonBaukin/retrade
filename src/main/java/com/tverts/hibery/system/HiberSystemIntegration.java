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
		//HINT: in present Hibernate implementation we must call
		//  SetTx listener before default SAVE listener. Otherwise,
		//  save state would be reserved, and later updates ineffective.
		//  Additional SAVE_UPDATE listener is used when cascading.
		//  SetTx listener has enough performance to ignore repeated calls.

		er.prependListeners(EventType.SAVE, SetTxHiberyListener.class);
		er.prependListeners(EventType.UPDATE, SetTxHiberyListener.class);
		er.prependListeners(EventType.SAVE_UPDATE, SetTxHiberyListener.class);
	}
}