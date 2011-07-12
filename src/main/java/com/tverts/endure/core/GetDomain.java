package com.tverts.endure.core;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public Domain getTestDomain()
	{

/*

from Domain d where (d.primaryKey < 0)

*/

		List domains = Q(

"from Domain d where (d.primaryKey < 0)"

		).
		  list();

		return (Domain)(domains.isEmpty()?(null):(domains.get(0)));
	}
}