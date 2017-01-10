package com.tverts.retrade.domain.account;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* Hibernate Persistence Layer */

import org.hibernate.query.Query;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePart;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: retrade domain (firms + payments) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.payment.Payment;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Loads {@link Account} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getAccount")
public class GetAccount extends GetObjectBase
{
	/* Get Account */

	public Account getAccount(Long domain, Long contractor, String code)
	{

/*

 from Account where (code = :code) and
   (domain.id = :domain) and (contractor is null)

 from Account where (code = :code) and
   (contractor.id = :contractor)

*/

		final String Q0 =

"from Account where (code = :code) and \n" +
"  (domain.id = :domain) and (contractor is null)";

		final String Q1 =

"from Account where (code = :code) and\n" +
"  (contractor.id = :contractor)";

		Query q = Q((contractor == null)?(Q0):(Q1)).
		  setParameter("code", code);

		if(contractor == null)
			q.setParameter("domain", domain);
		else
			q.setParameter("contractor", contractor);

		return (Account) q.uniqueResult();
	}

	public Account getOwnAccount(Long domain, String code)
	{
		if(domain == null) throw EX.arg();
		if(SU.sXe(code))   throw EX.arg();

		return getAccount(domain, null, code);
	}

	public Account getFirmAccount(Long contractor, String code)
	{
		if(contractor == null) throw EX.arg();
		if(SU.sXe(code))       throw EX.arg();

		return getAccount(null, contractor, code);
	}

	public int     countAccounts(AccountsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Account", Account.class);
		qb.nameEntity("Pay", PayIt.class);
		qb.setClauseFrom("Account a left outer join a.contractor c");

		//~: select clause
		qb.setClauseSelect("count(a.id)");


		//~: domain
		qb.getClauseWhere().addPart(
		  "a.domain.id = :domain"
		).
		  param("domain", mb.getDomain());

		//?: {not include own accounts}
		if(mb.isOwnOnly())
			qb.getClauseWhere().addPart("c is null");

		//~: restrict by the names
		accountsSearch(qb, mb.searchNames(), mb.isOwnOnly());

		//~: restrict by the selection set
		restrictAccountsBySelSet(qb, mb.getSelSet());


		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	/**
	 * Returns the records as Object[] of:
	 *
	 *  0  Account entity;
	 *  1  Contractor entity (may be null);
	 *  2  account balance income;
	 *  3  account balance expense.
	 */
	@SuppressWarnings("unchecked")
	public List    selectAccounts(AccountsModelBean mb)
	{
		//<: select the accounts first

		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Account", Account.class);
		qb.nameEntity("Pay", PayIt.class);
		qb.setClauseFrom("Account a left outer join a.contractor c");

		//~: select clause
		qb.setClauseSelect("a, c, 0, 0");

		//~: order by
		if(mb.isOwnOnly())
			qb.setClauseOrderBy("lower(a.name)");
		else
			qb.setClauseOrderBy("c.nameProc");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: domain
		qb.getClauseWhere().addPart(
		  "a.domain.id = :domain"
		).
		  param("domain", mb.getDomain());

		//?: {not include own accounts}
		if(mb.isOwnOnly())
			qb.getClauseWhere().addPart("c is null");

		//~: restrict by the selection set
		restrictAccountsBySelSet(qb, mb.getSelSet());

		//~: restrict by the names
		accountsSearch(qb, mb.searchNames(), mb.isOwnOnly());


		List<Object[]> res = (List<Object[]>) QB(qb).list();

		//>: select the accounts first

		qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Pay", PayIt.class);
		qb.nameEntity("Value", AggrValue.class);
		qb.setClauseFrom("Value v, Pay p");

		//~: select clause
		qb.setClauseSelect("sum(v.aggrPositive), sum(v.aggrNegative)");

		//~: group by clause
		qb.setClauseGroupBy("p.account.id");


		//~: account restriction
		WherePart wa = qb.getClauseWhere().addPart(
		  "p.account.id = :account"
		);

		//~: pay it >< volume  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "v.owner.id = p.id"
		);

		//~: balance volume aggr type
		qb.getClauseWhere().addPart(
		  "v.aggrType = :aggrType"
		).
		  param("aggrType", Accounts.aggrTypePayItBalance());

		Query q = QB(qb);

		//<: aggregate the balances

		for(Object[] rec : res)
		{
			//~: restrict the account
			q.setParameter(SU.sXs(wa.getParamsPrefix()) + "account",
			  ((Account)rec[0]).getPrimaryKey()
			);

			//!: select the aggregated volumes
			Object[] v = (Object[]) q.uniqueResult();

			rec[2] = (v[0] != null)?(v[0]):(new BigDecimal(0).setScale(5));
			rec[3] = (v[1] != null)?(v[1]):(new BigDecimal(0).setScale(5));
		}

		return res;

		//>: aggregate the balances
	}

