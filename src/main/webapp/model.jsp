<%@page trimDirectiveWhitespaces = 'true'%>

<%@page import = 'com.tverts.model.ModelAccessPoint'%>
<%@page import = 'com.tverts.model.ModelBean'%>
<%@page import = 'com.tverts.model.ModelPoint'%>
<%@page import = 'com.tverts.objects.XMAPoint'%>
<%@page import = 'static com.tverts.support.SU.s2s'%>
<%@page import = 'com.tverts.support.streams.StringBuilderWriter'%>


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
try
{
  StringBuilderWriter sw = new StringBuilderWriter(4096);

  XMAPoint.writeObject(model, sw);
  sw.close();

  response.setContentType("application/xml;charset=UTF-8");
  out.write(sw.buffer().toString());
}
catch(Exception e)
{
  response.setStatus(500);
  throw new ServletException(e);
}

%>