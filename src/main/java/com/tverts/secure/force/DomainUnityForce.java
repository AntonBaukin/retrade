package com.tverts.secure.force;

/* standard Java classes */

import java.util.List;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: events */

import com.tverts.event.CreatedEvent;
import com.tverts.event.Event;

/* com.tverts: endure (core + secure) */

import com.tverts.endure.United;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.secure.SecLink;
import com.tverts.endure.secure.SecRule;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sXe;



/**
 * Secure Force that acts on the defined
 * Unity Type on the Domain level.
 *
 * @author anton.baukin@gmail.com
 */
public class DomainUnityForce extends DomainKeyForce
{
	/* public: UnityForce (bean) interface */

	public Class  getTypeClass()
	{
		return typeClass;
	}

	public void   setTypeClass(Class cls)
	{
		if((cls == null) && !United.class.isAssignableFrom(cls))
			throw EX.arg("UnityForce must be configured with United class!");
		if(!DomainEntity.class.isAssignableFrom(cls))
			throw EX.arg("UnityForce must be configured with Domain Entity class!");
		this.typeClass = cls;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void   setTypeName(String typeName)
	{
		this.typeName = s2s(typeName);
	}


	/* public: SecForce interface */

	public void   init()
	{
		super.init();

		//~: ensure entities without rules
		ensureEntitiesWithoutRules();
	}


	/* public: Reactor interface */

	public void   react(Event event)
	{
		//?: {invoice created event}
		if(ise(event, CreatedEvent.class) && ist(event, getTypeClass()))
			if(isThatUnityCreated((CreatedEvent) event))
				reactUnityCreated((CreatedEvent) event);

		super.react(event);
	}


	/* protected: reactions */

	protected boolean    isThatUnityCreated(CreatedEvent e)
	{
		return isThatUnityEnsure((United)e.target());
	}

	protected boolean    isThatUnityEnsure(United u)
	{
		return isThatUnityType(u);
	}

	protected boolean    isThatUnityType(United u)
	{
		return (getTypeName() == null) || (u.getUnity() == null) ||
		  getTypeName().equals(u.getUnity().getUnityType().getTypeName());
	}

	protected void       reactUnityCreated(CreatedEvent e)
	{
		long td = System.currentTimeMillis();
		ensureUnityLink((United)e.target());

		if((System.currentTimeMillis() - td > 100L) && LU.isD(LU.LOGT))
			LU.D(LU.LOGT, getClass().getSimpleName(), ".reactUnityCreated(",
			  LU.sig(e), ") took ", LU.td(td), '!'
			);
	}

	protected void       ensureUnityLink(United u)
	{
		//~: load domain rule
		SecRule rule = loadDomainRule(
		  ((DomainEntity) u).getDomain().getPrimaryKey());

		//~: link with that invoice
		ensureLink(getSecKey(), rule, u, !isForbid());
	}

	/**
	 * In this Force the Domain is not linked with
	 * the Rule, but Invoices are.
	 */
	protected void       linkRuleWithDomain(SecRule rule)
	{}

	@SuppressWarnings("unchecked")
	protected void       ensureEntitiesWithoutRules()
	{
		//~: select entities without the rules linked
		QueryBuilder qb = new QueryBuilder();
		queryEntitiesWithoutRules(qb);

		List<Long>   ids = (List<Long>) QB(qb).list();

		if(ids.isEmpty())
			LU.D(getLog(), logsig(), " not found entities to ensure secure links");
		else
			LU.D(getLog(), logsig(), " found [", ids.size(),
			  "] entities to ensure secure links");

		//c: load them and ensure links
		for(Long id : ids)
		{
			United u = (United) session().
			  load(getTypeClass(), id);

			if(isThatUnityEnsure(u))
				ensureUnityLink(u);
		}
	}

	/**
	 * Loads keys of the entities with class configured
	 * and having no rule links creates yet (as the events
	 * were lost, or the force created after them).
	 */
	protected void       queryEntitiesWithoutRules(QueryBuilder qb)
	{
/*

 select e.id from CLS e where (e.unity.unityType = :unityType)
   and e.id not in (select l.target.id from SecLink l join l.rule r where
     (r.force = :force) and (l.key = :key))

*/

		//~: select clause
		qb.setClauseSelect("e.id");

		//~: from clause
		qb.setClauseFrom("EntityClass e");
		qb.nameEntity("EntityClass", getTypeClass());
		qb.nameEntity("SecLink", SecLink.class);


		//~: restrict by Unity Type
		if(!sXe(getTypeName())) qb.getClauseWhere().
		  addPart("e.unity.unityType = :ut").
		  param("ut", UnityTypes.unityType(getTypeClass(), getTypeName()));

		//~: restrict by secure link absence
		qb.getClauseWhere().addPart(

  "e.id not in (select l.target.id from SecLink l join l.rule r where " +
  " (r.force = :force) and (l.key = :key))"

		).
		  param("force", uid()).
		  param("key",   key(getSecKey()));
	}


	/* private: unity type */

	private Class  typeClass;
	private String typeName;
}