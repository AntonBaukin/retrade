package com.tverts.system.tx;

/* com.tverts: actions */

import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionTx;
import com.tverts.actions.ActionWithTxBase;


/**
 * Action that invokes {@link TxPoint#txn(Tx, Object)}
 * on the task object with the own {@link ActionTx} context.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class SetTxAction extends ActionWithTxBase
{
	/* public: constructor */

	public SetTxAction(ActionTask task)
	{
		super(task);
	}

	public SetTxAction(ActionTask task, Object target)
	{
		super(task);
		this.target = target;
	}


	/* public: Action interface */

	public Object  getResult()
	{
		return (target != null)?(target):target();
	}


	/* protected: ActionBase interface */

	protected void execute()
	  throws Throwable
	{
		TxPoint.txn(tx(), getResult());
	}


	/* private: alternative target */

	private Object target;
}