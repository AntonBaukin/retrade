package com.tverts.endure.core;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: data */

import com.tverts.data.models.AdaptedEntitiesSelected;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Loads {@link Unity} instances and provides support
 * routines to handle them.
 *
 *
 * @author anton.baukin@gmail.com
 */
@Component("getUnity")
public class GetUnity extends GetObjectBase
{
	/* Get Unity */

	public Unity  getUnity(Long pk)
	{
		return (pk == null)?(null):
		  (Unity) session().get(Unity.class, pk);
	}

	/**
	 * Loads the actual instance having this
	 * Unity object as it's unified mirror.
	 */
	public United getUnited(Long pk)
	{
		if(pk == null) return null;

/*

select ut from Unity u join u.unityType ut
  where u.primaryKey = :primaryKey

*/
		UnityType ut = (UnityType) Q(

"select ut from Unity u join u.unityType ut\n" +
"  where u.primaryKey = :primaryKey"

		).
		  setLong("primaryKey", pk).
		  uniqueResult();

		//?: {not found it}
		if(ut == null) return null;

		//?: {not an entity type}
		if(!ut.isEntityType())
			throw new IllegalStateException(String.format(
			  "Requested United instance [%d] has Unity type not " +
			  "of an entity, but: %s!", pk, ut.toString()));

		//?: {not a united class}
		if(!United.class.isAssignableFrom(ut.getTypeClass()))
			throw new IllegalStateException(String.format(
			  "Requested United instance [%d] has Unity type not " +
			  "of United class, but: %s!", pk, ut.toString()));

		return (United) session().get(ut.getTypeClass(), pk);
	}

	public United getUnited(Unity u)
	{
		return (United) session().get(
		  u.getUnityType().getTypeClass(), u.getPrimaryKey());
	}


	/* Entities Selection */

