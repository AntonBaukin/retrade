package com.tverts.endure.person;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Loads {@link FirmEntity}.
 *
 * @author anton.baukin@gmail.com.
 */
@Component
public class GetFirm extends GetObjectBase
{
	/* Get Firm */

	public FirmEntity getFirm(Domain domain, String code)
	{
		EX.assertn(domain);
		EX.asserts(code);

/*

 from FirmEntity f where
   (f.domain = :domain) and (f.code = :code)

*/
		final String Q =

"from FirmEntity f where\n" +
"  (f.domain = :domain) and (f.code = :code)";

		return object(FirmEntity.class, Q,
		  "domain", domain, "code", code
		);
	}
}