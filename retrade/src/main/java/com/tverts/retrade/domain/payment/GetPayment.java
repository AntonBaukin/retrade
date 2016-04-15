package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import com.tverts.retrade.domain.account.PayBank;
import org.springframework.stereotype.Component;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Loads Payments in various projections.
 *
 * @author anton.baukin@gmail.com
 */
@Component
public class GetPayment extends GetObjectBase
{
	/* Get Payments Dispatcher */

	public int            countPaymentsDisp(PaymentsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		if("gen".equals(mb.getProjection()))
			queryPaymentsGeneral(qb, mb);
		else if("con".equals(mb.getProjection()))
			queryPaymentsContractors(qb, mb);
		else if("conBank".equals(mb.getProjection()))
			queryPaymentsContractorsBanks(qb, mb);
		else
			throw EX.state();

		//~: select clause
		qb.setClauseSelect("count(p.id)");


		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	public List<Object[]> selectPaymentsDisp(PaymentsModelBean mb)
	{
		if("gen".equals(mb.getProjection()))
			return selectPaymentsGeneral(mb);

		if("con".equals(mb.getProjection()))
			return selectPaymentsContractors(mb);

		if("conBank".equals(mb.getProjection()))
			return selectPaymentsContractorsBanks(mb);

		throw EX.state();
	}


	/* Get Payments (general) */

	@SuppressWarnings("unchecked")
	public List<Object[]> selectPaymentsGeneral(PaymentsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: build the query
		queryPaymentsGeneral(qb, mb);

		//~: select clause
		qb.setClauseSelect("p, pu, put, po, ps, sa, sw");
		mb.setModelFlags("self-order-account-way");

		//~: order by clause
		qb.setClauseOrderBy("p.orderIndex");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		return (List<Object[]>) QB(qb).list();
	}

	protected void        queryPaymentsGeneral(QueryBuilder qb, PaymentsModelBean mb)
	{
		//~: from clause
		qb.nameEntity("Payment", Payment.class);
		qb.setClauseFrom(

"Payment p join p.unity pu join pu.unityType put " +
"  join p.payOrder po join p.paySelf ps " +
"  join ps.account sa join ps.payWay sw"

		);

		//~: domain restriction
		qb.getClauseWhere().addPart(
		  "p.domain.id = :domain"
		).
		  param("domain", mb.getDomain());

		//~: restrict the dates
		restrictDates(qb, mb);

		//~: select the payment direction
		restrictPays(qb, mb);

		//~: search the names
		searchNamesGeneral(qb, mb);

		//~: filter by the selection set
		filterSelSetGeneral(qb, mb);
	}


	/* Get Payments (contractors) */

	@SuppressWarnings("unchecked")
	public List<Object[]> selectPaymentsContractors(PaymentsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: build the query
		queryPaymentsContractors(qb, mb);

		//~: select clause
		qb.setClauseSelect("p, pu, put, po, ps, sa, sw, pf, fa, c");
		mb.setModelFlags("self-order-firm-account-way");

		//~: order by clause
		qb.setClauseOrderBy("p.orderIndex");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		return (List<Object[]>) QB(qb).list();
	}

	protected void        queryPaymentsContractors(QueryBuilder qb, PaymentsModelBean mb)
	{
		//~: from clause
		qb.nameEntity("Payment", Settling.class);
		qb.setClauseFrom(

"Payment p join p.unity pu join pu.unityType put " +
"  join p.payOrder po join p.paySelf ps " +
"  join ps.account sa join ps.payWay sw " +
"  join p.payFirm pf join pf.account fa join fa.contractor c"

		);

		//~: domain restriction
		qb.getClauseWhere().addPart(
		  "p.domain.id = :domain"
		).
		  param("domain", mb.getDomain());

		//~: restrict the dates
		restrictDates(qb, mb);

		//~: select the payment direction
		restrictPays(qb, mb);

		//~: search the names
		searchNamesContractor(qb, mb);

		//~: filter by the selection set
		filterSelSetContractor(qb, mb);
	}


	/* Get Payments (contractors in banks) */

	@SuppressWarnings("unchecked")
	public List<Object[]> selectPaymentsContractorsBanks(PaymentsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: build the query
		queryPaymentsContractorsBanks(qb, mb);

		//~: select clause
		qb.setClauseSelect("p, pu, put, po, ps, sa, sw, pf, fa, c, fw");
		mb.setModelFlags("self-order-firm-account-way-bank");

		//~: order by clause
		qb.setClauseOrderBy("p.orderIndex");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		return (List<Object[]>) QB(qb).list();
	}

	protected void        queryPaymentsContractorsBanks
	  (QueryBuilder qb, PaymentsModelBean mb)
	{
		//~: from clause
		qb.nameEntity("Payment", Settling.class);
		qb.nameEntity("PayBank", PayBank.class);
		qb.setClauseFrom(

"Payment p join p.unity pu join pu.unityType put " +
"  join p.payOrder po join p.paySelf ps " +
"  join ps.account sa join ps.payWay sw " +
"  join p.payFirm pf join pf.account fa join fa.contractor c, " +
"  PayBank fw"

		);

		//~: domain restriction
		qb.getClauseWhere().addPart(
		  "p.domain.id = :domain"
		).
		  param("domain", mb.getDomain());

		//~: firm payment way >< bank payment way [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "pf.payWay.id = fw.id"
		);

		//~: restrict the dates
		restrictDates(qb, mb);

		//~: select the payment direction
		restrictPays(qb, mb);

		//~: search the names
		searchNamesContractorBank(qb, mb);

		//~: filter by the selection set
		filterSelSetContractor(qb, mb);
	}


