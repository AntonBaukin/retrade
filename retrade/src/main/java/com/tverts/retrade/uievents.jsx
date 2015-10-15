var ZeT    = JsX.once('zet/app.js')
var ZeTS   = JsX.once('zet/strings.js')
var getMsg = ZeT.bean('GetMsg')
var config = ZeT.bean('SystemConfig')

function get()
{
	response.setContentType('text/javascript;charset=UTF-8')

	//sec: also, secure check
	var ctx       = {}
	ctx.msgBoxObj = getMsg.msgBox()
	ctx.msgBox    = ctx.msgBoxObj.getOx()
	ctx.noids     = ZeT.jarray(java.lang.Long, 2)

	//~: the parameters
	ctx.task  = ZeTS.trim(params.task)
	ctx.query = ZeTS.trim(params.query)

	//~: decode the query
	decodeQuery(ctx)

	//~: and dispatch the task
	dispatchTask(ctx)
}

function getNumbers(ctx)
{
	return {
		txn: ctx.msgBoxObj.getTxn(),
		numbers: {
			N: ctx.msgBox.getTotal(),
			R: ctx.msgBox.getRed(),
			G: ctx.msgBox.getGreen(),
			O: ctx.msgBox.getOrange()
		}
	}
}

function taskNumbers(ctx)
{
	print('ReTrade.desktop.uievents.numbers(', ZeT.o2s(getNumbers(ctx)), ')')
}

function taskColor(ctx)
{
	ZeT.asserts(ctx.query)
	var codes = ZeT.SU.s2aws(ctx.query)
	ZeT.assert(codes.length && (codes.length%2 == 0))

	//~: decode the colors from the query
	var colors = new java.util.HashMap()
	for(var i = 0;(i < codes.length);i+=2)
		colors.put(java.lang.Long.parseLong(codes[i]), color(codes[i+1]))

	//~: assign the colors
	getMsg.setColors(ctx.msgBoxObj, colors)

	//~: print the numbers
	taskNumbers(ctx)
}

function taskFetch(ctx)
{
	ZeT.assert((ctx.query == 'older') || (ctx.query == 'newer'))
	var pages = (ctx.query == 'older')?(+2):(-2)

	ctx.messages = getMsg.loadMsgs(
	  ctx.msgBoxObj.getPrimaryKey(),
	  ctx.noids, ctx.id, pages, color(ctx.color)
	)
}

function taskDelete(ctx)
{
	//~: decode the ids
	ZeT.asserts(ctx.query)
	var codes = ZeT.SU.s2aws(ctx.query)
	ZeT.assert(codes.length > 0)
	var ids   = new java.util.HashSet()
	ZeT.each(codes, function(c)
	{
		ids.add(java.lang.Long.parseLong(c))
	})

	//~: remove the items
	getMsg.remove(ctx.msgBoxObj, ids)

	//~: select the messages around
	ctx.messages = getMsg.loadMsgs(
	  ctx.msgBoxObj.getPrimaryKey(),
	  ctx.noids, ctx.id, 0, color(ctx.color)
	)

	//~: find first position
	ctx.first = closest(ctx, ids)
}

function taskFilter(ctx)
{
	//?: {neutral} select the newest message
	if(ctx.color == 'N') ctx.id = null

	//~: select the messages around
	ctx.messages = getMsg.loadMsgs(
	  ctx.msgBoxObj.getPrimaryKey(),
	  ctx.noids, ctx.id, 0, color(ctx.color)
	)

	//~: find first position
	ctx.first = closest(ctx, null)
}

function taskDefault(ctx)
{
	ctx.messages = getMsg.loadMsgs(
	  ctx.msgBoxObj.getPrimaryKey(),
	  ctx.noids, null, 0, 'N'
	)
}

