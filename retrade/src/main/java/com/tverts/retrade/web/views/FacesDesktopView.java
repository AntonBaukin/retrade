package com.tverts.retrade.web.views;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ViewWithModes;

/* com.tverts: api */

import com.tverts.api.clients.Person;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * JavaServer Faces view for index page that
 * implements the ReTrade application desktop.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesDesktopView extends ViewWithModes
{
	/* View Support */

	public String  getUserDisplayName()
	{
		Person  p = EX.assertn(getPerson().getOx());
		String  L = p.getLastName();
		boolean l = !SU.sXe(L);
		String  F = p.getFirstName();
		boolean f = !SU.sXe(F);
		String  M = p.getMiddleName();
		boolean m = !SU.sXe(M);
		String  I = p.getInitials();
		boolean i = !SU.sXe(I);

		if(l & f & m)
			return SU.cats(L, " ", F, " ", M);

		if(l & i)
			return SU.cats(L, " ", I);

		if(l)
			return L;

		if(f & m)
			return SU.cats(F, " ", M);

		return SecPoint.loadLogin().getCode();
	}

	public boolean isDomainStuff()
	{
		return (EX.assertn(getPerson().getOx()).getFirmKey() != null);
	}


}