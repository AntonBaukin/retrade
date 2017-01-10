package com.tverts.endure.cats;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectPrototypeBase;


/**
 * Loads leaf instances implementing {@link CatItem}.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getCatItem") @Scope("prototype")
public class GetCatItem extends GetObjectPrototypeBase
{
	/* public: GetCatItem (bean) interface */

	public GetCatItem setCatItemClass(Class<? extends CatItem> catItemClass)
	{
		this.catItemClass = catItemClass;
		return this;
	}


	/* Get Catalogue Item */

	public int     countItems(Long domain)
	{
		if(catItemClass == null)
			throw new IllegalStateException();

/*

select count(ci.id) from CatItem ci where
  (ci.domain = :domain)

*/
		return ((Number) QR(

"select count(ci.id) from CatItem ci where\n" +
"  (ci.domain.id = :domain)",

		  "CatItem", catItemClass
		).
		  setParameter("domain", domain).
		  uniqueResult()).
		  intValue();
	}

	public CatItem getCatItem(Long domain, String code)
	{
		if(catItemClass == null)
			throw new IllegalStateException();

/*

from CatItem ci where (ci.domain = :domain)
  and (ci.code = :code)

*/
		return (CatItem) QR(

"from CatItem ci where (ci.domain = :domain)\n" +
"  and (ci.code = :code)",

		  "CatItem", catItemClass
		).
		  setParameter("domain", domain).
		  setParameter("code", code).
		  uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public CatItem getNthCatItem(Long domain, int offset)
	{

// from CatItem ci where (ci.domain = :domain)

		return (CatItem) QR(

"from CatItem ci where (ci.domain = :domain)",

		  "CatItem", catItemClass
		).
		  setParameter("domain", domain).
		  setFirstResult(offset).
		  setMaxResults(1).
		  uniqueResult();
	}


	/* cat item class */

	protected Class<? extends CatItem> catItemClass;
}