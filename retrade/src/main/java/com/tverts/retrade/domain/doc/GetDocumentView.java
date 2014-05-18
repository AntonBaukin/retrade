package com.tverts.retrade.domain.doc;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import com.tverts.retrade.domain.sells.SellsData;
import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: secure */

import static com.tverts.secure.SecPoint.login;

/* com.tverts: retrade domain (firms + payments + sells + selection sets) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.payment.InvoiceBill;
import com.tverts.retrade.domain.sells.SellsSession;
import com.tverts.retrade.domain.selset.SelItem;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Loads {@link DocumentView} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getDocumentView")
public class GetDocumentView extends GetObjectBase
{
	/* Get DocumentView */

	public DocumentView findDocumentView(Long owner)
	{
		if(owner == null) return null;

// from DocumentView dv where dv.viewOwner.id = :owner

		return (DocumentView) Q(
		  "from DocumentView dv where dv.viewOwner.id = :owner"
		).
		  setLong("owner", owner).
		  uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<DocumentView> selectDocuments(DocsSearchModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("View", DocumentView.class);
		qb.setClauseFrom("View v");

		//~: order by
		qb.setClauseOrderBy("v.docDate");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());

		//~: domain
		qb.getClauseWhere().addPart(
		  "v.domain.id = :domain"
		).
		  param("domain", mb.getDomain());

		//~: restrict by the dates
		restrictDates(qb, mb);

		//~: restrict by the document types and states
		restrictTypes(qb, mb);
		restrictStates(qb, mb);

		//~: restrict by the document owner
		restrictDocViewOwner(qb, mb);

		//~: restrict by selection set
		restrictSelSet(qb, mb);

		//sec: restrict to "view"
		secureByKey(qb, "v.viewOwner.id", "view");

		return (List<DocumentView>) QB(qb).list();
	}

	public int countDocuments(DocsSearchModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: select clause
		qb.setClauseSelect("count(v.id)");

		//~: from clause
		qb.nameEntity("View", DocumentView.class);
		qb.setClauseFrom("View v");

		//~: domain
		qb.getClauseWhere().addPart(
		  "v.domain.id = :domain"
		).
		  param("domain", mb.getDomain());

		//~: restrict by the dates
		restrictDates(qb, mb);

		//~: restrict by the document types and states
		restrictTypes(qb, mb);
		restrictStates(qb, mb);

		//~: restrict by the document owner
		restrictDocViewOwner(qb, mb);

		//~: restrict by selection set
		restrictSelSet(qb, mb);

		//sec: restrict to "view"
		secureByKey(qb, "v.viewOwner.id", "view");

		return ((Number) QB(qb).uniqueResult()).intValue();
	}


	/* private: local restrictions */

	private void restrictDates(QueryBuilder qb, DocsSearchModelBean mb)
	{
		if((mb.getMinDate() == null) && (mb.getMaxDate() == null))
			return;

		//~: <= max date
		if(mb.getMinDate() == null) qb.getClauseWhere().addPart(
		  "v.docDate  <= :maxDate"
		).
		  param("maxDate", mb.getMaxDate());

		//~: >= min date
		else if(mb.getMaxDate() == null) qb.getClauseWhere().addPart(
		  "v.docDate  >= :minDate"
		).
		  param("minDate", mb.getMinDate());

		//~: between
		else qb.getClauseWhere().addPart(
		  "v.docDate between :minDate and :maxDate"
		).
		  param("minDate", mb.getMinDate()).
		  param("maxDate", mb.getMaxDate());
	}

	private void restrictTypes(QueryBuilder qb, DocsSearchModelBean mb)
	{
		Long[] types = mb.selectDocTypes(); if(types == null) return;

		qb.getClauseWhere().addPart(
		  "v.docType.id in (:docTypes)"
		).
		  param("docTypes", types);
	}

	private void restrictStates(QueryBuilder qb, DocsSearchModelBean mb)
	{
		Long[] states = mb.selectDocStates(); if(states == null) return;

		qb.getClauseWhere().addPart(
		  "v.ownerState.id in (:docStates)"
		).
		  param("docStates", states);
	}

	private void restrictDocViewOwner(QueryBuilder qb, DocsSearchModelBean mb)
	{
		if(mb.getDocOwnerKey() == null) return;

		if(Contractor.class.equals(mb.getDocOwnerClass()))
			restrictContractor(qb, mb);

		else if(SellsSession.class.equals(mb.getDocOwnerClass()))
			restrictSellsSession(qb, mb);

		else throw EX.state(
		  "Do not know hos to restrict documents query for class [",
		  mb.getDocOwnerClass(), "] and type [", mb.getDocOwnerType(), "]!"
		);
	}

	private void restrictContractor(QueryBuilder qb, DocsSearchModelBean mb)
	{

/*

 v.viewOwner.id in (select i.id from
   Bill ib join ib.invoice i
   where (ib.contractor.id = :contractor)
 )

 */

		qb.nameEntity("Bill", InvoiceBill.class);
		qb.getClauseWhere().addPart(

"v.viewOwner.id in (select i.id from\n" +
"  Bill ib join ib.invoice i\n" +
"  where (ib.contractor.id = :contractor)\n" +
")"

		).
		  param("contractor", mb.getDocOwnerKey());
	}

	private void restrictSellsSession(QueryBuilder qb, DocsSearchModelBean mb)
	{

/*

 v.viewOwner.id in (select sd.invoice.id
   from SellsData sd where (sd.session.id = :session)
 )

 */

		qb.nameEntity("SellsData", SellsData.class);
		qb.getClauseWhere().addPart(

"v.viewOwner.id in (select sd.invoice.id\n" +
"  from SellsData sd where (sd.session.id = :session)\n" +
")"

		).
		  param("session", mb.getDocOwnerKey());
	}

	private void restrictSelSet(QueryBuilder qb, DocsSearchModelBean mb)
	{
		if(!mb.isWithSelSet() || (mb.getSelSet() == null))
			return;

/*

 v.viewOwner.id in (select si.object from SelItem si join si.selSet ss
   where (ss.login.id = :login) and (ss.name = :name))

*/
		qb.nameEntity("SelItem", SelItem.class);

		qb.getClauseWhere().addPart(
		  "v.viewOwner.id in (select si.object from SelItem si join si.selSet ss\n" +
		  "   where (ss.login.id = :login) and (ss.name = :name))"
		).
		  param("login", login()).
		  param("name",  mb.getSelSet());
	}
}