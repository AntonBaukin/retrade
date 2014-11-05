package com.tverts.endure.core;

/* Java */

import java.util.List;

/* Spring Framework */

import com.tverts.secure.SecPoint;
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
}