	/**
	 * Returns array with the Account balance as:
	 *  [0] income, [1] expense, [2] balance (scaled to 2)
	 */
	public BigDecimal[] getAccountBalance(Long account)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Pay", PayIt.class);
		qb.nameEntity("Value", AggrValue.class);
		qb.setClauseFrom("Value v, Pay p");

		//~: select clause
		qb.setClauseSelect("sum(v.aggrPositive), sum(v.aggrNegative), 0");

		//~: group by clause
		qb.setClauseGroupBy("p.account.id");

		//~: account restriction
		WherePart wa = qb.getClauseWhere().addPart(
		  "p.account.id = :account"
		).
		  param("account", account);

		//~: pay it >< volume  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "v.owner.id = p.id"
		);

		//~: balance volume aggr type
		qb.getClauseWhere().addPart(
		  "v.aggrType = :aggrType"
		).
		  param("aggrType", Accounts.aggrTypePayItBalance());


		Object[] res = (Object[]) QB(qb).uniqueResult();

		if(res[0] == null) res[0] = new BigDecimal(0).setScale(5);
		if(res[1] == null) res[1] = new BigDecimal(0).setScale(5);

		return new BigDecimal[] {
		  (BigDecimal) res[0], (BigDecimal) res[1],
		  ((BigDecimal)res[0]).subtract((BigDecimal)res[1]).
		    setScale(2, BigDecimal.ROUND_HALF_UP)
		};
	}

	/**
	 * Returns the records as Object[] of:
	 *
	 *  0  Account entity;
	 *  1  account balance income;
	 *  2  account balance expense.
	 */
	@SuppressWarnings("unchecked")
	public List selectPayWayAccounts(Long payWay)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Pay", PayIt.class);
		qb.nameEntity("Value", AggrValue.class);
		qb.setClauseFrom("Value v, Pay p join p.account a");

		//~: select clause
		qb.setClauseSelect("a, v.aggrPositive, v.aggrNegative");

		//~: order by clause
		qb.setClauseOrderBy("lower(a.name)");


		//~: pay it >< volume  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "v.owner.id = p.id"
		);

		//~: balance volume aggr type
		qb.getClauseWhere().addPart(
		  "v.aggrType = :aggrType"
		).
		  param("aggrType", Accounts.aggrTypePayItBalance());


		//~: payment way restriction
		qb.getClauseWhere().addPart(
		  "p.payWay.id = :payWay"
		).
		  param("payWay", payWay);


		//~: assign empty volumes
		List<Object[]> res = (List<Object[]>) QB(qb).list();

		for(Object[] rec : res)
		{
			if(rec[1] == null) rec[1] = new BigDecimal(0).setScale(5);
			if(rec[2] == null) rec[2] = new BigDecimal(0).setScale(5);
		}

		return res;
	}


	/* Get Pay It & Pay Way */

	public PayIt getPayIt(Account account, PayWay way)
	{

// from PayIt where (account = :account) and (payWay = :payWay)

		return (PayIt) Q(

"  from PayIt where (account = :account) and (payWay = :way)"

		).
		  setParameter("account", account).
		  setParameter("way",     way).
		  uniqueResult();
	}

	public List<PayIt> getPayIts(Long account)
	{

// from PayIt where (account.id = :account)

		return list(PayIt.class,

		  "from PayIt where (account.id = :account)",

		  "account", account
		);
	}

	@SuppressWarnings("unchecked")
	public List<PaySelf> getSelfPays(Long domain)
	{

// from PaySelf where (account.domain.id = :domain)

		return (List<PaySelf>) Q(

"from PaySelf where (account.domain.id = :domain)"

		).
		  setParameter("domain", domain).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<PayFirm> getFirmPays(Contractor contractor)
	{

// from PayFirm where (account.contractor = :contractor)

		return (List<PayFirm>) Q(

"from PayFirm where (account.contractor = :contractor)"

		).
		  setParameter("contractor", contractor).
		  list();
	}

	public Contractor getPayWayContractor(Long payWay)
	{

/*

 select c from PayIt p join p.account.contractor c
 where (p.payWay = :payWay)

 */
		return (Contractor) Q(


"select c from PayIt p join p.account.contractor c\n" +
"where (p.payWay = :payWay)"

		).
		  setParameter("payWay", payWay).
		  setMaxResults(1).
		  uniqueResult();
	}


	/**
	 * Returns the records as Object[] of:
	 *
	 *  0  PayWay entity;
	 *  1  pay way balance income;
	 *  2  pay way balance expense.
	 */
	@SuppressWarnings("unchecked")
	public List selectAccountPayWays(Long account)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Pay", PayIt.class);
		qb.nameEntity("Value", AggrValue.class);
		qb.setClauseFrom("Value v, Pay p join p.payWay w");

		//~: select clause
		qb.setClauseSelect("w, v.aggrPositive, v.aggrNegative");

		//~: order by clause
		qb.setClauseOrderBy("lower(w.name)");


		//~: pay it >< volume  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "v.owner.id = p.id"
		);

		//~: balance volume aggr type
		qb.getClauseWhere().addPart(
		  "v.aggrType = :aggrType"
		).
		  param("aggrType", Accounts.aggrTypePayItBalance());

		//~: account restriction
		qb.getClauseWhere().addPart(
		  "p.account.id = :account"
		).
		  param("account", account);


		//~: assign empty volumes
		List<Object[]> res = (List<Object[]>) QB(qb).list();

		for(Object[] rec : res)
		{
			if(rec[1] == null) rec[1] = new BigDecimal(0).setScale(5);
			if(rec[2] == null) rec[2] = new BigDecimal(0).setScale(5);
		}

		return res;
	}

	public int countPayWays(PayWaysModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Pay", PayIt.class);
		qb.setClauseFrom(
		  "Pay p join p.payWay w join p.account a left outer join a.contractor c"
		);

		//~: select clause
		qb.setClauseSelect("count(distinct w.id)");

		//~: domain
		qb.getClauseWhere().addPart(
		  "w.domain.id = :domain"
		).
		  param("domain", mb.getDomain());

		//?: {not include own accounts}
		if(mb.isOwnOnly())
			qb.getClauseWhere().addPart("c is null");

		//~: restrict by the selection set
		restrictPayWaysBySelSet(qb, mb.getSelSet());

		//~: restrict by the names
		payWaysSearch(qb, mb.searchNames(), mb.isOwnOnly());


		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	/**
	 * Returns the records as Object[] of:
	 *
	 *  0  PayWay entity;
	 *  1  Contractor entity (if exists);
	 *  2  pay way balance income;
	 *  3  pay way balance expense;
	 */
	@SuppressWarnings("unchecked")
	public List selectPayWays(PayWaysModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//<: select the payment ways

		//~: from clause
		qb.nameEntity("Pay", PayIt.class);
		qb.setClauseFrom(
		  "Pay p join p.payWay w join p.account a left outer join a.contractor c"
		);

		//~: select clause
		qb.setClauseSelect("distinct w, c, 0, 0");

		//~: order by
		if(mb.isOwnOnly())
			qb.setClauseOrderBy("w.name");
		else
			qb.setClauseOrderBy("c.nameProc");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: domain
		qb.getClauseWhere().addPart(
		  "w.domain.id = :domain"
		).
		  param("domain", mb.getDomain());

		//?: {not include own accounts}
		if(mb.isOwnOnly())
			qb.getClauseWhere().addPart("c is null");

		//~: restrict by the selection set
		restrictPayWaysBySelSet(qb, mb.getSelSet());

		//~: restrict by the names
		payWaysSearch(qb, mb.searchNames(), mb.isOwnOnly());

		List<Object[]> res = (List<Object[]>) QB(qb).list();

		//>: select the payment ways


		//<: aggregate the balances

		qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Value", AggrValue.class);
		qb.nameEntity("Pay", PayIt.class);
		qb.setClauseFrom("Value v, Pay p");

		//~: select clause
		qb.setClauseSelect("sum(v.aggrPositive), sum(v.aggrNegative)");


		//~: payment way restriction
		WherePart ww = qb.getClauseWhere().addPart(
		  "p.payWay.id = :way"
		);

		//~: pay it >< volume  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "v.owner.id = p.id"
		);

		//~: balance volume aggr type
		qb.getClauseWhere().addPart(
		  "v.aggrType = :aggrType"
		).
		  param("aggrType", Accounts.aggrTypePayItBalance());

		Query q = QB(qb);

		for(Object[] rec : res)
		{
			//~: restrict the payment way
			q.setParameter(SU.sXs(ww.getParamsPrefix()) + "way",
			  ((PayWay)rec[0]).getPrimaryKey()
			);

			//!: select the aggregated volumes
			Object[] v = (Object[]) q.uniqueResult();

			rec[2] = (v[0] != null)?(v[0]):(new BigDecimal(0).setScale(5));
			rec[3] = (v[1] != null)?(v[1]):(new BigDecimal(0).setScale(5));
		}

		return res;

		//>: aggregate the balances
	}

	/**
	 * Returns array with the Payment Way balance as:
	 *  [0] income, [1] expense, [2] balance (scaled to 2)
	 */
	public BigDecimal[] getPayWayBalance(Long payWay)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Pay", PayIt.class);
		qb.nameEntity("Value", AggrValue.class);
		qb.setClauseFrom("Value v, Pay p");

		//~: select clause
		qb.setClauseSelect("sum(v.aggrPositive), sum(v.aggrNegative), 0");

		//~: group by clause
		qb.setClauseGroupBy("p.payWay.id");

		//~: account restriction
		WherePart wa = qb.getClauseWhere().addPart(
		  "p.payWay.id = :payWay"
		).
		  param("payWay", payWay);

		//~: pay it >< volume  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "v.owner.id = p.id"
		);

		//~: balance volume aggr type
		qb.getClauseWhere().addPart(
		  "v.aggrType = :aggrType"
		).
		  param("aggrType", Accounts.aggrTypePayItBalance());


		Object[] res = (Object[]) QB(qb).uniqueResult();

		if(res[0] == null) res[0] = new BigDecimal(0).setScale(5);
		if(res[1] == null) res[1] = new BigDecimal(0).setScale(5);

		return new BigDecimal[] {
		  (BigDecimal) res[0], (BigDecimal) res[1],
		  ((BigDecimal)res[0]).subtract((BigDecimal)res[1]).
		    setScale(2, BigDecimal.ROUND_HALF_UP)
		};
	}


	/* Get Settling */

	@SuppressWarnings("unchecked")
	public List<Payment> getPayments(Long payOrder)
	{

// from Payment where (payOrder.id = :payOrder)

		return (List<Payment>) Q(

"from Payment where (payOrder.id = :payOrder)"

		).
		  setParameter("payOrder", payOrder).
		  list();
	}



	/* private: shortage routines */

	private void accountsSearch(QueryBuilder qb, String[] words, boolean noc)
	{
		if(words != null) for(String w : words) if((w = SU.s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			if(!noc)
				p.addPart("c.nameProc like :w").param("w", w);
			p.addPart("lower(a.name) like :w").param("w", w);
			p.addPart("lower(a.code) like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}

	private void restrictAccountsBySelSet(QueryBuilder qb, String selset)
	{
		if(selset == null) return;

		WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(p);

/*

 a.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 c.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 a.id in (select p.account.id from Pay p where p.id in
   (select si.object from SelItem si join si.selSet ss
     where (ss.name = :set) and (ss.login.id = :login)))

 */
		//~: directly selected accounts
		p.addPart(

"a.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());

		//~: contractors
		p.addPart(

"c.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());

		//~: payment links
		p.addPart(

"a.id in (select p.account.id from Pay p where p.id in\n" +
"  (select si.object from SelItem si join si.selSet ss\n" +
"    where (ss.name = :set) and (ss.login.id = :login)))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());
	}

	private void payWaysSearch(QueryBuilder qb, String[] words, boolean noc)
	{

/*

 (select lower(

   pb.bankId || pb.bankName || pb.bankAccount ||
   pb.remitteeAccount || pb.remitteeName )

  from PayBank pb where (pb.id = w.id)) like :w

 */

		if(words != null) for(String w : words) if((w = SU.s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			if(!noc)
				p.addPart("c.nameProc like :w").param("w", w);
			p.addPart("lower(w.name) like :w").param("w", w);

			p.addPart(

"(select lower(\n" +
"\n" +
"   pb.bankId || pb.bankName || pb.bankAccount ||\n" +
"   pb.remitteeAccount || pb.remitteeName )\n" +
"\n" +
"  from PayBank pb where (pb.id = w.id)) like :w"

			).param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}

	private void restrictPayWaysBySelSet(QueryBuilder qb, String selset)
	{
		if(selset == null) return;

		WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(p);

/*

 w.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 a.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 c.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 p.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 */
		//~: directly selected payment ways
		p.addPart(

"w.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());

		//~: accounts
		p.addPart(

"a.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());

		//~: contractors
		p.addPart(

"c.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());

		//~: payment links
		p.addPart(

"p.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());
	}
}