function dispatchTask(ctx)
{
	if(ctx.task == 'numbers')
		taskNumbers(ctx)
	else if(ctx.task == 'color')
		taskColor(ctx)
	else if(ctx.task == 'fetch')
		taskFetch(ctx)
	else if(ctx.task == 'delete')
		taskDelete(ctx)
	else if(ctx.task == 'filter')
		taskFilter(ctx)
	else
		taskDefault(ctx)

	if(ctx.messages)
		printMessages(ctx)
}

var MsgAdapters = Java.type('com.tverts.endure.msg.MsgAdapters')

function printMessages(ctx)
{
	//~: reset first id
	if(ctx.id && (ctx.id == ctx.first))
		ctx.first = null

	//~: resulting object
	var result = ZeT.extend(getNumbers(ctx),
	{
		firstid: ctx.first, filter: ctx.color,
		newer: ctx.noids[0], older: ctx.noids[1],
		items: []
	})

	var cl  = java.util.Calendar.getInstance()
	var now = new java.util.Date()
	var sb  = new java.lang.StringBuilder()

	//~: add each message
	ZeT.each(ctx.messages, function(msg)
	{
		var m = msg.getOx()

		//~: the time of the message
		cl.setTime(ZeT.assertn(m.getTime()))

		//~: time
		sb.delete(0, sb.length())
		ZeT.DU.time2str(sb, cl)
		var t = sb.toString()

		//~: date
		sb.delete(0, sb.length())
		ZeT.DU.namedDateDiffStrRu(m.getTime(), sb, now, cl)
		var d = sb.toString()

		//~: message text
		var x = ZeT.jss(m.getTitle())

		//~: message script
		var s = MsgAdapters.msgScript(m)
		if(ZeTS.ises(s)) s = null; else s = ZeT.jss(s)

		result.items.push({
			id: msg.getPrimaryKey(), time: t, date: d,
			color: '' + m.getColor(), text: x, script: s
		})
	})

	print('ReTrade.desktop.uievents.set(', ZeT.o2s(result), ')')
}

function decodeQuery(ctx)
{
	if(ZeTS.ises(ctx.query) || !ctx.query.startsWith('>'))
		return

	//~: >ID
	var i = ctx.query.indexOf(' ')
	ZeT.assert(i > 0)

	var x = ctx.query.substring(1, i)
	ZeT.asserts(x)

	//?: {id != ?}
	if(x != '?')
	{
		ctx.id = parseInt(x)
		ZeT.assert(ZeT.isi(ctx.id) && (ctx.id > 0))
	}

	//~: >ID C;
	var j = ctx.query.indexOf(';', i)
	ZeT.assert((j > 0) && (i + 2 <= j))
	x = ctx.query.substring(i + 1, j)
	if(!ZeTS.ises(x))
		ctx.color = ZeTS.trim(x)

	//~: the rest of the query
	ctx.query = ZeTS.trim(ctx.query.substring(j + 1))
}

function closest(ctx, ids)
{
	//?: {has no messages}
	if(ctx.messages.isEmpty())
		return null

	//~: search for the context id is in the messages
	var found = false
	if(ctx.id) ZeT.each(ctx.messages, function(m)
	{
		if(ctx.id == m.getPrimaryKey())
			return !(found = true) //<-- break
	})

	//?: {given id is in the list}
	if(found) return ctx.id

	//~: ids max
	var M = null; if(ids) ZeT.each(ids, function(id)
	{
		M = (M == null)?(id):Math.max(M, id)
	})

	//~: search for the closest page start
	var r = null, D = null, P = config.getUserEventsPage()
	if((ctx.id != null) || (M != null))
		for(var i = 0;(i < ctx.messages.size());i += P)
		{
			var x = ctx.messages[i].getPrimaryKey()
			var d = Math.abs(x - ((ctx.id != null)?(ctx.id):(M)))
			if(D == null || (d < D)) { D = d; r = x }
		}

	return r
}

function color(c)
{
	c = '' + c
	ZeT.asserts(c)
	c = c.toUpperCase()
	ZeT.assert(color.COLORS.indexOf(c) >= 0)
	return new java.lang.Character(c.charAt(0))
}

color.COLORS = [ "N", "R", "G", "O" ]
