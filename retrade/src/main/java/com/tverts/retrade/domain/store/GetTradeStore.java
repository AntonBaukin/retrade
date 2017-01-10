package com.tverts.retrade.domain.store;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Loads {@link TradeStore} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getTradeStore")
public class GetTradeStore extends GetObjectBase
{
	/* Get TradeStore */

	public TradeStore getTradeStore(Long domain, String code)
	{
		if(domain == null)
			throw new IllegalArgumentException();

		if((code = s2s(code)) == null)
			throw new IllegalArgumentException();

/*

 from TradeStore ts where
   (ts.domain.id = :domain) and (ts.code = :code)

*/

		return (TradeStore) Q(

"from TradeStore ts where\n" +
"  (ts.domain.id = :domain) and (ts.code = :code)"

		).
		  setParameter("domain", domain).
		  setParameter("code", code).
		  uniqueResult();
	}

	public TradeStore getTradeStore(Long pk)
	{
		if(pk == null) return null;
		return (TradeStore) session().get(TradeStore.class, pk);
	}

	@SuppressWarnings("unchecked")
	public List<TradeStore> getTradeStores(Long domain)
	{
		if(domain == null)
			throw new IllegalArgumentException();

/*

from TradeStore ts where (ts.domain.id = :domain)
order by lower(ts.name)

*/
		return (List<TradeStore>) Q(

"from TradeStore ts where (ts.domain.id = :domain)\n" +
"order by lower(ts.name)"

		).
		  setParameter("domain", domain).
		  list();
	}

	public TradeStore getStoreByOffset(Long domain, int offset)
	{
		if(domain == null)
			throw new IllegalArgumentException();

/*

from TradeStore ts where (ts.domain.id = :domain)
order by lower(ts.code)

*/
		List list = Q(

"from TradeStore ts where (ts.domain.id = :domain)\n" +
"order by lower(ts.code)"

		).
		  setParameter("domain", domain).
		  setFirstResult(offset).
		  setMaxResults(1).
		  list();

		return (TradeStore)(list.isEmpty()?(null):(list.get(0)));
	}
}