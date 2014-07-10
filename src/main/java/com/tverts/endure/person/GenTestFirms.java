package com.tverts.endure.person;

/* Java */

import java.net.URL;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: api */

import com.tverts.api.clients.Firm;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: support */

import com.tverts.support.EX;
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
	{}

	protected FirmEntity saveFirm(GenCtx ctx, GenState s)
	{
		FirmEntity fe = new FirmEntity();

		//=: domain
		fe.setDomain(ctx.get(Domain.class));

		//=: firm ox-object
		fe.setOx(s.firm);

		//!: save action
		actionRun(ActionType.SAVE, fe);

		return fe;
	}

	protected void       genFirm(GenCtx ctx, FirmEntity fe)
	{

	}


	/* protected: XML Handler State */

	protected static class GenState
	{
		public Firm firm;
	}


	/* protected: XML Handler */

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
			//?: {<computer> | <person>}
			if(islevel(1)) try
			{
				EX.assertx(istag("firm"));

				//~: assign the tags
				requireFillClearTags(state().firm, "code", "name", "full-name");

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