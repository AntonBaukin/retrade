package com.tverts.actions;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * A collection of actions shared between
 * various action builders.
 *
 * @author anton.baukin@gmail.com
 */
public class ActionsCollection
{
	/* save primary instance */

	/**
	 * Saves given {@link NumericIdentity} instance.
	 * If it has no primary key, the key would be generated.
	 */
	public static class SaveNumericIdentified
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public SaveNumericIdentified(ActionTask task)
		{
			super(task);
		}

		/* protected: ActionBase interface */

		protected void openValidate()
		{
			super.openValidate();

			//?: {the target is not a primary identity}
			if(!(targetOrNull() instanceof NumericIdentity))
				throw new IllegalStateException(String.format(
				  "Can't save entity of class '%s' not a NumericIdentity!",
				  OU.cls(targetOrNull())
				));
		}

		protected void execute()
		  throws Throwable
		{
			//~: set the primary key
			setPrimaryKey();

			//!: save
			doSave();
		}

		public Object  getResult()
		{
			return targetOrNull();
		}

		/* protected: execution */

		protected void setPrimaryKey()
		{
			NumericIdentity e = target(NumericIdentity.class);

			//?: {entity still has no primary key} generate it
			if(e.getPrimaryKey() == null)
				HiberPoint.setPrimaryKey(session(), e);
		}

		protected void doSave()
		{
			//!: invoke save
			session().save(target());
		}
	}
}