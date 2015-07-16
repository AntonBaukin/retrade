package com.tverts.support.fmt;

/* Java */

import java.util.Date;

/* com.tverts: endure (core, catalogues) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.UnityType;
import com.tverts.endure.cats.CodedEntity;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Title formatter for documents.
 *
 * @author anton.baukin@gmail.com.
 */
public abstract class DocFmtBase extends FmtBase
{
	protected String format(FmtCtx ctx)
	{
		StringBuilder   s = new StringBuilder(92);
		NumericIdentity i = (NumericIdentity) ctx.obj();
		UnityType       t = EX.assertn(getDocType(ctx),
		  "Document [", i.getPrimaryKey(), "] Unity Type is undefined!"
		);

		//~: invoice type name
		if(t.getTitleLo() != null)
			s.append(t.getTitleLo());
		else
			s.append(t.getTitle());

		//~: code
		s.append(formatCode(ctx));

		//?: {long} add date
		Date d; if(ctx.is(LONG) && ((d = getTime(ctx)) != null))
			s.append(formatTime(ctx, d));

		return s.toString();
	}

	protected abstract UnityType getDocType(FmtCtx ctx);

	protected String             formatCode(FmtCtx ctx)
	{
		NumericIdentity i = (NumericIdentity) ctx.obj();

		if(!(i instanceof CodedEntity))
			return "";

		if(ctx.is(DISPLAY))
			return SU.cats(" ⸬ ", ((CodedEntity)i).getCode());

		return SU.cats(" №", ((CodedEntity)i).getCode());
	}

	protected Date               getTime(FmtCtx ctx)
	{
		return null;
	}

	protected String             formatTime(FmtCtx ctx, Date d)
	{
		if(ctx.is(DISPLAY))
			return SU.cats(" ⸬ ", DU.datetime2str(d));

		return SU.cats(" от ", DU.datetime2str(d));
	}
}