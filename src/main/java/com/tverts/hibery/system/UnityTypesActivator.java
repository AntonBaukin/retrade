package com.tverts.hibery.system;

/* standard Java classes */

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* com.tverts: (spring + tx) */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;

/* com.tverts: endure */

import com.tverts.endure.UnityType;
import com.tverts.endure.types.UnityTypeStruct;
import com.tverts.endure.types.UnityTypeStructReference;

/* com.tverts: objects */

import com.tverts.objects.ObjectsReference;
import com.tverts.objects.StringsReference;


/* com.tverts: support */

import static com.tverts.support.SU.cat;


/**
 * System startup listener that creates and registers
 * system {@link UnityType} instances.
 *
 * @author anton.baukin@gmail.com
 */
public class      UnityTypesActivator
       extends    UnityTypesInitHiberBase
       implements ServletContextListener,
                  ObjectsReference<ServletContextListener>
{
	/* public: ObjectsReference interface */

	public List<ServletContextListener> dereferObjects()
	{
		return Collections.<ServletContextListener>singletonList(this);
	}


	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		//?: {has no unity types codes} nothing to do
		if(getUnityTypes() == null) return;

		String whole = cat(null, "; ",
		  getUnityTypes().dereferObjects()).toString();

		//~: parse the encoded string
		List<ParseEntry>      pes = parseWhole(whole);

		//~: collect the descriptors
		List<UnityTypeStruct> uts;

		if(getStructs() == null)
			uts = Collections.emptyList();
		else
			uts = getStructs().dereferObjects();

		//?: {has no unity types} nothing to do
		if(pes.isEmpty() && uts.isEmpty())
			return;

		//~: create the type instances if needed
		ensureEntries(pes, uts);

		//~: register the unity types
		registerUnityTypes(pes, uts);
	}

	public void contextDestroyed(ServletContextEvent sce)
	{}


	/* public: UnityTypesActivator interface */

	public StringsReference
	            getUnityTypes()
	{
		return unityTypes;
	}

	public void setUnityTypes(StringsReference unityTypes)
	{
		this.unityTypes = unityTypes;
	}

	public UnityTypeStructReference
	            getStructs()
	{
		return structs;
	}

	public void setStructs(UnityTypeStructReference structs)
	{
		this.structs = structs;
	}


	/* protected: UnityTypesInitHiberBase interface */

	protected void      ensureEntries
	  (final Collection<ParseEntry> pes, final Collection<UnityTypeStruct> structs)
	{
		//~: do ensure in the transactional context
		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				ensureEntriesDo(pes, structs);
			}
		});
	}

	protected void      ensureEntriesDo
	  (Collection<ParseEntry> pes, Collection<UnityTypeStruct> structs)
	{
		super.ensureEntries(pes, structs);
	}


	/* private: unity types encoded */

	private StringsReference         unityTypes;
	private UnityTypeStructReference structs;
}