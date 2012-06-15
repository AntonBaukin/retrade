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
	 * Returns the system-wide property.
	 */
	public Property get(String name)
	{
		if(sXe(name)) throw new IllegalArgumentException();

/*

from Property where (name = :name) and
  (area is null) and (domain is null)

*/


		return (Property) session().createQuery(

"from Property where (name = :name) and\n" +
"  (area is null) and (domain is null)"

		).
		  setString("name", name).
		  uniqueResult();
	}

	/**
	 * Returns the system-wide property within the area.
	 */
	public Property get(String area, String name)
	{
		if(sXe(area)) return this.get(name);
		if(sXe(name)) throw new IllegalArgumentException();

/*

from Property where (name = :name) and
  (area = :area) and (domain is null)

*/


		return (Property) session().createQuery(

		  "from Property where (name = :name) and\n" +
		  "  (area = :area) and (domain is null)"

		).
		  setString("name", name).
		  setString("area", area).
		  uniqueResult();
	}

	/**
	 * Returns the domain-wide property.
	 */
	public Property get(Long domain, String name)
	{
		if(domain == null) return this.get(name);
		if(sXe(name))      throw new IllegalArgumentException();

/*

from Property where (name = :name) and
  (area is null) and (domain.id = :domain)

*/


		return (Property) session().createQuery(

"from Property where (name = :name) and\n" +
"  (area is null) and (domain.id = :domain)"

		).
		  setString("name", name).
		  setLong("domain", domain).
		  uniqueResult();
	}

	/**
	 * Returns the domain-wide property within the area.
	 */
	public Property get(Domain domain, String name)
	{
		if(domain == null) return this.get(name);
		return this.get(domain.getPrimaryKey(), name);
	}

	/**
	 * Returns the domain-wide property.
	 */
	public Property get(Long domain, String area, String name )
	{
		if(sXe(area))      return this.get(domain, name);
		if(domain == null) return this.get(area, name);
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
	 * Returns the domain-wide property.
	 */
	public Property get(Domain domain, String area, String name)
	{
		if(domain == null) return this.get(area, name);
		return this.get(domain.getPrimaryKey(), area, name);
	}

	/**
	 * Get or Create. Returns the domain-wide property.
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
		if(prop == null) throw new IllegalArgumentException();

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
			got.setType(prop.getType());
			got.setValue(prop.getValue());
			got.setObject(prop.getObject());
		}

		return got;
	}


	/* Values Read */

	public String  string(Property p)
	{
		return p.getValue();
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
		return xml2obj(p.getObject());
	}

	public <O> O   bean(Property p, Class<O> c1ass)
	{
		return xml2obj(p.getObject(), c1ass);
	}


	/* Values Write */

	public GetProps set(Property p, String v)
	{
		p.setType(String.class);
		p.setValue(v);
		return this;
	}

	public GetProps set(Property p, Integer v)
	{
		p.setType(Integer.class);
		p.setValue((v == null)?(null):(v.toString()));
		return this;
	}

	public GetProps set(Property p, Long v)
	{
		p.setType(Long.class);
		p.setValue((v == null)?(null):(v.toString()));
		return this;
	}

	public GetProps set(Property p, Boolean v)
	{
		p.setType(Boolean.class);
		p.setValue((v == null)?(null):(v.toString()));
		return this;
	}

	public GetProps bean(Property p, Object bean)
	{
		if(bean == null)
		{
			p.setObject(null);
			return this;
		}

		if((p.getType() == null) || sXe(p.getValue()))
			p.setType(bean.getClass());
		p.setValue(obj2xml(bean));

		return this;
	}
}