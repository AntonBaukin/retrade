package com.tverts.actions;

/* com.tverts: actions */

import static com.tverts.actions.ActionBuildersRoot.STEP_TARGET;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Checks the action build target' class, or
 * the {@link UnityType} if the type name
 * parameter set.
 *
 * (Technical: works on target phase only.)
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   ActionBuilderTypeChecker
       extends ActionBuilderCheckerBase
{

	/* public: ActionBuilderTypeChecker bean interface */

	public Class   getTypeClass()
	{
		return typeClass;
	}

	public void    setTypeClass(Class typeClass)
	{
		this.typeClass = typeClass;
	}

	public String  getTypeName()
	{
		return typeName;
	}

	public void    setTypeName(String typeName)
	{
		this.typeName = s2s(typeName);
	}

	/**
	 * When this parameter is set not only the
	 * direct entities are allowed to pass, but
	 * their unities also. By default disabled.
	 */
	public boolean isMatchUnity()
	{
		return matchUnity;
	}

	public void    setMatchUnity(boolean matchUnity)
	{
		this.matchUnity = matchUnity;
	}


	/* protected: action build dispatching */

	protected boolean   isActionBuildPossible(ActionBuildRec abr)
	{
		//?: {target step + the type needed}
		return isBuildStep(abr, STEP_TARGET) &&
		  isActionTargetMatch(abr);
	}

	@SuppressWarnings("unchecked")
	protected boolean   isActionTargetMatch(ActionBuildRec abr)
	{
		//?: {the target class is not defined} illegal config
		if(getTypeClass() == null)
			return false;

		//?: {there is no target instance}
		if(targetOrNull(abr) == null)
			return false;

		//?: {the type name is not defined} do direct class check
		if(getTypeName() == null)
			return isActionTargetClassMatch(abr);

		//~: do check the unity type
		return isActionTargetUnityTypeMatch(abr);
	}

	protected boolean   isActionTargetClassMatch(ActionBuildRec abr)
	{
		String c = targetClass(abr).getName();

		//?: {this is not a unity matched} check the class equals
		if(!isMatchUnity() || !Unity.class.getName().equals(c))
			return getTypeClass().getName().equals(c);

		UnityType ut = target(abr, Unity.class).getUnityType();
		return (ut != null) && ut.getTypeClass().getName().equals(c);
	}

	protected boolean   isActionTargetUnityTypeMatch(ActionBuildRec abr)
	{
		UnityType ut = findTargetUnityType(abr);
		return (ut != null) && ut.equals(getTypeClass(), getTypeName());
	}

	protected UnityType findTargetUnityType(ActionBuildRec abr)
	{
		Unity unity = null;

		if(isMatchUnity() && (target(abr) instanceof Unity))
			unity = target(abr, Unity.class);
		else if(target(abr) instanceof United)
			unity = target(abr, United.class).getUnity();

		return (unity == null)?(null):(unity.getUnityType());
	}


	/* private: dispatcher selector class  */

	private Class   typeClass;
	private String  typeName;
	private boolean matchUnity;
}