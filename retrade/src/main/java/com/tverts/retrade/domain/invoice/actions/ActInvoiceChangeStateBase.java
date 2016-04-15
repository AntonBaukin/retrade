package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionsCollection.DeleteEntity;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: endure (domain core) */

import com.tverts.endure.core.ActUnity;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceState;


/**
 * {@link InvoiceState}s has no own action builders
 * because the states are not separated from the
 * invoices owning them. The request to change the
 * state comes not to a state, but to it's invoice.
 *
 * This class provides useful things to the builders
 * of actions changing the states.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActInvoiceChangeStateBase
       extends        ActInvoiceBase
{
	/* protected: state manipulation */

	protected void   saveAssignState(ActionBuildRec abr, InvoiceState state)
	{
		//~: set the primary key
		if(state.getPrimaryKey() == null)
			setPrimaryKey(session(abr), state, isTestTarget(abr));

		//~: save & assign invoice state
		chain(abr).first(new SaveAssignInvoiceState(task(abr), state));

		//~: create state' unity
		xnest(abr, ActUnity.CREATE, state);
	}

	protected void   deleteState(ActionBuildRec abr)
	{
		//HINT: we always delete the unity after the object owning it.

		//~: delete the state' unity
		xnest(abr, ActUnity.DELETE,
		  target(abr, Invoice.class).getInvoiceState());

		//~: delete the state instance itself
		chain(abr).first(createDeleteStateAction(abr));
	}

	protected Action createDeleteStateAction(ActionBuildRec abr)
	{
		return new DeleteInvoiceState(task(abr));
	}


	/* delete invoice state action */

	public static class DeleteInvoiceState extends DeleteEntity
	{
		/* public: constructor */

		public DeleteInvoiceState(ActionTask task)
		{
			super(task);
			setFlushAfter(true); //<-- by default
		}


		/* public: DeleteEntity (access the parameters) */

		public InvoiceState getDeleteTarget()
		{
			if(this.state != null) return this.state;

			Object obj = targetOrNull();

			if(obj instanceof Invoice)
				return ((Invoice)obj).getInvoiceState();

			if(obj instanceof InvoiceState)
				return (InvoiceState)obj;

			return null;
		}


		/* protected: invoice state reference */

		protected InvoiceState state;
	}


	/* save & assign invoice state action */

	public static class SaveAssignInvoiceState
	       extends      SaveNumericIdentified
	{
		/* public: constructor */

		public SaveAssignInvoiceState(ActionTask task, InvoiceState target)
		{
			super(task, target);
		}


		/* public: SaveNumericIdentified interface */

		public InvoiceState getSaveTarget()
		{
			return ((InvoiceState)super.getSaveTarget());
		}


		/* protected: SaveNumericIdentified (execution) */

		protected void execute()
		  throws Throwable
		{
			Invoice invoice = target(Invoice.class);

			//~: save the state
			getSaveTarget().setInvoice(invoice);

			//~: assign new state to the invoice instance
			invoice.setInvoiceState(getSaveTarget());

			super.execute();
		}


		/* protected: invoice state reference */

		protected InvoiceState state;
	}
}