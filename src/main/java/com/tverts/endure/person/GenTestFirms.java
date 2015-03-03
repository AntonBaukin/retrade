package com.tverts.endure.person;

/* Java */

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenUtils;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: api */

import com.tverts.api.clients.Firm;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.xml.SaxProcessor;


/**
 * Generates test Firm Entity instances.
 * Overwritten in ReTrade to create the
 * Contractors with the related Firms.
 *
 * @author anton.baukin@gmail.com.
 */
public class GenTestFirms extends GenesisHiberPartBase
{
	/* Genesis Interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		try
		{
			createProcessor(ctx).
			  process(getDataFile().toString());
		}
		catch(Throwable e)
		{
			e = EX.xrt(e);

			if(e instanceof GenesisError)
				throw (GenesisError)e;
			else
				throw new GenesisError(e, this, ctx);
		}
	}


	/* protected: generation */

	protected URL        getDataFile()
	{
		return EX.assertn(
		  getClass().getResource("GenTestFirms.xml"),
		  "No GenTestFirms.xml file found!"
		);
	}

	protected void       generate(GenCtx ctx, GenState s)
	{
		EX.asserts(s.firm.getCode());

		//~: lookup the firm exists
		FirmEntity fe = bean(GetFirm.class).
		  getFirm(ctx.get(Domain.class), s.firm.getCode());

		//?: {found it}
		if(fe != null)
			updateFirm(ctx, s, fe);
		else
			saveFirm(ctx, s);
	}

	protected void       updateFirm(GenCtx ctx, GenState s, FirmEntity fe)
	{
		rememberFirm(ctx, fe);
	}

	protected FirmEntity saveFirm(GenCtx ctx, GenState s)
	{
		FirmEntity fe = new FirmEntity();

		//=: domain
		fe.setDomain(ctx.get(Domain.class));

		//=: firm ox-object
		fe.setOx(s.firm);

		//~: assign the state
		genFirm(ctx, fe);

		//!: save action
		actionRun(ActionType.SAVE, fe);

		rememberFirm(ctx, fe);
		return fe;
	}

	protected void       genFirm(GenCtx ctx, FirmEntity fe)
	{
		Firm f = EX.assertn(fe.getOx());

		//?: {has no tax number}
		if(SU.sXe(f.getTaxNumber()))
			f.setTaxNumber(GenUtils.number(ctx.gen(), 10));

		//?: {has no tax code}
		if(SU.sXe(f.getTaxCode()))
			f.setTaxCode(GenUtils.number(ctx.gen(), 9));

		//?: {has no registry code}
		if(SU.sXe(f.getRegCode()))
			f.setTaxCode(GenUtils.number(ctx.gen(), 13));

		//?: {has no phones}
		if(SU.sXe(f.getPhones()))
			f.setPhones(GenUtils.phones(ctx.gen(), "+7-4822-", 2, 6));

		//?: {has no registry address}
		if(f.getRegistryAddress() == null)
			f.setRegistryAddress(Addresses.INSTANCE.
				 selectRandomAddress(ctx.gen()));

		//?: {has no contact address}
		if(f.getContactAddress() == null)
			//?: {really need it}
			if(ctx.gen().nextInt(5) == 0)
				f.setContactAddress(Addresses.INSTANCE.
				 selectRandomAddress(ctx.gen()));
	}

	@SuppressWarnings("unchecked")
	protected void       rememberFirm(GenCtx ctx, FirmEntity fe)
	{
		Map<String, FirmEntity> fem = (Map<String, FirmEntity>)
		  ctx.get((Object)FirmEntity.class);

		if(fem == null) ctx.set( FirmEntity.class,
		  fem = new LinkedHashMap<String, FirmEntity>(17));

		EX.asserts(fe.getCode());
		FirmEntity fx = fem.put(fe.getCode(), fe);
		EX.assertx((fx == null) || fx.equals(fe));
	}


	/* protected: XML Processor State */

	protected static class GenState
	{
		public Firm firm;
	}


	/* protected: XML Processor */

	protected SaxProcessor<?> createProcessor(GenCtx ctx)
	{
		return new ReadTestFirms(ctx);
	}

	protected class ReadTestFirms extends SaxProcessor<GenState>
	{
		public ReadTestFirms(GenCtx ctx)
		{
			this.ctx = ctx;
			this.collectTags = true;
		}


		/* protected: SaxProcessor interface */

		protected void createState()
		{
			//?: <firm>
			if(istag(1, "firm"))
			{
				event().state(new GenState());
				state().firm = new Firm();
			}

			else if(islevel(1))
				throw wrong();
		}

		protected void open()
		{}

		protected void close()
		{
			//?: {<firm>}
			if(islevel(1)) try
			{
				EX.assertx(istag("firm"));

				//~: assign the tags
				requireFillClearTags(state().firm, true, "code", "name", "full-name");

				//!: do generate
				generate(ctx, state());
			}
			catch(Throwable e)
			{
				throw EX.wrap(e);
			}
		}


		/* genesis context */

		private GenCtx ctx;
	}
}