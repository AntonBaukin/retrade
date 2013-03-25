package com.tverts.api.support;

/* standard Java classes */

import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: api */

import com.tverts.api.core.TwoKeysObject;
import com.tverts.api.core.TxObject;
import com.tverts.api.core.XKeyPair;


/**
 * Creates and stores an information on the
 * bean class provided.
 *
 * @author anton.baukin@gmail.com
 */
public class BeanInfo
{
	/* public: constructor */

	public BeanInfo(Class type)
	  throws Exception
	{
		if(type == null) throw new IllegalArgumentException();
		this.type = type;

		//~: collect the properties
		this.properties = findProperties();

		//~: map them
		this.pmap = mapProperties();

		//~: search for the properties with x-keys
		this.xKeyPairs = findXKeyPairs();

		//~: name the x-paired properties (of x-keys)
		this.xKeysPk = new HashMap<String, String>(getXKeyPairs().size());
		this.xKeysEx = new HashMap<String, String>(getXKeyPairs().size());
		mapXPairsKeys(this.xKeysPk, this.xKeysEx);

		//~: find multiple properties
		this.multiple = findMultipleProps();
	}


	/* public: access bean info interface */

	public Class                getType()
	{
		return type;
	}

	/**
	 * Returns the properties used to compare the
	 * entities. Some properties of the API interfaces
	 * are excluded.
	 */
	public PropertyDescriptor[] getProps()
	{
		return properties;
	}

	/**
	 * The mapping of the properties by the name.
	 */
	public Map<String, PropertyDescriptor>
	                            getPMap()
	{
		return pmap;
	}

	/**
	 * Returns the properties with get-methods having
	 * annotation {@link XKeyPair}.
	 *
	 * The mapping contains {@link XKeyPair#type()}
	 * attribute of the annotation.
	 */
	public Map<String, Class>   getXKeyPairs()
	{
		return xKeyPairs;
	}

	/**
	 * Maps the ReTrade primary key property
	 * (that links an entity) to the external
	 * system property (x-key) that refers the
	 * same entity in the external system.
	 *
	 * Note that the key set of the mapping
	 * is the same as {@link #getXKeyPairs()}.
	 */
	public Map<String, String>  getXKeysPk()
	{
		return xKeysPk;
	}

	/**
	 * Reverse mapping for {@link #getXKeysPk()}.
	 */
	public Map<String, String>  getXKeysEx()
	{
		return xKeysEx;
	}

	/**
	 * Returns the properties being collections or arrays.
	 */
	public Set<String>          getMultipleProps()
	{
		return multiple;
	}


	/* protected: information creation */

	@SuppressWarnings("unchecked")
	protected PropertyDescriptor[]
	               findProperties()
	  throws Exception
	{
		//~: introspect the bean
		List<PropertyDescriptor> pds = new ArrayList<PropertyDescriptor>(
		  Arrays.asList(Introspector.getBeanInfo(getType()).
		    getPropertyDescriptors()));

		//~: support for Boolean is-getters
		for(PropertyDescriptor pd : pds) if(pd.getReadMethod() == null)
			if(Boolean.class.equals(pd.getPropertyType()))
			{
				StringBuilder s = new StringBuilder(16);

				s.append(pd.getName());
				s.setCharAt(0, Character.toUpperCase(s.charAt(0)));
				s.insert(0, "is");

				pd.setReadMethod(getType().getMethod(s.toString()));
			}

		//~: exclude properties from the comparison
		excludeProperties(pds);

		return pds.toArray(new PropertyDescriptor[pds.size()]);
	}

	protected void excludeProperties(List<PropertyDescriptor> pds)
	{
		excludeElseProperties(pds);

		if(TxObject.class.isAssignableFrom(getType()))
			removeProperty(pds, "tx", true);

		if(TwoKeysObject.class.isAssignableFrom(getType()))
		{
			removeProperty(pds, "pkey", true);
			removeProperty(pds, "xkey", true);
		}
	}

	protected void excludeElseProperties(List<PropertyDescriptor> pds)
	{
		removeProperty(pds, "class", false);

		for(Iterator<PropertyDescriptor> i = pds.iterator();(i.hasNext());)
			if(i.next().getWriteMethod() == null)
				i.remove();
	}

	protected void removeProperty(List<PropertyDescriptor> pds, String p, boolean strict)
	{
		for(int i = 0;(i < pds.size());i++)
			if(pds.get(i).getName().equals(p))
			{
				pds.remove(i);
				return;
			}

		if(strict) throw new IllegalStateException();
	}

	protected Map<String, PropertyDescriptor>
	               mapProperties()
	{
		Map<String, PropertyDescriptor> pmap =
		  new HashMap<String, PropertyDescriptor>(getProps().length);

		for(PropertyDescriptor pd : getProps())
			pmap.put(pd.getName(), pd);

		return pmap;
	}

	protected Map<String, Class>
	               findXKeyPairs()
	{
		Map<String, Class> xpairs = new HashMap<String, Class>(1);

		for(PropertyDescriptor pd : getProps())
		{
			XKeyPair xkp = pd.getReadMethod().
			  getAnnotation(XKeyPair.class);

			if(xkp == null) continue;

			if(xkp.type() == null)
				throw new IllegalStateException();
			xpairs.put(pd.getName(), xkp.type());
		}

		return xpairs;
	}

	protected void mapXPairsKeys
	  (Map<String, String> rtPx, Map<String, String> xPrt)
	{
		for(String p : getXKeyPairs().keySet())
		{
			StringBuilder s = new StringBuilder(p.length() + 1);

			s.append("X").append(p);
			s.setCharAt(1, Character.toUpperCase(p.charAt(0)));

			String xp = s.toString();

			if(!getPMap().containsKey(xp))
				throw new IllegalStateException(String.format(
				  "Entity [%s] has no x-key paired property for property [%s]!",
				  getType().getName(), p
				));

			rtPx.put(p, xp);
			xPrt.put(xp, p);
		}
	}

	protected Set<String>
	               findMultipleProps()
	{
		Set<String> res = new HashSet<String>(1);

		for(PropertyDescriptor pd : getProps())
		{
			Class cls = pd.getPropertyType();

			if(cls.isArray())
				res.add(pd.getName());

			if(Collection.class.isAssignableFrom(cls))
				res.add(pd.getName());

			if(Map.class.isAssignableFrom(cls))
				res.add(pd.getName());
		}

		return res;
	}


	/* information */

	private Class                           type;
	private PropertyDescriptor[]            properties;
	private Map<String, PropertyDescriptor> pmap;

	private Map<String, Class>              xKeyPairs;
	private Map<String, String>             xKeysPk;
	private Map<String, String>             xKeysEx;

	private Set<String>                     multiple;
}