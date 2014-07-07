package com.tverts.endure.person;

/* standard Java classes */

import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;

/* com.tverts: endure (logins) */

import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.GetAuthLogin;


/**
 * Actions builder on a {@link PersonEntity} entities.
 *
 * @author anton.baukin@gmail.com
 */
public class ActPerson extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType UPDATE =
	  ActionType.UPDATE;


	/* parameters */

	/**
	 * Parameter to edit Person with
	 * {@link EditPersonModelBean}.
	 */
	public static final String PARAM_PERSON =
	  ActPerson.class.getName() + ": person";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			savePerson(abr);

		if(UPDATE.equals(actionType(abr)))
			updatePerson(abr);
	}


	/* protected: action methods */

	protected void savePerson(ActionBuildRec abr)
	{
		//?: {target is not a Person}
		checkTargetClass(abr, PersonEntity.class);

		//~: save the person
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set person unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, getUnityType());

		complete(abr);
	}

	protected void updatePerson(ActionBuildRec abr)
	{
		//?: {target is not a Person}
		checkTargetClass(abr, PersonEntity.class);

		//~: add update action
		chain(abr).first(createUpdateAction(abr));

		complete(abr);
	}


	/* protected: actions build support */

	protected UnityType getUnityType()
	{
		return UnityTypes.unityType(
		  PersonEntity.class, Persons.TYPE_PERSON);
	}

	protected Action    createUpdateAction(ActionBuildRec abr)
	{
		return new UpdatePersonAction(task(abr)).
		  setEditModel(param(abr, PARAM_PERSON, EditPersonModelBean.class));
	}


	/* update action */

	public static class UpdatePersonAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public UpdatePersonAction(ActionTask task)
		{
			super(task);
		}


		/* public: UpdatePersonAction (bean) interface */

		public UpdatePersonAction setEditModel(EditPersonModelBean editModel)
		{
			this.editModel = editModel;
			return this;
		}


		/* public: Action interface */

		public PersonEntity getResult()
		{
			return target(PersonEntity.class);
		}

		protected void execute()
		  throws Throwable
		{
			//~: update from the edit model
			if(editModel != null)
				editModel.copy(target(PersonEntity.class));

			//~: update the logins
			updateLogins();
		}


		/* protected: execution parts */

		protected void updateLogins()
		{
			List<AuthLogin> logins = bean(GetAuthLogin.class).
			  getLogins(target(PersonEntity.class));

			for(AuthLogin login : logins)
				//~: clear the name, then update it on save
				login.setName(null);
		}


		/* private: parameters */

		private EditPersonModelBean editModel;
	}
}