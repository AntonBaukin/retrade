package com.tverts.endure.core;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

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
	public Property get(String name, String area)
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
	public Property get(String name, Long domain)
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
	public Property get(String name, Domain domain)
	{
		if(domain == null) return this.get(name);
		return this.get(name, domain.getPrimaryKey());
	}

	/**
	 * Returns the domain-wide property.
	 */
	public Property get(String name, String area, Long domain)
	{
		if(sXe(area))      return this.get(name, domain);
		if(domain == null) return this.get(name, area);
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
	public Property get(String name, String area, Domain domain)
	{
		if(domain == null) return this.get(name, area);
		return this.get(name, area, domain.getPrimaryKey());
	}

	/**
	 * Searches for the property instance with the same
	 * alternative key (name + area + domain) as of the
	 * property instance given.
	 */
	public Property get(Property prop)
	{
		return (prop == null)?(null):this.get(
		  prop.getName(), prop.getArea(), prop.getDomain()
		);
	}

	public GetProps save(Property prop)
	{
		if(prop == null) throw new IllegalArgumentException();
		session().save(prop);
		return this;
	}

	public Property merge(Property prop)
	{
		if(prop == null) throw new IllegalArgumentException();
		Property got = this.get(prop);

		//?: {a new instance} save it
		if(got == null)
			session().save(prop);
		//e: do merge
		else
		{
			got.setType(prop.getType());
			got.setValue(prop.getValue());
			got.setObject(prop.getObject());
		}

		return got;
	}


	/* Values Access */

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

		if(p.getType() == null)
			p.setType(bean.getClass());
		p.setValue(obj2xml(bean));

		return this;
	}
}