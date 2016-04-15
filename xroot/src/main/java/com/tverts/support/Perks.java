package com.tverts.support;

/* Java */

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

/* com.tverts: support */

import com.tverts.support.fs.Invokable;
import com.tverts.support.fs.Taker;


/**
 * Support for perks: objective flags
 * or self-describing processing values.
 *
 * @author anton.baukin@gmail.com.
 */
public class Perks
{
	public Perks(Object... perks)
	{
		this.perks = Arrays.asList(perks);
	}

	public Perks(Collection<Object> perks)
	{
		this.perks = EX.assertn(perks);
	}

	protected final Collection<Object> perks;


	/* Perks Access */

	public boolean   when(Object perk)
	{
		return this.perks.contains(perk);
	}

	public Perks     when(Object perk, Invokable task)
	{
		if(this.perks.contains(perk)) try
		{
			task.invoke();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}

		return this;
	}

	@SuppressWarnings("unchecked")
	public <P> P     find(Class<P> perk)
	{
		EX.assertn(perk);

		for(Object p : perks)
			if((p != null) && perk.isAssignableFrom(p.getClass()))
				return (P)p;

		return null;
	}

	public <P> Perks find(Class<P> perk, Taker<P> take)
	{
		EX.assertn(take);

		P p = this.find(perk);
		if(p != null) try
		{
			take.take(p);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}

		return this;
	}
}