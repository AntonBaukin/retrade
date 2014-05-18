package com.tverts.retrade.domain.account;

/* standard Java classes */

import java.util.Random;

/* SAX Parser */

import javax.xml.parsers.SAXParserFactory;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: support */

import static com.tverts.support.SU.sXe;


/**
 * Support class for generation of various
 * instances related to the payment ways.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestPayWays
{
	/* GenTestBanks Singleton */

	public static GenTestPayWays getInstance()
	{
		return INSTANCE;
	}

	private static final GenTestPayWays INSTANCE =
	  new GenTestPayWays();

	protected GenTestPayWays()
	{}


	/* public: GenTestBanks interface */

	public void    genTestPayBank(PayBank pb, Random rnd)
	{
		ReadTestBanks r = new ReadTestBanks().setSeed(rnd.nextInt());

		//!: invoke the sax parser
		try
		{
			SAXParserFactory.newInstance().newSAXParser().parse(
			  GenTestPayWays.class.getResource("GenTestBanks.xml").
			    toURI().toString(), r
			);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		//?: {has no result}
		if(sXe(r.getBankId()) || sXe(r.getBankName()) || sXe(r.getBankAccount()))
			throw new IllegalStateException();

		//~: copy Bank ID
		pb.setBankId(r.getBankId());

		//~: copy bank name
		pb.setBankName(r.getBankName());

		//~: copy bank account
		pb.setBankAccount(r.getBankAccount());

		//~: generate remittee account
		pb.setRemitteeAccount(Accounts.genAccount(rnd, r.getBankId()));

		//~: generate remittee (russian) Tax ID
		pb.setRemitteeTaxId(Accounts.genTaxIdRus(rnd, false));

		//~: set fixed remittee code
		pb.setRemitteeTaxCode("774301001");
	}

	/**
	 * Creates and saves {@link PayBank} instance for the
	 * account given. The parameters are as follows:
	 *
	 *  0) pay way name (optional);
	 *  1) pay way remarks (optional);
	 *  2) remittee name.
	 */
	public PayBank createTestPayBank(GenCtx ctx, String... p)
	{
		PayBank pb = new PayBank();

		//~: set primary key
		setPrimaryKey(ctx.session(), pb, true);

		//~: domain
		pb.setDomain(ctx.get(Domain.class));

		//~: open date
		pb.setOpened(new java.util.Date());

		//!: generate the bank info
		genTestPayBank(pb, ctx.gen());

		//~: pay way name
		if(p[0] == null)
			p[0] = String.format("Счёт %s в %s",
			  pb.getRemitteeAccount().substring(0, 5),
			  pb.getBankName()
			);

		pb.setName(p[0]);

		//~: pay way remarks
		if(p[1] != null)
			pb.setRemarks(p[1]);

		//~: remittee name
		pb.setRemitteeName(p[2]);


		//!: save the payment destination
		actionRun(ActionType.SAVE, pb);

		return pb;
	}

	/**
	 * Creates and saves {@link PayBank} instance for the
	 * account given. The parameters are as follows:
	 *
	 *  0) pay way name;
	 *  1) pay way remarks (optional).
	 */
	public PayCash createTestPayCash(GenCtx ctx, String... p)
	{
		PayCash pc = new PayCash();

		//~: set primary key
		setPrimaryKey(ctx.session(), pc, true);

		//~: domain
		pc.setDomain(ctx.get(Domain.class));

		//~: open date
		pc.setOpened(new java.util.Date());

		//~: pay way name
		pc.setName(p[0]);

		//~: pay way remarks
		if(p[1] != null)
			pc.setRemarks(p[1]);


		//!: save the payment destination
		actionRun(ActionType.SAVE, pc);

		return pc;
	}
}