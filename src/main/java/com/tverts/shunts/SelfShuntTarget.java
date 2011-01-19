package com.tverts.shunts;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/**
 * Allows to invoke arbitrary object annotated
 * with {@link SelfShuntUnit} and with
 * {@link SelfShuntMethod} as a {@link SelfShunt}
 * instance.
 *
 * If the object targeted must be a Spring prototype
 * bean, consider {@link SelfShuntBean} instead: in
 * this case you would have to name the bean and refer
 * it by the name to create prototype on each method
 * invocation.
 *
 * Note that all invocations of shunt methods share
 * the same target instance. It is not cleared after
 * all the methods are invoked. Create your own
 * implementations to free shared resources defining
 * {@link #freeShuntEnvironment()}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      SelfShuntTarget
       extends    SelfShuntBaseAnnotated
       implements SelfShuntReference
{
	/* public: SelfShuntReference interface */

	public List<SelfShunt>
	               dereferObjects()
	{
		return Collections.<SelfShunt>singletonList(this);
	}

	/* public: SelfShuntTarget interface */

	public Object getTarget()
	{
		return target;
	}

	/**
	 * Note that the target instance class must be
	 * annotated with {@link SelfShuntUnit}, or
	 * {@link IllegalStateException} is raised.
	 */
	public void   setTarget(Object target)
	{
		if(target == null)
			throw new IllegalArgumentException();

		this.target = target;

		if(atShuntUnit() == null)
			throw new IllegalArgumentException(String.format(
			  "SelfShuntTarget can't be created for the class '%s'" +
			  "that is not annotated width @SelfShuntUnit!",
			  target.getClass().getName()));
	}

	/* private: the target */

	private Object target;
}