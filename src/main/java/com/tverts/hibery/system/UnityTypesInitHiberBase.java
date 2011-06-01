package com.tverts.hibery.system;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.action;

/* com.tverts: endure */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.GetUnityType;
import com.tverts.endure.core.SaveUnityType;
import com.tverts.endure.core.UnityTypesInitBase;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: support */

import com.tverts.support.LU;
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
	protected void ensureEntries(List<ParseEntry> pes)
	{
		GetUnityType getUnityType = bean(GetUnityType.class);

		for(ParseEntry pe : pes)
		{
			//?: {has no type class found}
			if(pe.typeClass == null)
				throw new IllegalStateException(String.format(
				  "Can't ensure Unity Type of undefined class! " +
				  "Type name '%s', type class '%s'",

				  pe.typeName, pe.className
				));


			//?: {has no type name}
			if(SU.sXe(pe.typeName))
				throw new IllegalStateException(String.format(
				  "Can't ensure Unity Type of undefined type name! " +
				  "Type class '%s'", pe.className
				));


			//~: load the unity type
			if(pe.unityType == null)
			  pe.unityType = getUnityType.
			    findUnityType(pe.typeClass, pe.typeName);

			//?: {not found it} create it
			if(pe.unityType == null)
				createUnityType(pe);

			//?: {not saved yet} save it
			if(pe.unityType.getPrimaryKey() == null)
			{
				//!: save unity type
				action(SaveUnityType.SAVE, pe.unityType).run();

				if(LU.isI(getLog())) LU.I(getLog(), String.format(
				  "Saved new system Unity Type '%s' " +
				  "for class [%s] having type flag [%c].",

				  pe.unityType.getTypeName(),
				  pe.unityType.getTypeClass().getName(),
				  pe.unityType.getTypeFlag()));
			}
		}
	}

	protected void createUnityType(ParseEntry pe)
	{
		pe.unityType = new UnityType();

		//type name
		pe.unityType.setTypeName(pe.typeName);

		//type class
		pe.unityType.setTypeClass(pe.typeClass);

		//type flag
		if(pe.typeFlag != null)
			pe.unityType.setTypeFlag(pe.typeFlag);
		else
			pe.unityType.setEntityType();

		//system flag
		pe.unityType.setSystem();
	}

	/* protected: parse entry handling */

	protected void handleEntry(ParseEntry pe)
	{
		if(getSessionFactory() == null)
			throw new IllegalStateException();

		findClassName(pe);
		super.handleEntry(pe);
	}

	protected void findClassName(ParseEntry pe)
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

	protected String getLog()
	{
		return HiberSystem.LOG_HIBER_SYSTEM;
	}
}