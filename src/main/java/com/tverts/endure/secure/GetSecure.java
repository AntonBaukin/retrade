package com.tverts.endure.secure;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: support */

import static com.tverts.support.SU.sXe;


/**
 * Collection of functions to load
 * security related instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getSecure")
public class GetSecure extends GetObjectBase
{
	/* Security Keys */

	public SecKey getSecKey(Long pk)
	{
		return (SecKey) session().get(SecKey.class, pk);
	}

	public SecKey getSecKey(String name)
	{
		if(sXe(name)) throw new IllegalArgumentException();

		// from SecKey where (name = :name)

		return (SecKey) Q(
		  "from SecKey where (name = :name)"
		).
		  setString("name", name).
		  uniqueResult();
	}
}