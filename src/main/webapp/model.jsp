<%@page contentType = 'application/xml;charset = UTF-8' trimDirectiveWhitespaces = 'true'%>

<%@page import = 'com.tverts.model.ModelAccessPoint'%>
<%@page import = 'com.tverts.model.ModelBean'%>
<%@page import = 'com.tverts.model.ModelPoint'%>
<%@page import = 'com.tverts.objects.XMAPoint'%>
<%@page import = 'static com.tverts.support.SU.s2s'%>


<%

ModelPoint point = ModelAccessPoint.model();
String     param = s2s(request.getParameter("model"));
Object     model = (param == null)?(null):(point.readBean(param));


//?: {model bean implements main interface} access data bean
if(model instanceof ModelBean)
  model = ((ModelBean)model).modelData();

//?: {no model bean provided}
if(model == null)
{
  if(param == null)
    response.sendError(404, "No model bean was specified!");
  else
    response.sendError(404, "Specified model bean was not found!");

  return;
}

//!: do XML mapping
XMAPoint.writeObject(model, out);

%>