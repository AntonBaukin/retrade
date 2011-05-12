package com.tverts.hibery.system;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* Spring Framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: endure */

import com.tverts.endure.UnityType;

/* com.tverts: objects */

import com.tverts.objects.ObjectsReference;
import com.tverts.objects.StringsReference;

/* com.tverts: support */

import static com.tverts.support.SU.sXs;
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
		List<ParseEntry> pes = parseWhole(whole);

		//?: {has no unity types coded} nothing to do
		if(pes.isEmpty()) return;

		//~: create the type instances if needed
		ensureEntries(pes);

		//~: register the unity types
		registerUnityTypes(pes);
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

	/* protected: UnityTypesInitHiberBase interface */

	@Transactional
	protected void ensureEntries(List<ParseEntry> pes)
	{
		super.ensureEntries(pes);
	}

	/* private: unity types encoded */

	private StringsReference unityTypes;
}