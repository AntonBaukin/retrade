package com.tverts.endure.msg;

/* Java */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.HiberPoint;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: system */

import com.tverts.system.SystemConfig;
import com.tverts.system.tx.TxPoint;

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

 select distinct mb.id from MsgLink l join l.msgBox mb
   where (l.source.id = :source) and l.type in (:types)

 */
		final String Q =

"select distinct mb.id from MsgLink l join l.msgBox mb\n" +
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

			//=: primary key
			HiberPoint.setPrimaryKey(session(), m);

			if(x != null)
			{
				//=: color
				m.setColor(x.getColor());

				//=: ox-bytes
				m.setOxBytes(x.getOxBytes());

				//=: ox-search
				m.setOxSearch(x.getOxSearch());
			}
			//~: create the first message
			else
				//=: ox-object
				(x = m).setOx(msg);

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
		HiberPoint.setPrimaryKey(session(), m);

		//=: ox-object
		m.setOx(msg);

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

		//~: update ox
		mb.updateOx();

		//~: set new tx-number
		TxPoint.txn(mb);
	}

	/**
	 * Removes all the messages in the box.
	 */
	public GetMsg     clear(MsgBoxObj mb)
	{
		final String Q = "delete from MsgObj where (msgBox = :mb)";

		Q(Q, "mb", mb).executeUpdate();
		return this;
	}

	/**
	 * Removes all the links from the sources to this box.
	 */
	public GetMsg     unlink(MsgBoxObj mb)
	{
		final String Q = "delete from MsgLink where (msgBox = :mb)";

		Q(Q, "mb", mb).executeUpdate();
		return this;
	}


	/* Messages Fetch */

	/**
	 * Returns Message Box object of the current user.
	 */
	public MsgBoxObj    msgBox()
	{
		return this.msgBox(SecPoint.login());
	}

	public MsgBoxObj    msgBox(Long login)
	{
		return EX.assertn( this.findMsgBox(login),
		  "Auth Login [", login, "] has no Message Box created!"
		);
	}

	/**
	 * This complex function finds the messages of the Message Box
	 * given by the primary key and places them in the resulting list.
	 *
	 * Filtering color must be one of the following:
	 *  N  for all the messages;
	 *  R  for red messages only;
	 *  G  for green messages only;
	 *  O  for red and orange messages.
	 *
	 * If 'id' argument is defined, the list found is centered by it:
	 * the center page starts with this id. Also left and right 2-pages.
	 *
	 * If 'pages' argument is not-zero, it's absolute value is added
	 * to the number of regular range pages in the older (positive)
	 * or newer (negative) sides of the range.
	 *
	 * Array 'no' of [2] items has the primary keys of the closest
	 * newer-item, and the older-item out of the range given.
	 * If an item are undefined, no such an item does exist.
	 *
	 * The size of the page is defined by {@link SystemConfig#getUserEventsPage()}
	 * system parameter. Parameter {@link SystemConfig#getUserEventsFetch()}
	 * defines the number of messages to fetch.
	 *
	 * The order of the items returned is always from the newest to the oldest.
	 */
	@SuppressWarnings("unchecked")
	public void         loadMsgs
	  (Long mb, List<MsgObj> res, Long[] no, Long id, int pages, char color)
	{
/*

 from MsgObj where (msgBox.id = :mb) and (IX <= :id) order by IX desc
 from MsgObj where (msgBox.id = :mb) and (IX  > :id) order by IX asc
 from MsgObj where (msgBox.id = :mb) and (IX is not null) order by IX desc

 */

		//~: find the filtering field
		final String IX = EX.assertn(
		  (color == 'N')?("id"):(color == 'R')?("red"):
		  (color == 'G')?("green"):(color == 'O')?("orangeRed"):(null)
		);

		//~: number of older items
		final int ON = SystemConfig.INSTANCE.getUserEventsFetch() +
		  ((pages > 0)?(SystemConfig.INSTANCE.getUserEventsPage() * pages):(0));
		EX.assertx(ON > 0);

		//~: number of newer items
		final int NN = SystemConfig.INSTANCE.getUserEventsFetch() +
		  SystemConfig.INSTANCE.getUserEventsPage() -
		  ((pages < 0)?(SystemConfig.INSTANCE.getUserEventsPage() * pages):(0));
		EX.assertx(NN > 0);

		//?: {select the initial range only}
		if(id == null)
		{
			final String Q =

"from MsgObj where (msgBox.id = :mb) and (IX is not null) order by IX desc".
			replaceAll("IX", IX);

			//~: select the items
			List<MsgObj> newer = (List<MsgObj>) Q(Q, "mb", mb).
			  setMaxResults(NN+1).list();

			//?: {got the required number}
			if(newer.size() == NN+1)
			{
				no[1] = newer.get(NN).getPrimaryKey();
				newer.remove(NN);
			}

			//~: take that items and return them
			res.addAll(newer);
			return;
		}

		//<: select the newer items

		//~: query to select newer items
		final String NQ =

"from MsgObj where (msgBox.id = :mb) and (IX  > :id) order by IX asc".
		  replaceAll("IX", IX);

		//~: select the items
		List<MsgObj> newer = (List<MsgObj>) Q(NQ, "mb", mb, "id", id).
		  setMaxResults(NN + 1).list();

		//?: {got the required number}
		if(newer.size() == NN+1)
		{
			no[0] = newer.get(NN).getPrimaryKey();
			newer.remove(NN);
		}

		//!: reverse the items as they my be index-descending
		Collections.reverse(newer);
		res.addAll(newer);

		//>: select the newer items


		//<: select the older items

		//~: query to select older items
		final String OQ =
"from MsgObj where (msgBox.id = :mb) and (IX <= :id) order by IX desc".
		  replaceAll("IX", IX);

		//~: select the items
		List<MsgObj> older = (List<MsgObj>) Q(OQ, "mb", mb, "id", id).
		  setMaxResults(ON + 1).list();

		//?: {got the required number}
		if(older.size() == ON+1)
		{
			no[1] = older.get(ON).getPrimaryKey();
			older.remove(ON);
		}

		res.addAll(older);

		//>: select the older items
	}

	public List<MsgObj> loadMsgs(Long mb, Long[] no, Long id, int pages, char color)
	{
		List<MsgObj> res = new ArrayList<>(
		  SystemConfig.getInstance().getUserEventsPage() +
		  2 * SystemConfig.getInstance().getUserEventsFetch()
		);

		if(no == null) no = new Long[2];
		EX.assertx(no.length == 2);

		loadMsgs(mb, res, no, id, pages, color);
		return res;
	}

	public void         setColors(MsgBoxObj mbo, Map<Long, Character> colors)
	{
		MsgBox  mb = mbo.getOx();
		boolean up = false;

		//c: each message & color
		for(Long id : colors.keySet())
		{
			//~: color
			char c = colors.get(id);
			EX.assertx(c == 'N' | c == 'G' | c == 'R' | c == 'O');

			//~: load the message
			MsgObj mo = (MsgObj) session().get(MsgObj.class, id);
			if(mo == null) continue;

			//sec: {not the same box}
			if(!mo.getMsgBox().equals(mbo))
				throw EX.forbid("Message object [", id, "] is not yours!");

			//?: {already has this color}
			if(mo.getColor() == c) continue;
			up = true; //<-- updated

			//~: remove-add color from the numbers
			num(mb, mo.getColor(), -1);
			num(mb, c, +1);

			//~: assign the new color
			mo.getOx().setColor(c);
			mo.updateOx(); //<-- update the object!
		}

		//?: {updated}
		if(up)
		{
			//~: set new tx-number
			TxPoint.txn(mbo);

			//~: update object
			mbo.updateOx();

			//!: flush the session
			HiberPoint.flush(session());
		}
	}

	public void         remove(MsgBoxObj mbo, Set<Long> ids)
	{
		MsgBox  mb = mbo.getOx();
		boolean up = false;

		//c: each message
		for(Long id : ids)
		{
			//~: load the message
			MsgObj mo = (MsgObj) session().get(MsgObj.class, id);
			if(mo == null) continue;

			//sec: {not the same box}
			if(!mo.getMsgBox().equals(mbo))
				throw EX.forbid("Message object [", id, "] is not yours!");

			//~: remove color from the numbers
			num(mb, mo.getColor(), -1);

			//~: remove from the totals
			EX.assertx(mb.getTotal() >  0);
			mb.setTotal(mb.getTotal() - 1);
			EX.assertx(mb.getTotal() >= 0);

			//~: delete it
			up = true; //<-- updated
			session().delete(mo);
		}

		//?: {updated}
		if(up)
		{
			//~: set new tx-number
			TxPoint.txn(mbo);

			//~: update object
			mbo.updateOx();

			//!: flush the session
			HiberPoint.flush(session());
		}
	}

	private static void num(MsgBox mb, char x, int d)
	{
		if(x == 'G')
		{
			EX.assertx(mb.getGreen() >= 0);
			mb.setGreen(mb.getGreen() + d);
			EX.assertx(mb.getGreen() >= 0);
		}
		else if(x == 'O')
		{
			EX.assertx(mb.getOrange() >= 0);
			mb.setOrange(mb.getOrange() + d);
			EX.assertx(mb.getOrange() >= 0);
		}
		else if(x == 'R')
		{
			EX.assertx(mb.getRed() >= 0);
			mb.setRed(mb.getRed() + d);
			EX.assertx(mb.getRed() >= 0);
		}
	}
}