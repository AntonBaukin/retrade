/*===============================================================+
 |               JSON Provider for User Web Links                |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ZeT  = JsX.once('zet/app.js')
var ZeTS = JsX.once('zet/strings.js')

function get()
{
	//~: get login of current user
	var login = ZeT.sec.loadLogin()

	//?: {get tx-number}
	if(params.task == 'txn')
		return getUserTxn(login)
	//?: {set color}
	else if(params.task == 'color')
		return updateModel(login, actionColor)
	//?: {remove}
	else if(params.task == 'remove')
		return updateModel(login, actionRemove)
	//?: {move}
	else if(params.task == 'move')
		return updateModel(login, actionMove)

	//~: return the web links
	return getUserLinks(login)
}

function getUserLinks(login)
{
	response.setContentType('application/json')
	print(login.getUserLinks())
}

function getUserTxn(login)
{
	response.setContentType('text/plain')
	print(login.getTxn())
}

function updateModel(login, action)
{
	//~: decode the model object
	var model = ZeT.s2o(login.getUserLinks())
	ZeT.assert(ZeT.isa(model))

	//~: invoke the action on the model
	action(model)

	//~: save the model
	login.setUserLinks(ZeT.o2s(model))

	//!: update the tx-number of the login
	ZeT.tx.txn(login)

	//~: and write it back
	response.setContentType('text/plain')
	print(login.getTxn())
}

function searchRecord(model)
{
	ZeT.assert(ZeT.isa(model))
	ZeT.asserts(params.item)

	for(var i = 0;(i < model.length);i++)
		if(model[i].id === params.item)
			return model[i]
}

function searchRecords(model, ids)
{
	ZeT.assert(ZeT.isa(model))
	ZeT.asserta(ids)

	var r = [], m = {}
	ZeT.each(ids, function(x, i){ m[ZeT.asserts(x)] = i })

	for(var i = 0;(i < model.length);i++)
		if(ZeT.isn(m[model[i].id]))
			r.push(model[i])

	r.sort(function(a, b){ return m[a.id] - m[b.id] })
	return r
}

function actionColor(model)
{
	//~: search for the record
	var rec = ZeT.assertn(searchRecord(model))

	//~: check the color
	var color = ZeT.asserts(params.color)
	ZeT.assert([ 'N', 'R', 'G', 'O' ].indexOf(color) >= 0)

	//=: set the color of the record
	rec.color = color
}

function actionRemove(model)
{
	//~: search for the record
	var rec = ZeT.assertn(searchRecord(model))

	//?: {found} remove it
	if(rec) ZeTA.del(model, rec)
}

function actionMove(model)
{
	//~: search for the record
	var rec = ZeT.assertn(searchRecord(model))

	//~: find the models to move
	ZeT.asserts(params.moved)
	var moved = searchRecords(model, params.moved.split(';'))
	ZeT.assert(moved.indexOf(rec) == -1)

	//~: take them out
	ZeTA.del.apply(model, moved)

	//~: index to insert
	var i = model.indexOf(rec)
	ZeT.assert(i >= 0)

	//~: insert back
	if(i == model.length - 1)
		model.push.apply(model, moved)
	else
	{
		var x = ZeTA.concat([i, 0], moved)
		model.splice.apply(model, x)
	}
}