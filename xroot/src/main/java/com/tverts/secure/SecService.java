package com.tverts.secure;

/* standard Java classes */

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: system services */

import com.tverts.support.SU;
import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;
import com.tverts.system.services.Servicer;
import com.tverts.system.services.events.SystemReady;

/* com.tverts: secure */

import com.tverts.secure.force.SecForce;
import com.tverts.secure.force.SecForceRef;
import com.tverts.secure.force.SecForces;

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

		//~: map the forces
		List<SecForce> forlst = this.forces.dereferObjects();
		this.formap = new HashMap<String, SecForce>(forlst.size());
		for(SecForce f : forlst)
			this.formap.put(f.uid(), f);
	}


	/* public: Reactor interface */

	public void react(com.tverts.event.Event event)
	{
//		long td = System.currentTimeMillis();

		for(SecForce f : forces.dereferObjects())
		{
//			long xd = System.currentTimeMillis();

			f.react(event);

//			if((System.currentTimeMillis() - xd > 100L) && LU.isD(LU.LOGT))
//				LU.D(LU.LOGT, "secure force ", f.getClass().getSimpleName(),
//				  " uid [", f.uid(), "] took ", LU.td(xd), '!'
//				);
		}

//		if((System.currentTimeMillis() - td > 200L) && LU.isD(LU.LOGT))
//		  LU.D(LU.LOGT, "secure for event ", LU.sig(event), " took ", LU.td(td));
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

	public void       setForces(SecForceRef forces)
	{
		if(forces == null) throw new IllegalArgumentException();
		this.forces = forces;
	}


	/* public: SecService (support) interface */

	public String[] forces()
	{
		return formap.keySet().
		  toArray(new String[formap.size()]);
	}

	public SecForce force(String uid)
	{
		return this.formap.get(uid);
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
			  "There are Secure Rules having unknown forces: [",
			  SU.scats("], [", found), "]!"
			));
	}


	/* private: security forces */

	private SecForceRef           forces = new SecForces();
	private Map<String, SecForce> formap = new HashMap<String, SecForce>();
}