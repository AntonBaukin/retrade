package com.tverts.endure.msg;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.core.GetUnity;

/* com.tverts: support */

import com.tverts.hibery.HiberPoint;
import com.tverts.support.EX;


/**
 * Actions for the Message Boxes.
 *
 * @author anton.baukin@gmail.com.
 */
public class ActMsgBox extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE =
	  ActionType.SAVE;

	/**
	 * Ensures that the denoted source Unity
	 * and the link type names exists for
	 * target Message Box.
	 */
	public static final ActionType LINK =
	  new ActionType(MsgBoxObj.class, "link");


	/* parameters of the action task */

	/**
	 * Source Unity or it's primary key.
	 */
	public static final String SOURCE =
	  ActMsgBox.class.getName() + ": source unity";

	/**
	 * Link type name.
	 */
	public static final String TYPE   =
	  ActMsgBox.class.getName() + ": link type";


	/* Action Builder */

	public void      buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveMsgBox(abr);

		if(LINK.equals(actionType(abr)))
			ensureMsgLink(abr);
	}


	/* protected: action methods */

	protected void   saveMsgBox(final ActionBuildRec abr)
	{
		//?: {target is not a Message Box}
		checkTargetClass(abr, MsgBoxObj.class);

		//?: {box has the login assigned}
		MsgBoxObj mb = target(abr, MsgBoxObj.class);
		EX.assertn(mb.getLogin());

		//~: ensure ox
		mb.getOx(); //<-- side-effect
		mb.updateOx();

		//~: save the box
		chain(abr).first(new SaveNumericIdentified(task(abr)).setOwner(mb.getLogin()).
		setAfterSave(new Runnable()
		{
			public void run()
			{
				greetUser(abr);
			}
		}
		));

		complete(abr);
	}

	protected void   ensureMsgLink(ActionBuildRec abr)
	{
		//?: {target is not a Message Box}
		checkTargetClass(abr, MsgBoxObj.class);

		//~: ensure the link action
		chain(abr).first(new EnsureMsgLink(task(abr), getSource(abr), getType(abr)));

		complete(abr);
	}


	/* protected: action build support  */

	protected Unity  getSource(ActionBuildRec abr)
	{
		Object p = param(abr, SOURCE);

		//?: {primary key}
		if(p instanceof Long)
			p = EX.assertn(bean(GetUnity.class).getUnity((Long)p),
			  "Message source Unity [", p, "] is not found!"
			);

		//?: {United}
		if(p instanceof United)
			p = ((United)p).getUnity();

		//?: {not a Unity}
		if(!(p instanceof Unity))
			throw EX.arg("Message source Unity is not defined!");

		return (Unity)p;
	}

	protected String getType(ActionBuildRec abr)
	{
		return EX.asserts(param(abr, TYPE, String.class),
		  "Message link type name is not specified!"
		);
	}

	protected void   greetUser(ActionBuildRec abr)
	{
		Message msg = new Message();
		msg.setTitle("Добро пожаловать в систему РеТрейд!");

		//~: sent that message
		Messages.send(target(abr, MsgBoxObj.class), msg);
	}


	/* Ensure Link Action */

	protected static class EnsureMsgLink extends ActionWithTxBase
	{
		/* public: constructor */

		public EnsureMsgLink(ActionTask task, Unity source, String type)
		{
			super(task);
			this.source = source;
			this.type   = type;
		}


		/* public: Action interface */

		public MsgLink getResult()
		{
			return link;
		}

		protected void execute()
		  throws Throwable
		{
			MsgBoxObj box = target(MsgBoxObj.class);

			//~: find the existing link
			this.link = bean(GetMsg.class).
			  getBoxLink(box.getPrimaryKey(), source.getPrimaryKey(), type);

			//?: {found it} do nothing
			if(link != null) return;

			//~: create it
			link = new MsgLink();

			//=: primary key
			HiberPoint.setPrimaryKey(session(), link,
			  HiberPoint.isTestInstance(box));

			//=: message box
			link.setMsgBox(box);

			//=: link type
			link.setType(type);

			//=: source unity
			link.setSource(source);

			//!: save it
			session().save(link);
		}


		protected final Unity  source;
		protected final String type;
		protected MsgLink      link;
	}
}