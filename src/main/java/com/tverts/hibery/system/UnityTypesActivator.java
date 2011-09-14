package com.tverts.hibery.system;

/* standard Java classes */

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* Spring Framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: endure */

import com.tverts.endure.UnityType;
import com.tverts.endure.types.UnityTypeStruct;
import com.tverts.endure.types.UnityTypeStructReference;

/* com.tverts: objects */

import com.tverts.objects.ObjectsReference;
import com.tverts.objects.StringsReference;

/* com.tverts: system tx */

import com.tverts.system.tx.TxPoint;

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
		List<UnityTypeStruct> uts = (getStructs() == null)
		  ?(Collections.<UnityTypeStruct> emptyList())
		  :(getStructs().dereferObjects());

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

	@Transactional
	protected void      ensureEntries
	  (Collection<ParseEntry> pes, Collection<UnityTypeStruct> structs)
	{
		//~: push default transaction context
		TxPoint.getInstance().setTxContext();

		try
		{
			//~: do ensure in the transactional context
			super.ensureEntries(pes, structs);
		}
		finally
		{
			//!: pop transaction context
			TxPoint.getInstance().setTxContext(null);
		}
	}

	/* private: unity types encoded */

	private StringsReference         unityTypes;
	private UnityTypeStructReference structs;
}