package com.tverts.secure;

/* standard Java classes */

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* com.tverts: system services */

import com.tverts.secure.force.SecForceRef;
import com.tverts.secure.force.SecForces;
import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;
import com.tverts.system.services.Servicer;
import com.tverts.system.services.events.SystemReady;

/* com.tverts: secure */

import com.tverts.secure.force.SecForce;

/* com.tverts: events */

import com.tverts.event.Reactor;
import com.tverts.event.ReactorRef;

/* com.tverts: system (transactions) */

import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: support */

import static com.tverts.support.SU.cats;
import static com.tverts.support.SU.sXe;


/**
 * Security Service includes Security Force
 * ({@link SecForce}) strategies and implements
 * task related to system security.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SecService
       extends    ServiceBase
       implements Reactor
{
	public static final SecService INSTANCE =
	  new SecService();

	public static SecService getInstance()
	{
		return INSTANCE;
	}


	/* public: Service interface */

	public void service(Event event)
	{
		if(event instanceof SystemReady)
			onSystemReady();
	}

	public void init(Servicer servicer)
	{
		super.init(servicer);

		//~: inspect the rules existing
		checkRules();
	}


	/* public: Reactor interface */

	public void react(com.tverts.event.Event event)
	{
		for(SecForce f : forces.dereferObjects())
			f.react(event);
	}


	/* public: SecService (bean) interface */

	public ReactorRef getReactorRef()
	{
		return new ReactorRef()
		{
			public List<Reactor> dereferObjects()
			{
				return Collections.<Reactor> singletonList(SecService.this);
			}
		};
	}

	public void setForces(SecForceRef forces)
	{
		if(forces == null) throw new IllegalArgumentException();
		this.forces = forces;
	}


	/* protected: security implementation */

	protected void onSystemReady()
	{
		initForces();
	}

	protected void initForces()
	{
		for(SecForce f : forces.dereferObjects())
			f.init();
	}

	protected void checkUIDs(Set<String> uids)
	{
		for(SecForce f : forces.dereferObjects())
		{
			if(sXe(f.uid()))
				throw new IllegalStateException(String.format(
				  "Security Force with class '%s' has no UID assigned!",
				  f.getClass().getName()
				));

			if(uids.contains(f.uid()))
				throw new IllegalStateException(String.format(
				  "Security Force with class '%s' has UID [%s] already assigned!",
				  f.getClass().getName(), f.uid()
				));

			uids.add(f.uid());
		}
	}

	@SuppressWarnings("unchecked")
	protected void checkRules()
	{
		//~: check and collect UIDs
		Set<String> forces = new HashSet<String>(17);
		checkUIDs(forces);

		// select distinct r.force from SecRule r

		Set<String> found = new HashSet<String>((List<String>) txSession().
		  createQuery("select distinct r.force from SecRule r").list());

		//?: {has unknown forces}
		found.removeAll(forces);
		if(!found.isEmpty())
			throw new IllegalStateException(cats(
			  "There are Secure Rules having unknown forces: ", found
			));
	}


	/* private: security forces */

	private SecForceRef forces = new SecForces();
}