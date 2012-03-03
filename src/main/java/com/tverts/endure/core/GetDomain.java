package com.tverts.endure.core;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;


/**
 * Loads {@link Domain} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getDomain")
public class GetDomain extends GetObjectBase
{
	/* Get Domain */

	public Domain getTestDomain()
	{

// from Domain d where (d.primaryKey < 0)

		List domains = Q(

"from Domain d where (d.primaryKey < 0)"

		).
		  list();

		return (Domain)(domains.isEmpty()?(null):(domains.get(0)));
	}

	public Domain getDomain(Long id)
	{
		return (Domain) session().get(Domain.class, id);
	}
}