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

	//~: return the web links
	return getUserLinks(login)
}

function getUserLinks(login)
{
	response.setContentType('application/json')
	print(login.getUserLinks())
}