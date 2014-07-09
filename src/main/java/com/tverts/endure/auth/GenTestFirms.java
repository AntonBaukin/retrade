package com.tverts.endure.auth;

/* Java */

import java.net.URL;
import java.util.ArrayList;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: genesis */

import com.tverts.api.clients.Firm;
import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: events */

/* com.tverts: secure */

/* com.tverts: api */

/* com.tverts: endure (core + persons) */

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

	public void    generate(GenCtx ctx)
	  throws GenesisError
	{
		try
		{
			new ReadTestFirms(ctx).
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

	protected URL  getDataFile()
	{
		return EX.assertn(
		  getClass().getResource("GenTestFirms.xml"),
		  "No GenTestFirms.xml file found!"
		);
	}

	protected void generate(GenCtx ctx, GenState s)
	{

	}


	/* protected: XML Handler State */

	protected static class GenState
	{
		public Firm firm;
	}


	/* protected: XML Handler */

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