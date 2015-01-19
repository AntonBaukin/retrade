package com.tverts.endure.msg;

/* Java */

import java.util.Collection;
import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.HiberPoint;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Get-queries for the database relate to the Messages.
 *
 * @author anton.baukin@gmail.com.
 */
@Component
public class GetMsg extends GetObjectBase
{
	/* Message Boxes and Links */

	public MsgBoxObj  findMsgBox(Long login)
	{
		final String Q =
"from MsgBoxObj where (login.id = :login)";

		return object(MsgBoxObj.class, Q, "login", login);
	}

	public MsgLink    getBoxLink(Long box, Long source, String type)
	{
/*

 from MsgLink where (msgBox.id = :box) and
   (source.id = :source) and (type = :type)

 */

		final String Q =

"from MsgLink where (msgBox.id = :box) and\n" +
"  (source.id = :source) and (type = :type)";

		return object(MsgLink.class, Q, "box", box, "source", source, "type", type);
	}

	/**
	 * By the source Unity given finds distinct Message
	 * Boxes linked with one of the types listed.
	 */
	public List<Long> findLinkedBoxes(Long source, Collection<String> types)
	{
/*

 select distinct b.id from MsgLink l join m.msgBox m
   where (l.source.id = :source) and l.type in (:types)

 */
		final String Q =

"select distinct b.id from MsgLink l join m.msgBox m\n" +
"  where (l.source.id = :source) and l.type in (:types)";

		return list(Long.class, Q, "source", source, "types", types);
	}

	/**
	 * Finds all the message boxes linked with the source Unity
	 * by at least one of the types given and sends single
	 * message for that box.
	 */
	public void       send(Long source, Message msg, Collection<String> types)
	{
		EX.assertn(source);
		EX.assertn(msg);
		EX.asserte(types);

		List<Long> mbs  = findLinkedBoxes(source, types);
		MsgObj     m, x = null;

		//c: for all the boxes found
		for(Long mb : mbs)
		{
			m = new MsgObj();

			if(x != null)
			{
				//=: color
				m.setColor(x.getColor());

				//=: ox-bytes
				m.setOxBytes(m.getOxBytes());

				//=: ox-search
				m.setOxSearch(m.getOxSearch());
			}
			//~: create the first message
			else
				//=: ox-object
				(x = m).setOx(msg);

			//=: primary key
			HiberPoint.setPrimaryKey(session(), m,
			  HiberPoint.isTestPrimaryKey(mb)
			);

			//=: active
			m.setActive(m.getPrimaryKey());

			//=: message box
			m.setMsgBox(get(MsgBoxObj.class, mb));

			//!: save it
			session().save(m);

			//~: update the message box
			update(m.getMsgBox(), msg);
		}
	}

	public void       send(MsgBoxObj box, Message msg)
	{
		MsgObj m = new MsgObj();

		//=: primary key
		HiberPoint.setPrimaryKey(session(), m,
		  HiberPoint.isTestInstance(box)
		);

		//=: ox-object
		m.setOx(msg);

		//=: active
		m.setActive(m.getPrimaryKey());

		//=: message box
		m.setMsgBox(box);

		//!: save it
		session().save(m);

		//~: update the message box
		update(box, msg);
	}

	private void      update(MsgBoxObj mb, Message m)
	{
		MsgBox mox = mb.getOx();

		//~: ++ total count
		mox.setTotal(mox.getTotal() + 1);

		//?: {green}  ++ green count
		if(m.getColor() == 'G')
			mox.setGreen(mox.getGreen() + 1);
			//?: {red}    ++ green count
		else if(m.getColor() == 'R')
			mox.setRed(mox.getRed() + 1);
			//?: {orange} ++ orange count
		else if(m.getColor() == 'O')
			mox.setOrange(mox.getOrange() + 1);

		//!: update ox
		mb.updateOx();
	}
}