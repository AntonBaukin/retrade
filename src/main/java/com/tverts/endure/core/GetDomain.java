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

	@SuppressWarnings("unchecked")
	public List<Domain> getTestDomains()
	{

// from Domain d where (d.primaryKey < 0)

		return Q(

"from Domain d where (d.primaryKey < 0)"

		).
		  list();
	}

	public Domain getDomain(Long id)
	{
		return (Domain) session().get(Domain.class, id);
	}
}