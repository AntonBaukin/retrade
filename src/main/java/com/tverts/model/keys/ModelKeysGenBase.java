package com.tverts.model.keys;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelBeanInfo;
import com.tverts.model.ModelKeysGen;

/* com.tverts: support */

import static com.tverts.support.OU.getSimpleNameLowerFirst;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.cats;


/**
 * COMMENT ModelKeysGenBase
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelKeysGenBase
       implements     ModelKeysGen
{
	public static final long serialVersionUID = 0L;


	/* public: ModelKeysGen interface */

	public String     genModelKey(ModelBean bean)
	{
		//?: {the bean is undefined}
		if(bean == null) throw new IllegalArgumentException(
		  "Can't generate key for Model Bean undefined!");

		//?: {the bean is unique} invoke special variant
		if(isUniqueBean(bean))
			return genModelKeyUnique(bean);

		//~: generate the key with random part
		return genModelKeySerial(bean);
	}


	/* protected: generation details */

	protected abstract String
	                  generateSerialKey(ModelBean bean);

	protected String  genModelKeyUnique(ModelBean bean)
	{
		ModelBeanInfo info = bean.getClass().
		  getAnnotation(ModelBeanInfo.class);
		String        key  = (info == null)?(null):(info.key());

		//?: {the annotation' key is empty} take the simple name
		if((key = s2s(key)) == null)
			key = getSimpleNameLowerFirst(bean.getClass());

		return key;
	}

	protected String  genModelKeySerial(ModelBean bean)
	{
		return cats(selectKeyPrefix(bean), generateSerialKey(bean));
	}

	protected boolean isUniqueBean(ModelBean bean)
	{
		ModelBeanInfo info = bean.getClass().
		  getAnnotation(ModelBeanInfo.class);

		return (info != null) && info.unique();
	}

	/**
	 * This (default) implementation inspects the bean
	 * {@link ModelBeanInfo#key()} annotation's value.
	 *
	 * If it is not an empty string, returns it with
	 * '_' suffix added. Else returns the simple name
	 * of the class having first letter turned to lower
	 * if not all the string is in upper case.
	 */
	protected String  selectKeyPrefix(ModelBean bean)
	{
		ModelBeanInfo info = bean.getClass().
		  getAnnotation(ModelBeanInfo.class);
		String        key  = (info == null)?(null):(info.key());

		//?: {the annotation' key is empty}
		if((key = s2s(key)) == null)
			key = getSimpleNameLowerFirst(bean.getClass());

		return cats(key, '_');
	}
}