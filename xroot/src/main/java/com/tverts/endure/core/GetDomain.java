package com.tverts.endure.core;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Loads {@link Domain} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getDomain")
public class GetDomain extends GetObjectBase
{
	/* Get Domain */

	public Domain getDomain(Long pk)
	{
		return (pk == null)?(null):
		  (Domain) session().get(Domain.class, pk);
	}

	public String getDomainCode(Long domain)
	{

// select code from Domain where (id = :domain)

		return object(String.class,

"  select code from Domain where (id = :domain)",

		  "domain", domain
		);
	}

	public Domain getDomain(String code)
	{
		if(SU.sXe(code))
			throw new IllegalArgumentException();


// from Domain where (codeux = :code)

		return (Domain) Q(

"from Domain where (codeux = :code)"

		).
		  setParameter("code", SU.sXs(code).toLowerCase()).
		  uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Domain> getAllDomains()
	{
		return (List<Domain>) Q("from Domain").list();
	}

	@SuppressWarnings("unchecked")
	public List<Domain> getTestDomains()
	{

// from Domain d where (d.primaryKey < 0)

		return Q(

"from Domain d where (d.primaryKey < 0)"

		).
		  list();
	}
}