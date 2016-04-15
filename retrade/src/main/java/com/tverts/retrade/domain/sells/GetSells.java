package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;


/**
 * Loads sells related instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getSells")
public class GetSells extends GetObjectBase
{
	/* Payments Desks & Sells Desks (Terminals) */

	public List<PayDesk> getPayDesks(Long domain)
	{

// from PayDesk where (domain.id = :domain)

		return list(PayDesk.class,

"from PayDesk where (domain.id = :domain)",

		  "domain", domain
		);
	}

	public SellsDesk getSellsDesk(Long pk)
	{
		return get(SellsDesk.class, pk);
	}

	public List<SellsDesk> getSellsDesks(Long domain)
	{

/*

 from SellsDesk where (domain.id = :domain)
 order by lower(code)

 */

		return list(SellsDesk.class,

" from SellsDesk where (domain.id = :domain)\n" +
" order by lower(code)",

		  "domain", domain
		);
	}


	/* Sells Sessions & Receipts */

	public SellsSession getSession(Long pk)
	{
		return load(SellsSession.class, pk);
	}

	public SellReceipt getReceipt(Long pk)
	{
		return load(SellReceipt.class, pk);
	}

	public int countSellReceipts(SellsSessionModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Receipt", SellReceipt.class);
		qb.setClauseFrom("Receipt r join r.session s");

		//~: select clause
		qb.setClauseSelect("count(r.id)");


		//~: restrict by session
		qb.getClauseWhere().addPart(
		  "s.id = :session"
		).
		  param("session", mb.getPrimaryKey());

		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List selectSellReceipts(SellsSessionModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Receipt", SellReceipt.class);
		qb.setClauseFrom("Receipt r join r.session s");

		//~: select clause
		qb.setClauseSelect("r");

		//~: order by
		qb.setClauseOrderBy("r.time");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: restrict by session
		qb.getClauseWhere().addPart(
		  "s.id = :session"
		).
		  param("session", mb.getPrimaryKey());

		return QB(qb).list();
	}


	/* Sells Invoices */

	public List<Invoice> getSellsInvoices(SellsSession session)
	{

// select i from SellsData d join d.invoice i where (d.session = :session)

		return list(Invoice.class,

"  select i from SellsData d join d.invoice i where (d.session = :session)",

		  "session", session
		);
	}


	/* Sells Payments */

	public List<SellsPay> getSellsPayments(SellsSession session)
	{

// from SellsPay where payOrder = :session

		return list(SellsPay.class,

"  from SellsPay where payOrder = :session",

		  "session", session
		);
	}

}