package com.tverts.faces;

/* JavaServer Faces */


import javax.faces.context.FacesContext;

/**
 * Provides utility operations helping with JSF.
 *
 * @author anton.baukin@gmail.com
 */
public class FacesPoint
{
	/* FacesPoint Singleton */

	public static FacesPoint getInstance()
	{
		return INSTANCE;
	}

	private static final FacesPoint INSTANCE =
	  new FacesPoint();

	protected FacesPoint()
	{}


	/* helper routines */

	public static Object requestBean(String name)
	{
		if(name == null) throw new IllegalArgumentException();

		FacesContext ctx = FacesContext.getCurrentInstance();

		//~: get the bean from the map
		Object res = ctx.getExternalContext().
		  getRequestMap().get(name);
		if(res != null) return res;

		//~: request it in evaluate way
		return ctx.getApplication().evaluateExpressionGet(
		  ctx, String.format("#{%s}", name), Object.class);
	}

	/**
	 * Returns the bean by the name. The bean must be defined
	 * and be instance of the class given.
	 */
	@SuppressWarnings("unchecked")
	public static <O> O  requestBean(String name, Class<O> c1ass)
	{
		if(c1ass == null) throw new IllegalArgumentException();

		Object res = requestBean(name);
		if(res == null) throw new IllegalStateException(String.format(
		  "JavaServer Faces request Bean '%s' does not exist!"
		));

		if(!c1ass.isAssignableFrom(res.getClass()))
			throw new IllegalStateException(String.format(
			  "JavaServer Faces request Bean '%s' is not an instance of " +
			  "'%s' class!", name, c1ass.getName()
			));

		return (O)res;
	}

	public static <O> O  requestBean(Class<O> bean)
	{
		StringBuilder s = new StringBuilder(bean.getSimpleName());
		s.setCharAt(0, Character.toLowerCase(s.charAt(0)));

		return requestBean(s.toString(), bean);
	}
}