	public int count(AdaptedEntitiesSelected mb)
	{
		//~: take the unity type
		UnityType ut = EX.assertn(
		  bean(GetUnityType.class).getUnityType(mb.getUnityType()),
		  "Unity Type [", mb.getUnityType(), "] was not found!"
		);

		QueryBuilder qb = new QueryBuilder();


		//~: from clause
		qb.nameEntity("Entity", ut.getTypeClass());
		qb.setClauseFrom("Entity e");

		//~: select clause
		qb.setClauseSelect("count(e.id)");


		//~: domain
		qb.getClauseWhere().addPart(
		  "e.domain.id = :domain"
		).
		  param("domain", mb.domain());


		//~: unity type
		qb.getClauseWhere().addPart(
		  "e.unity.unityType = :unityType"
		).
		  param("unityType", ut);


		//~: the selection set
		qb.getClauseWhere().addPart(

"e.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"
		).
		  param("set",   SU.sXs(mb.getSelSet())).
		  param("login", EX.assertn(mb.getLogin()));

		//~: model-provided restrictions
		if(mb.getQuery() != null)
			mb.getQuery().tuneQuery(qb);


		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<United> select(AdaptedEntitiesSelected mb)
	{
		//~: take the unity type
		UnityType ut = EX.assertn(
		  bean(GetUnityType.class).getUnityType(mb.getUnityType()),
		  "Unity Type [", mb.getUnityType(), "] was not found!"
		);

		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Entity", ut.getTypeClass());
		qb.setClauseFrom("Entity e");

		//~: select clause
		qb.setClauseSelect("e");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: domain
		qb.getClauseWhere().addPart(
		  "e.domain.id = :domain"
		).
		  param("domain", mb.domain());


		//~: unity type
		qb.getClauseWhere().addPart(
		  "e.unity.unityType = :unityType"
		).
		  param("unityType", ut);


		//~: the selection set
		qb.getClauseWhere().addPart(

"e.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :sset) and (ss.login.id = :login))"
		).
		  param("sset",  SU.sXs(mb.getSelSet())).
		  param("login", EX.assertn(mb.getLogin()));

		//~: model-provided restrictions
		if(mb.getQuery() != null)
			mb.getQuery().tuneQuery(qb);


		return (List<United>) QB(qb).list();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> select(Class<T> typeClass, String typeName, String selset)
	{
		EX.assertn(typeClass);

		//~: define the unity type
		UnityType ut = (typeName == null)?(null):
		  UnityTypes.unityType(typeClass, typeName);

		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Entity", typeClass);
		qb.setClauseFrom("Entity e");

		//~: select clause
		qb.setClauseSelect("e");


		//~: restrict unity type
		if(ut != null) qb.getClauseWhere().addPart(
		  "e.unity.unityType = :unityType"
		).
		  param("unityType", ut);

		//~: restrict the selection set
		qb.getClauseWhere().addPart(

"e.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :sset) and (ss.login.id = :login))"
		).
		  param("sset",  SU.sXs(selset)).
		  param("login", SecPoint.login());

		return (List<T>) QB(qb).list();
	}

	@SuppressWarnings("unchecked")
	public List<Long> selectIds(Class typeClass, String typeName, String selset)
	{
		EX.assertn(typeClass);

		//~: define the unity type
		UnityType ut = (typeName == null)?(null):
		  UnityTypes.unityType(typeClass, typeName);

		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Entity", typeClass);
		qb.setClauseFrom("Entity e");

		//~: select clause
		qb.setClauseSelect("e.id");


		//~: restrict unity type
		if(ut != null) qb.getClauseWhere().addPart(
		  "e.unity.unityType = :unityType"
		).
		  param("unityType", ut);

		//~: restrict the selection set
		qb.getClauseWhere().addPart(

"e.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :sset) and (ss.login.id = :login))"
		).
		  param("sset",  SU.sXs(selset)).
		  param("login", SecPoint.login());

		return (List<Long>) QB(qb).list();
	}


	/* Unified Attributes */

	public AttrType        getAttrType(Long pk)
	{
		return get(AttrType.class, pk);
	}

	public AttrType        getAttrType(Long domain, Long type, String name)
	{
		EX.assertn(domain);
		EX.assertn(type);
		EX.asserts(name);

/*

 from AttrType where (domain.id = :domain) and
   (attrType.id = :type) and (name = :name)

 */
		final String Q =

"from AttrType where (domain.id = :domain) and\n" +
"  (attrType.id = :type) and (name = :name)";

		return object(AttrType.class, Q, "domain", domain, "type", type, "name", name);
	}

	public List<AttrType>  getAttrTypes(Long domain, Long type)
	{
		EX.assertn(domain);
		EX.assertn(type);

/*

 from AttrType where (domain.id = :domain) and (attrType.id = :type)

 */
		final String Q =
"from AttrType where (domain.id = :domain) and (attrType.id = :type)";

		return list(AttrType.class, Q, "domain", domain, "type", type);
	}

	public List<UnityAttr> getAttrs(Long unity)
	{
		EX.assertn(unity);

// from UnityAttr where (unity.id = :unity) order by index

		final String Q =
"  from UnityAttr where (unity.id = :unity) order by index";

		return list(UnityAttr.class, Q, "unity", unity);
	}

	public List<UnityAttr> getAllAttrs(AttrType type)
	{
		EX.assertn(type);

// from UnityAttr where (attrType = :type) order by index

		final String Q =
"  from UnityAttr where (attrType = :type) order by index";

		return list(UnityAttr.class, Q, "type", type);
	}

	/**
	 * Removes all attribute values having the type given
	 * and the source reference defined --- those are
	 * copies of some original attributes.
	 */
	public void            removeSharedAttributes(AttrType type)
	{
		EX.assertn(type);

// delete from UnityAttr where (attrType = :type) and (source is not null)

		final String Q =
"  delete from UnityAttr where (attrType = :type) and (source is not null)";

		Q(Q, "type", type).executeUpdate();
	}

	/**
	 * Removes attribute values of previously array
	 * type: all the records having index >= 1.
	 */
	public void            removeArrayAttributes(AttrType type)
	{
		EX.assertn(type);

// delete from UnityAttr where (attrType = :type) and (index > 0)

		final String Q =
"  delete from UnityAttr where (attrType = :type) and (index > 0)";

		Q(Q, "type", type).executeUpdate();
	}


	/* Secured Operations */

	/**
	 * Loads the entities with the given Long (also, as a String)
	 * primary key values checking that they are in the domain
	 * specified. If the domain is undefined, domain of the
	 * current user is selected as the default.
	 *
	 * Note that it is also checked that entities do exist.
	 * The entities are selected in teh same order with
	 * the duplicates allowed.
	 */
	public <O extends DomainEntity> List<O> selectAndCheckDomain
	  (Class<O> typeClass, Long domain, Object[] ids)
	{
		EX.assertn(typeClass);

		//?: {no domain}
		if(domain == null)
			domain = SecPoint.domain();

		//?: {no keys provided}
		if((ids == null) || (ids.length == 0))
			return new ArrayList<>(0);

		//c: load the objects
		List<O> res = new ArrayList<>(ids.length);
		for(Object id : ids)
		{
			if(id instanceof CharSequence)
				id = Long.parseLong(id.toString());

			if(!(id instanceof Long))
				throw EX.arg("Primary key [", id, "] must be a Long integer!");

			//~: load the entity
			O o = load(typeClass, (Long)id);
			res.add(o);

			//sec: {has the same domain}
			if(!domain.equals(o.getDomain().getPrimaryKey()))
				throw EX.forbid("Accessing entity with key [", id, "] of a class [",
				  typeClass, "] is being in else Domain [",
				  o.getDomain().getPrimaryKey(), "]!");
		}

		return res;
	}
}