	/* private: shared restrictions */

	private void restrictDates(QueryBuilder qb, PaymentsModelBean mb)
	{
		if((mb.getMinDate() == null) && (mb.getMaxDate() == null))
			return;

		//~: <= max date
		if(mb.getMinDate() == null) qb.getClauseWhere().addPart(
		  "p.time  <= :maxDate"
		).
		  param("maxDate", mb.getMaxDate());

		//~: >= min date
		else if(mb.getMaxDate() == null) qb.getClauseWhere().addPart(
		  "p.time  >= :minDate"
		).
		  param("minDate", mb.getMinDate());

		//~: between
		else qb.getClauseWhere().addPart(
		  "p.time between :minDate and :maxDate"
		).
		  param("minDate", mb.getMinDate()).
		  param("maxDate", mb.getMaxDate());
	}

	private void restrictPays(QueryBuilder qb, PaymentsModelBean mb)
	{
		if(!mb.isWithExpense() && !mb.isWithIncome())
			throw EX.state("Both income and expense fields are hidden!");

		if(!mb.isWithExpense())
			qb.getClauseWhere().addPart(
			  "p.expense is null"
			);

		if(!mb.isWithIncome())
			qb.getClauseWhere().addPart(
			  "p.income is null"
			);
	}


	/* private: names search */

	private void searchNamesGeneral(QueryBuilder qb, PaymentsModelBean mb)
	{
		String[] words = mb.searchNames();

		if(words != null) for(String w : words) if((w = SU.s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("lower(p.code)  like :w").param("w", w);
			p.addPart("lower(po.code) like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}

	private void searchNamesContractor(QueryBuilder qb, PaymentsModelBean mb)
	{
		String[] words = mb.searchNames();

		if(words != null) for(String w : words) if((w = SU.s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("lower(p.code)     like :w").param("w", w);
			p.addPart("lower(po.code)    like :w").param("w", w);
			p.addPart("lower(c.nameProc) like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}

	private void searchNamesContractorBank(QueryBuilder qb, PaymentsModelBean mb)
	{
		String[] words = mb.searchNames();

		if(words != null) for(String w : words) if((w = SU.s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("lower(p.code)     like :w").param("w", w);
			p.addPart("lower(po.code)    like :w").param("w", w);
			p.addPart("lower(c.nameProc) like :w").param("w", w);

			p.addPart(

"lower(fw.bankId || fw.bankName || fw.bankAccount || " +
" fw.remitteeAccount || fw.remitteeName) like :w"

			).param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}


	/* private: selection set filtering */

	private WherePartLogic filterSelSetGeneral(QueryBuilder qb, PaymentsModelBean mb)
	{
		if(!mb.isWithSelSet()) return null;

		WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(p);

/*

 p.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 po.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 ps.account.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 ps.payWay.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 */
		//~: directly selected payments
		p.addPart(

"p.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   SU.sXs(mb.getSelSet())).
		  param("login", SecPoint.login());

		//~: payment orders
		p.addPart(

"po.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   SU.sXs(mb.getSelSet())).
		  param("login", SecPoint.login());

		//~: own accounts
		p.addPart(

"ps.account.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   SU.sXs(mb.getSelSet())).
		  param("login", SecPoint.login());

		//~: own payment ways
		p.addPart(

"ps.payWay.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   SU.sXs(mb.getSelSet())).
		  param("login", SecPoint.login());

		return p;
	}

	private WherePartLogic filterSelSetContractor(QueryBuilder qb, PaymentsModelBean mb)
	{
		WherePartLogic p = filterSelSetGeneral(qb, mb);
		if(p == null) return null;

/*

 c.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 fa.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 pf.payWay.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 */
		//~: contractors
		p.addPart(

"c.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   SU.sXs(mb.getSelSet())).
		  param("login", SecPoint.login());

		//~: firm accounts
		p.addPart(

"fa.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   SU.sXs(mb.getSelSet())).
		  param("login", SecPoint.login());

		//~: firm payment ways
		p.addPart(

"pf.payWay.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   SU.sXs(mb.getSelSet())).
		  param("login", SecPoint.login());

		return p;
	}
}