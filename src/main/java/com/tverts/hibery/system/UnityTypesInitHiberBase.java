package com.tverts.hibery.system;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collection;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.GetUnityType;
import com.tverts.endure.core.ActUnityType;
import com.tverts.endure.types.UnityTypeStruct;
import com.tverts.endure.types.UnityTypesInitBase;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.OU;
import com.tverts.support.SU;


/**
 * Extends {@link UnityTypesInitBase} as searching for the FQN
 * of the class by it's simple name in the Hibernate mappings.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnityTypesInitHiberBase
       extends        UnityTypesInitBase
{
	/* public: UnityTypesInitHiberBase interface */

	public SessionFactory getSessionFactory()
	{
		return HiberPoint.getInstance().getSessionFactory();
	}

	/* protected: unity types storing */

	/**
	 * Loads the unity types from the database.
	 * Creates system unity type if it does not exist yet.
	 *
	 * Warning: this method must be executed in the
	 * transactional context!
	 */
	protected void      ensureEntries
	  (Collection<ParseEntry> pes, Collection<UnityTypeStruct> structs)
	{
		//~: ensure parsed entries
		for(ParseEntry pe : pes)
			ensureEntry(pe);

		//~: ensure type descriptors
		for(UnityTypeStruct s : structs)
			ensureEntry(s);
	}

	protected void      ensureEntry(Object e)
	{
		//~: find | load the unity type
		UnityType unityType = findUnityType(e);

		//?: {not found it} create it
		if(unityType == null)
			unityType = createUnityType(e);

		//?: {not saved yet} save it
		if(unityType.getPrimaryKey() == null)
			saveUnityType(unityType);
	}

	protected void      checkUnityTypeEntry(Object e)
	{
		Class     typeClass = null;
		String    typeName  = null;

		if(e instanceof ParseEntry)
		{
			typeClass = ((ParseEntry)e).typeClass;
			typeName  = ((ParseEntry)e).typeName;
		}

		if(e instanceof UnityTypeStruct)
		{
			typeClass = ((UnityTypeStruct)e).getTypeClass();
			typeName  = ((UnityTypeStruct)e).getTypeName();
		}

		//?: {has no type class found}
		if(typeClass == null)
			throw new IllegalStateException(String.format(
			  "Can't ensure Unity Type of undefined class! " +
			  "(Type name is '%s'.)", typeName
			));


		//?: {has no type name}
		if(SU.sXe(typeName))
			throw new IllegalStateException(String.format(
			  "Can't ensure Unity Type of undefined type name! " +
			  "Type class '%s'",  OU.cls(typeClass)
			));
	}

	protected UnityType findUnityType(Object entry)
	{
		if(entry instanceof ParseEntry)
			return findUnityType((ParseEntry)entry);

		if(entry instanceof UnityTypeStruct)
			return findUnityType((UnityTypeStruct)entry);

		throw new IllegalArgumentException();
	}

	protected UnityType findUnityType(ParseEntry pe)
	{
		if(pe.unityType != null)
			return pe.unityType;

		return pe.unityType = bean(GetUnityType.class).
		  findUnityType(pe.typeClass, pe.typeName);
	}

	protected UnityType findUnityType(UnityTypeStruct s)
	{
		if(s.getUnityType() != null)
			return s.getUnityType();

		UnityType r = bean(GetUnityType.class).
		  findUnityType(s.getTypeClass(), s.getTypeName());

		s.setUnityType(r);
		return r;
	}

	protected UnityType createUnityType(Object entry)
	{
		if(entry instanceof ParseEntry)
			return createUnityType((ParseEntry)entry);

		if(entry instanceof UnityTypeStruct)
			return createUnityType((UnityTypeStruct)entry);

		throw new IllegalArgumentException();
	}

	protected UnityType createUnityType(ParseEntry pe)
	{
		pe.unityType = new UnityType();

		//~: type class
		pe.unityType.setTypeClass(pe.typeClass);

		//~: type name
		pe.unityType.setTypeName(pe.typeName);

		//~: type flag
		if(pe.typeFlag != null)
			pe.unityType.setTypeFlag(pe.typeFlag);
		else
			pe.unityType.setEntityType();

		//~: system flag
		pe.unityType.setSystem();

		return pe.unityType;
	}

	protected UnityType createUnityType(UnityTypeStruct s)
	{
		UnityType ut = new UnityType();
		s.setUnityType(ut);

		//~: type class
		ut.setTypeClass(s.getTypeClass());

		//~: type name
		ut.setTypeName(s.getTypeName());

		//~: type flag
		if(s.getTypeFlag() != null)
			ut.setTypeFlag(s.getTypeFlag());
		else
			ut.setEntityType();

		//~: system flag
		ut.setSystem();

		//~: title
		ut.setTitle(s.getTitle());

		//~: title localized
		ut.setTitleLo(s.getTitleLo());

		return ut;
	}

	protected void      saveUnityType(UnityType unityType)
	{
		//!: save unity type
		actionRun(ActUnityType.SAVE, unityType);

		if(LU.isI(getLog())) LU.I(getLog(), String.format(
		  "Saved new system Unity Type '%s' " +
		  "for class [%s] having type flag [%c].",

		  unityType.getTypeName(),
		  unityType.getTypeClass().getName(),
		  unityType.getTypeFlag())
		);
	}


	/* protected: parse entry handling */

	protected void      handleEntry(ParseEntry pe)
	{
		if(getSessionFactory() == null)
			throw new IllegalStateException();

		findClassName(pe);
		super.handleEntry(pe);
	}

	protected void      findClassName(ParseEntry pe)
	{
		//?: {already have the class defined} quit
		if(pe.typeClass != null) return;

		//?: {have the class name defined} quit
		if(pe.className != null) return;

		//?: {have no simple name} nothing to do
		if(pe.simpleName == null) return;

		ArrayList<String> res = new ArrayList<String>(1);

		String className = new StringBuilder(1 + pe.simpleName.length()).
		  append('.').append(pe.simpleName).toString();

		//~: search for the entities with the same simple name
		for(String en : getSessionFactory().getAllClassMetadata().keySet())
			if(en.equals(pe.className) || en.endsWith(className))
				res.add(en);

		//?: {has more than one} warn in log
		if(res.size() > 1) LU.W(getLog(), String.format(
		  "Can't guess the full class name by it's simple name '%s': " +
		  "there are more than one class mapped in Hibernate.",

		  pe.simpleName));

		if(res.size() == 1)
			pe.className = res.get(0);
	}


	/* protected: logging */

	protected String    getLog()
	{
		return LU.getLogBased(HiberSystem.class, this);
	}
}