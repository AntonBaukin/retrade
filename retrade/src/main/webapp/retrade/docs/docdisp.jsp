<%@page trimDirectiveWhitespaces = 'true'%>

<%@page import = 'com.tverts.endure.Unity'%>
<%@page import = 'com.tverts.endure.UnityTypes'%>
<%@page import = 'com.tverts.endure.core.GetUnity'%>
<%@page import = 'com.tverts.faces.ModelView'%>
<%@page import = 'com.tverts.retrade.domain.invoice.Invoices'%>
<%@page import = 'com.tverts.retrade.domain.sells.Sells'%>
<%@page import = 'com.tverts.servlet.RequestPoint'%>
<%@page import = 'static com.tverts.spring.SpringPoint.bean'%>
<%@page import = 'static com.tverts.support.SU.s2s'%>


<%

String param  = s2s(request.getParameter(ModelView.ENTITY_PARAM));
if(param == null) throw new IllegalArgumentException(
  "No document parameter is defined!");

Unity  object = bean(GetUnity.class).getUnity(Long.parseLong(param));
if(object == null)
{
  response.sendError(404, String.format(
    "Object with key [%s] was not found!", param
  ));

  return;
}

String redirp = null;

//?: {it is a buy invoice}
if(Invoices.isBuyInvoice(object))
  redirp = "/go/retrade/invoices/info-buy";

//?: {it is a sell invoice}
if(Invoices.isSellInvoice(object))
  redirp = "/go/retrade/invoices/info-sell";

//?: {it is a move invoice}
if(Invoices.isMoveInvoice(object))
  redirp = "/go/retrade/invoices/info-move";

//?: {it is a sells invoice}
if(Sells.isSellsInvoice(object))
  redirp = "/go/retrade/sells/invoice-info";

  //?: {it is a sells session}
if(Sells.isSellsSession(object))
  redirp = "/go/retrade/sells/session-info";

//?: {it is a volume check document}
if(Invoices.isVolumeCheck(object))
  redirp = "/go/retrade/invoices/info-volume-check";

//?: {this type is not supported}
if(redirp == null)
{
  response.sendError(404, String.format(
    "No page was found for object %s!", object.toString()
  ));
  return;
}

response.sendRedirect(RequestPoint.formAbsoluteURL(redirp, true));

%>