package com.tverts.endure.core;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.HiberPoint;

/* com.tverts: support */

import static com.tverts.support.OU.obj2xml;
import static com.tverts.support.OU.xml2obj;
import static com.tverts.support.SU.sXe;


/**
 * Handler for the Properties.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getProps")
public class GetProps extends GetObjectBase
{
	/* Properties Loader */

	/**
	 * Returns the domain-wide property.
	 */
	public Property get(Long domain, String name)
	{
		return this.get(domain, "", name);
	}

	/**
	 * Returns the domain-wide property.
	 */
	public Property get(Domain domain, String name)
	{
		return this.get(domain, "", name);
	}

	/**
	 * Returns the property of the domain area.
	 */
	public Property get(Long domain, String area, String name)
	{
		if(sXe(area)) area = "";
		if(domain == null) throw new IllegalArgumentException();
		if(sXe(name))      throw new IllegalArgumentException();

/*

from Property where (name = :name) and
  (area = :area) and (domain.id = :domain)

*/


		return (Property) session().createQuery(

"from Property where (name = :name) and\n" +
"  (area = :area) and (domain.id = :domain)"

		).
		  setString("name", name).
		  setString("area", area).
		  setLong("domain", domain).
		  uniqueResult();
	}

	/**
	 * Returns the property of the domain area.
	 */
	public Property get(Domain domain, String area, String name)
	{
		return this.get(domain.getPrimaryKey(), area, name);
	}

	/**
	 * Get or Create: returns the property of the domain area.
	 */
	public Property goc(Domain domain, String area, String name)
	{
		Property res = this.get(domain, area, name);
		if(res != null) return res;

		//~: assign the alternative key
		res = new Property();
		res.setName(name);
		res.setArea(area);
		res.setDomain(domain);

		//!: save it
		this.save(res);

		return res;
	}

	/**
	 * Get or Create: returns the property of the domain area.
	 * If the property exists in the database, new instance.
	 */
	public Property goc(Property p)
	{
		Property res = this.get(p);
		if(res != null) return res;

		//!: save it
		this.save(p);
		return p;
	}

	/**
	 * Searches for the property instance with the same
	 * alternative key (name + area + domain) as of the
	 * property instance given.
	 */
	public Property get(Property prop)
	{
		return (prop == null)?(null):this.get(
		  prop.getDomain(), prop.getArea(), prop.getName()
		);
	}

	public GetProps save(Property prop)
	{
		if(prop == null)
			throw new IllegalArgumentException();
		if(prop.getDomain() == null)
			throw new IllegalArgumentException();
		if(sXe(prop.getName()))
			throw new IllegalArgumentException();


		//?: {has no primary key}
		if(prop.getPrimaryKey() == null)
			HiberPoint.setPrimaryKey(session(), prop,
			  (prop.getDomain() != null) && HiberPoint.
			    isTestInstance(prop.getDomain()));

		//!: do save
		session().save(prop);

		return this;
	}

	public Property merge(Property prop)
	{
		if(prop == null) throw new IllegalArgumentException();
		Property got = this.get(prop);

		//?: {a new instance} save it
		if(got == null)
			this.save(prop);
		//:: do merge
		else
		{
			got.setValue(prop.getValue());
			got.setObject(prop.getObject());
		}

		return got;
	}


	/* Values Read */

	public String  string(Property p)
	{
		return (p.getValue() != null)?(p.getValue()):(p.getObject());
	}

	public Integer integer(Property p)
	{
		return (p.getValue() == null)?(null):
		  Integer.valueOf(p.getValue());
	}

	public Long    longer(Property p)
	{
		return (p.getValue() == null)?(null):
		  Long.valueOf(p.getValue());
	}

	public Boolean bool(Property p)
	{
		String v = p.getValue();
		return (v == null)?(null):("true".equalsIgnoreCase(v))?(Boolean.TRUE):
		  ("false".equalsIgnoreCase(v))?(Boolean.FALSE):(null);
	}

	public Object  bean(Property p)
	{
		String s = this.string(p);
		return (s == null)?(null):xml2obj(s);
	}

	public <O> O   bean(Property p, Class<O> cls)
	{
		String s = this.string(p);
		return (s == null)?(null):xml2obj(s, cls);
	}


	/* Values Write */

	public GetProps set(Property p, String v)
	{
		p.setValue(null);
		p.setObject(null);

		if(v != null) if(v.length() <= 2048)
			p.setValue(v);
		else
			p.setObject(v);

		return this;
	}

	public GetProps set(Property p, Integer v)
	{
		return this.set(p,
		  (v == null)?(null):(v.toString())
		);
	}

	public GetProps set(Property p, Long v)
	{
		return this.set(p,
		  (v == null)?(null):(v.toString())
		);
	}

	public GetProps set(Property p, Boolean v)
	{
		return this.set(p,
		  (v == null)?(null):(v.toString())
		);
	}

	public GetProps bean(Property p, Object bean)
	{
		return this.set(p, obj2xml(bean));
	}
}